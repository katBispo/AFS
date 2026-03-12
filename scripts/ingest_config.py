#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Ingestor de configuração a partir de CSV/XLSX (RelatorioAFS.*) para MySQL (schema da aplicação Spring/JPA).

- Lê dados com colunas: Componente | Propriedade | Valor | (opcionais) Amostragem | Intervalo de amostragem | ...
- Detecta e ignora preâmbulo no início (linhas como "Projeto", "Monitorar", data/hora).
- Localiza a linha "Arquivo de Configuração" -> parseia o texto (!Current Configuration, interfaces, ip address, vlan, rotas, snmp etc.)
- Localiza linhas "Assinatura de Configuração", "Estado da Configuração", "Temperatura",
  e no grupo de "Protocolo Ping" a "Tempo Máximo de Resposta" (e tenta capturar "Intervalo de amostragem")
- Persiste em MySQL: equipamento, vlan, interface_rede, ip_interface, rota, configuracao_equipamento,
  usuario_equipamento, temperatura_leitura, teste_rede

Uso (Windows / cmd, aceitando CSV OU XLSX):
  python ingest_config.py --csv "C:\\Users\\SAT\\Documents\\AfsRelatorio\\scripts\\RelatorioAFS.xlsx" --sheet "Planilha1" --mysql-host localhost --mysql-db relatorioAfs --mysql-user root --mysql-pass "SUA_SENHA"

Requisitos:
  pip install pandas openpyxl python-dateutil mysql-connector-python
"""

import csv
import re
import argparse
import sys
from datetime import datetime, timezone
from dateutil import parser as dateparser
import mysql.connector as mysql
from typing import Optional, Dict, Any, List
from pathlib import Path

import re
from collections import defaultdict


# ------------------------------
# Helpers de rede / parsing
# ------------------------------

def dotted_mask_to_prefix(mask: str) -> Optional[int]:
    try:
        parts = [int(p) for p in mask.strip().split('.')]
        bits = ''.join(f"{p:08b}" for p in parts)
        return bits.count('1')
    except Exception:
        return None

def pick_interface_type_by_name(nome: Optional[str]) -> str:
    if not nome:
        return 'OUTRO'
    s = nome.upper()
    if 'VOIP' in s:
        return 'VOIP'
    if 'TELEM' in s:
        return 'TELEM'
    if 'VITAL' in s:
        return 'VITAL'
    if 'GER' in s or 'GERENCIA' in s:
        return 'GER'
    return 'OUTRO'

def to_bool_int(v: bool) -> int:
    return 1 if v else 0

def parse_datetime_loose(s: str) -> Optional[datetime]:
    try:
        dt = dateparser.parse(s, dayfirst=False, yearfirst=False, fuzzy=True)
        if dt and not dt.tzinfo:
            dt = dt.replace(tzinfo=timezone.utc)
        return dt
    except Exception:
        return None

# ------------------------------
# Parsing do bloco de configuração (igual ao do .txt)
# ------------------------------

def parse_config_text(raw: str) -> Dict[str, Any]:
    modelo = None
    m_modelo = re.search(r'!System Description\s+"?([^"\r\n]+)"?', raw)
    if m_modelo:
        modelo = m_modelo.group(1).strip()

    versao = None
    m_versao = re.search(r'!System Version\s+([^\r\n]+)', raw)
    if m_versao:
        versao = m_versao.group(1).strip()

    descricao = None
    m_banner = re.search(r'set pre-login-banner text\s+"([^"]+)"', raw)
    if m_banner:
        descricao = m_banner.group(1).replace('\\n', '\n')

    nome = None
    m_sysname = re.search(r'snmp-server\s+sysname\s+"?\s*([^"\r\n]+)\s*"?', raw)
    if m_sysname:
        nome = m_sysname.group(1).strip()

    localizacao = None
    m_loc = re.search(r'snmp-server\s+location\s+"([^"]+)"', raw)
    if m_loc:
        localizacao = m_loc.group(1).strip()

    fabricante = None
    m_contact = re.search(r'snmp-server\s+contact\s+"([^"]+)"', raw)
    if m_contact:
        fabricante = m_contact.group(1).strip()

    # VLANs
    vlans: List[int] = []
    for vm in re.finditer(r'^\s*vlan\s+(\d{1,4})\s*$', raw, re.MULTILINE):
        try:
            vlanid = int(vm.group(1))
            if 1 <= vlanid <= 4094 and vlanid not in vlans:
                vlans.append(vlanid)
        except:
            pass

    # Interfaces
    interfaces = []
    for im in re.finditer(r'^\s*interface\s+(\d+/\d+)\s*(.*?)^\s*exit\s*$', raw, re.MULTILINE | re.DOTALL):
        porta = im.group(1).strip()
        body = im.group(2)

        nome_iface = None
        m_iname = re.search(r'^\s*name\s+"([^"]+)"', body, re.MULTILINE)
        if m_iname:
            nome_iface = m_iname.group(1).strip()

        auto_neg = True
        if re.search(r'^\s*no\s+auto-negotiate', body, re.MULTILINE):
            auto_neg = False

        stp = True
        if re.search(r'^\s*no\s+spanning-tree\s+port\s+mode', body, re.MULTILINE):
            stp = False

        velocidade = None
        duplex = None
        m_speed = re.search(r'^\s*speed\s+(\d+)\s+(full-duplex|half-duplex)', body, re.MULTILINE | re.IGNORECASE)
        if m_speed:
            try:
                velocidade = int(m_speed.group(1))
            except:
                velocidade = None
            d = m_speed.group(2).upper()
            duplex = 'FULL' if 'FULL' in d else 'HALF'

        ips = []
        for ipm in re.finditer(r'^\s*ip\s+address\s+(\d{1,3}(?:\.\d{1,3}){3})\s+(\d{1,3}(?:\.\d{1,3}){3})', body, re.MULTILINE):
            ip = ipm.group(1)
            mask = ipm.group(2)
            prefix = dotted_mask_to_prefix(mask)
            ips.append({"ip": ip, "prefixo": prefix})

        interfaces.append({
            "porta": porta,
            "nome": nome_iface,
            "velocidade_mbps": velocidade,
            "duplex": duplex,
            "auto_negociacao": auto_neg,
            "spanning_tree": stp,
            "tipo": pick_interface_type_by_name(nome_iface),
            "ips": ips
        })

    # Rotas
    rotas = []
    for rm in re.finditer(r'^\s*ip\s+route\s+(\d{1,3}(?:\.\d{1,3}){3})\s+(\d{1,3}(?:\.\d{1,3}){3})\s+(\d{1,3}(?:\.\d{1,3}){3})', raw, re.MULTILINE):
        destino = rm.group(1)
        mask = rm.group(2)
        gw = rm.group(3)
        pf = dotted_mask_to_prefix(mask) or 0
        rotas.append({"destino": destino, "prefixo": pf, "gateway": gw, "tipo": "ESTATICA"})

    # Usuários SNMPv3
    usuarios = []
    for um in re.finditer(r'^\s*users\s+snmpv3\s+authentication\s+(\S+)\s+(md5|sha|sha256|sha512)\s*$', raw, re.MULTILINE | re.IGNORECASE):
        user = um.group(1)
        auth = um.group(2).upper()
        usuarios.append({"usuario": user, "snmpv3_auth": auth, "snmpv3_crypto": None})
    crypto_map = {}
    for cm in re.finditer(r'^\s*users\s+snmpv3\s+encryption\s+(\S+)\s+(des|aes128|aes192|aes256)\b', raw, re.MULTILINE | re.IGNORECASE):
        crypto_map[cm.group(1)] = cm.group(2).upper()
    for u in usuarios:
        if u["usuario"] in crypto_map:
            u["snmpv3_crypto"] = crypto_map[u["usuario"]]

    # ip principal (heurística)
    ip_principal = None
    for iface in interfaces:
        for ipd in iface["ips"]:
            if ipd["ip"] == "10.235.0.253":
                ip_principal = ipd["ip"]
                break
        if ip_principal:
            break
    if not ip_principal:
        for iface in interfaces:
            if iface["ips"]:
                ip_principal = iface["ips"][0]["ip"]
                break

    equipamento = {
        "nome": nome or "SEM_NOME",
        "modelo": modelo,
        "versao_sistema": versao,
        "fabricante": fabricante,
        "localizacao": localizacao,
        "ip_principal": ip_principal,
        "descricao": descricao,
        "status": "OK"
    }

    return {
        "equipamento": equipamento,
        "vlans": vlans,
        "interfaces": interfaces,
        "rotas": rotas,
        "usuarios": usuarios
    }

# ------------------------------
# Leitura de arquivos (CSV ou XLSX)
# ------------------------------

def read_xlsx_rows(path: str, sheet_name: Optional[str] = None) -> List[Dict[str, str]]:
    """
    Lê um XLSX/XLS com:
    - preâmbulo nas 3 primeiras linhas (Projeto/Monitorar/Data),
    - cabeçalho real a partir de 'Componente | Propriedade | Valor | ...',
    - preserva multiline em 'Valor'.
    Retorna lista de dicionários (colunas como chaves), como o DictReader.
    """
    import pandas as pd  # import local

    # Se não informaram sheet_name, usa a PRIMEIRA ABA (índice 0)
    target_sheet = 0 if sheet_name in (None, '', 'None') else sheet_name

    # 1) Ler sem cabeçalho para detectar a linha do cabeçalho
    df0 = pd.read_excel(path, sheet_name=target_sheet, header=None, dtype=str, engine='openpyxl')

    # Quando sheet_name for None em versões que retornam dict
    if isinstance(df0, dict):
        first_key = next(iter(df0.keys()))
        df0 = df0[first_key]

    df0 = df0.fillna('')

    header_row = None
    wanted = ('componente', 'propriedade', 'valor')
    max_scan = min(30, len(df0))

    for i in range(max_scan):
        row_vals = df0.iloc[i].astype(str).str.strip().str.lower().tolist()
        if any('componente' in c for c in row_vals) and any('propriedade' in c for c in row_vals) and any('valor' in c for c in row_vals):
            header_row = i
            break

    if header_row is None:
        # Fallback usual
        header_row = 3

    # 2) Relê já com o header detectado
    df = pd.read_excel(path, sheet_name=target_sheet, header=header_row, dtype=str, engine='openpyxl')
    if isinstance(df, dict):
        first_key = next(iter(df.keys()))
        df = df[first_key]

    df = df.fillna('')
    df.columns = [str(c).strip() for c in df.columns]

    # Remove linhas completamente vazias
    df = df[~(df.apply(lambda r: all((str(v).strip() == '') for v in r), axis=1))]

    # Converte para list[dict]
    records: List[Dict[str, str]] = []
    for row in df.to_dict(orient='records'):
        clean = {}
        for k, v in row.items():
            ks = str(k).strip() if k is not None else k
            vs = str(v).strip() if isinstance(v, str) else v
            clean[ks] = vs
        records.append(clean)
    return records


def read_csv_rows(path: str) -> List[Dict[str, str]]:
    """
    Leitor CSV robusto:
    - tenta encodings comuns,
    - detecta linha do cabeçalho (pula preâmbulo),
    - preserva multiline; separador ';' (típico pt-BR).
    """
    import io

    encodings = ('utf-8', 'utf-8-sig', 'cp1252', 'latin-1', 'utf-16', 'utf-16-le', 'utf-16-be')
    last_err = None

    for enc in encodings:
        try:
            with open(path, 'r', encoding=enc, newline='') as f:
                text = f.read()

            lines = text.splitlines()
            # Detecta linha de cabeçalho contendo as 3 colunas-chave
            start_idx = 0
            wanted = ('componente', 'propriedade', 'valor')
            for i, ln in enumerate(lines[:30]):
                ll = (ln or '').lower()
                if all(w in ll for w in wanted):
                    start_idx = i
                    break
            data_text = '\n'.join(lines[start_idx:])

            # Lê com DictReader preservando multiline; separador ';'
            reader = csv.DictReader(io.StringIO(data_text), delimiter=';', quotechar='"')

            rows: List[Dict[str, str]] = []
            for row in reader:
                clean = {}
                for k, v in row.items():
                    ks = k.strip() if isinstance(k, str) else k
                    vs = v.strip() if isinstance(v, str) else v
                    clean[ks] = vs
                rows.append(clean)

            # Sanity check
            headers = list(rows[0].keys()) if rows else []
            def has(hs, name):
                return any(isinstance(h, str) and name.lower() in h.lower() for h in hs)
            if not (has(headers, 'Componente') and has(headers, 'Propriedade') and has(headers, 'Valor')):
                last_err = RuntimeError(f'Cabeçalho não detectado. Colunas vistas: {headers}')
                continue

            return rows
        except Exception as ex:
            last_err = ex
            continue

    raise RuntimeError(f'Não foi possível ler o CSV {path}. Último erro: {last_err}')


def read_rows_any(path: str, sheet_name: Optional[str] = None) -> List[Dict[str, str]]:
    """
    Despacha para o leitor correto conforme a extensão:
    - .xlsx / .xls -> Excel
    - caso contrário -> CSV
    """
    ext = Path(path).suffix.lower()
    if ext in ('.xlsx', '.xls'):
        return read_xlsx_rows(path, sheet_name=sheet_name)
    else:
        return read_csv_rows(path)

# ------------------------------
# Utilidades de cabeçalhos e extração
# ------------------------------

def find_col(headers: List[str], *names) -> Optional[str]:
    lower = {h.lower(): h for h in headers}
    for n in names:
        if n.lower() in lower:
            return lower[n.lower()]
    # tenta startswith / contains heurístico
    for h in headers:
        hl = h.lower()
        for n in names:
            if n.lower() in hl:
                return h
    return None




def group_rows_by_ip(rows: List[Dict[str, str]]) -> Dict[str, List[Dict[str, str]]]:
    """
    Agrupa as linhas por IP do campo 'Componente'.
    Retorna: { '10.0.0.1': [linhas...], '192.168.1.10': [linhas...], ... }
    """
    if not rows:
        raise RuntimeError('CSV/Planilha sem linhas.')

    headers = list(rows[0].keys())
    col_comp = find_col(headers, 'Componente')
    if not col_comp:
        raise RuntimeError('Não encontrei a coluna "Componente".')

    por_ip: Dict[str, List[Dict[str, str]]] = defaultdict(list)
    for r in rows:
        comp = r.get(col_comp) or ''
        m = re.match(r'(\d{1,3}(?:\.\d{1,3}){3})', comp)
        if not m:
            # linha sem IP no início do Componente (ignora)
            continue
        ip = m.group(1)
        por_ip[ip].append(r)

    return dict(por_ip)


def extract_from_csv_block(rows: List[Dict[str, str]]) -> Dict[str, Any]:
    """
    Extrai dados de UM BLOCO (um IP).
    Se não houver 'Arquivo de Configuração', retorna dados 'parciais':
      - equipamento (ip_principal + nome, se existir)
      - temperatura, ping_tmax/intervalo (se existirem)
      - e deixa listas (vlans, interfaces, rotas, usuarios) vazias
    """
    if not rows:
        raise RuntimeError('Bloco vazio.')

    headers = list(rows[0].keys())

    col_comp = find_col(headers, 'Componente')
    col_prop = find_col(headers, 'Propriedade')
    col_val  = find_col(headers, 'Valor')
    col_amost = find_col(headers, 'Amostragem', 'Amostra', 'Amostragem/Registro')
    col_intervalo = find_col(headers, 'Intervalo de amostragem', 'Intervalo', 'Intervalo (amostragem)')

    if not (col_comp and col_prop and col_val):
        raise RuntimeError(f'Bloco não contém colunas esperadas. Achei: {headers}')

    # dados de alto nível
    config_text = None
    assinatura = None
    estado_conf = None
    temperatura = None
    ping_tmax_ms = None
    ping_intervalo_s = None
    data_coleta = None
    nome_do_bloco = None  # “Nome” (ex.: ARM KM186) que aparece como Propriedade no bloco

    # tenta achar uma data em qualquer célula
    for r in rows[:10]:
        for v in r.values():
            if isinstance(v, str):
                dt = parse_datetime_loose(v)
                if dt:
                    data_coleta = dt
                    break
        if data_coleta:
            break
    if not data_coleta:
        data_coleta = datetime.now(timezone.utc)

    # IP do componente (obrigatório para esse bloco)
    ip_component = None

    # Varre linhas do bloco
    for r in rows:
        comp = r.get(col_comp) or ''
        prop = (r.get(col_prop) or '').strip()
        val  = r.get(col_val)

        # IP do componente (primeiro IP do bloco)
        if not ip_component:
            mip = re.search(r'(\d{1,3}(?:\.\d{1,3}){3})', comp)
            if mip:
                ip_component = mip.group(1)

        # Nome (quando a planilha traz “Propriedade = Nome”)
        if prop.lower() == 'nome' and val:
            nome_do_bloco = val.strip()

        # Arquivo de Configuração -> texto multilinha
        if prop.lower() == 'arquivo de configuração' and val:
            config_text = val

        # Assinatura / Estado da Configuração
        if 'assinatura de configuração' in prop.lower() and val:
            assinatura = val.strip()
        if 'estado da configuração' in prop.lower() and val:
            estado_conf = val.strip().upper()

        # Temperatura
        if prop.lower() == 'temperatura' and val:
            m = re.search(r'(\d{1,3})', val)
            if m:
                temperatura = int(m.group(1))

        # PING (componente contém "... Protocolo Ping")
        if 'protocolo ping' in comp.lower() and ('tempo máximo de resposta' in prop.lower()) and val:
            m = re.search(r'(\d+)', val)
            if m:
                ping_tmax_ms = int(m.group(1))
            # tenta achar intervalo nessa mesma linha
            if col_intervalo:
                ival = r.get(col_intervalo)
                if ival:
                    m2 = re.search(r'(\d+)\s*seg', ival.lower())
                    if m2:
                        ping_intervalo_s = int(m2.group(1))

    # Equipamento base
    equipamento = {
        'nome': nome_do_bloco or 'SEM_NOME',
        'modelo': None,
        'versao_sistema': None,
        'fabricante': None,
        'localizacao': None,
        'ip_principal': ip_component,  # CHAVE do bloco!
        'descricao': None,
        'status': 'OK'
    }

    vlans: List[int] = []
    interfaces: List[Dict[str, Any]] = []
    rotas: List[Dict[str, Any]] = []
    usuarios: List[Dict[str, Any]] = []

    # Se houver arquivo de configuração, parseie
    if config_text:
        parsed = parse_config_text(config_text)

        # Se no texto o ip_principal não existir, usa o ip do bloco
        if ip_component and not parsed['equipamento']['ip_principal']:
            parsed['equipamento']['ip_principal'] = ip_component

        # Sobrescreve equipamento com o que veio do config (mantendo nome_do_bloco se houver)
        equipamento.update(parsed['equipamento'])
        if nome_do_bloco:
            equipamento['nome'] = nome_do_bloco

        vlans = parsed['vlans']
        interfaces = parsed['interfaces']
        rotas = parsed['rotas']
        usuarios = parsed['usuarios']

    config_meta = {
        'conteudo': config_text,                      # pode ser None
        'assinatura': assinatura,
        'estado': (estado_conf or 'SALVO').upper(),
        'data_coleta': data_coleta
    }

    monitor = {
        'temperatura_c': temperatura,
        'ping_tempo_max_ms': ping_tmax_ms,
        'ping_intervalo_s': ping_intervalo_s
    }

    return {
        'equipamento': equipamento,
        'vlans': vlans,
        'interfaces': interfaces,
        'rotas': rotas,
        'usuarios': usuarios,
        'config': config_meta,
        'monitor': monitor
    }

# ------------------------------
# Persistência MySQL
# ------------------------------

def connect_mysql(host, db, user, pwd):
    return mysql.connect(host=host, database=db, user=user, password=pwd, autocommit=False)


def upsert_equipamento(cur, e):
    if e['ip_principal']:
        cur.execute('SELECT id FROM equipamento WHERE ip_principal=%s', (e['ip_principal'],))
        row = cur.fetchone()
        if row:
            eid = row[0]
            cur.execute(
                """
                UPDATE equipamento SET nome=%s, modelo=%s, versao_sistema=%s, fabricante=%s,
                    localizacao=%s, descricao=%s, status=%s, updated_at=NOW()
                WHERE id=%s
                """,
                (e['nome'], e['modelo'], e['versao_sistema'], e['fabricante'], e['localizacao'],
                 e['descricao'], e['status'], eid)
            )
            return eid

    if e['nome']:
        cur.execute('SELECT id FROM equipamento WHERE nome=%s', (e['nome'],))
        row = cur.fetchone()
        if row:
            eid = row[0]
            cur.execute(
                """
                UPDATE equipamento SET ip_principal=%s, modelo=%s, versao_sistema=%s, fabricante=%s,
                    localizacao=%s, descricao=%s, status=%s, updated_at=NOW()
                WHERE id=%s
                """,
                (e['ip_principal'], e['modelo'], e['versao_sistema'], e['fabricante'], e['localizacao'],
                 e['descricao'], e['status'], eid)
            )
            return eid

    cur.execute(
        """
        INSERT INTO equipamento (nome, modelo, versao_sistema, fabricante, localizacao, ip_principal, descricao, status, created_at, updated_at)
        VALUES (%s,%s,%s,%s,%s,%s,%s,%s, NOW(), NOW())
        """,
        (e['nome'], e['modelo'], e['versao_sistema'], e['fabricante'], e['localizacao'], e['ip_principal'], e['descricao'], e['status'])
    )
    return cur.lastrowid


def upsert_vlan(cur, equipamento_id, numero):
    cur.execute('SELECT id FROM vlan WHERE equipamento_id=%s AND numero=%s', (equipamento_id, numero))
    r = cur.fetchone()
    if r:
        return r[0]
    cur.execute(
        """
        INSERT INTO vlan (equipamento_id, numero, created_at, updated_at)
        VALUES (%s,%s,NOW(),NOW())
        """,
        (equipamento_id, numero)
    )
    return cur.lastrowid


def upsert_interface(cur, equipamento_id, iface):
    cur.execute('SELECT id FROM interface_rede WHERE equipamento_id=%s AND porta=%s', (equipamento_id, iface['porta']))
    r = cur.fetchone()
    if r:
        iid = r[0]
        cur.execute(
            """
           UPDATE interface_rede
           SET nome=%s, velocidade_mbps=%s, duplex=%s, auto_negociacao=%s, spanning_tree=%s, tipo=%s, updated_at=NOW()
           WHERE id=%s
            """,
            (iface['nome'], iface['velocidade_mbps'], iface['duplex'], to_bool_int(iface['auto_negociacao']),
             to_bool_int(iface['spanning_tree']), iface['tipo'], iid)
        )
        return iid
    cur.execute(
        """
        INSERT INTO interface_rede (equipamento_id, porta, nome, velocidade_mbps, duplex, auto_negociacao, spanning_tree, tipo, created_at, updated_at)
        VALUES (%s,%s,%s,%s,%s,%s,%s,%s,NOW(),NOW())
        """,
        (equipamento_id, iface['porta'], iface['nome'], iface['velocidade_mbps'], iface['duplex'],
         to_bool_int(iface['auto_negociacao']), to_bool_int(iface['spanning_tree']), iface['tipo'])
    )
    return cur.lastrowid


def upsert_ip_interface(cur, interface_id, ip, prefixo):
    cur.execute('SELECT id FROM ip_interface WHERE interface_id=%s AND ip=%s AND prefixo=%s', (interface_id, ip, prefixo))
    r = cur.fetchone()
    if r:
        return r[0]
    cur.execute(
        """
        INSERT INTO ip_interface (interface_id, ip, prefixo, principal, created_at, updated_at)
        VALUES (%s,%s,%s,%s, NOW(), NOW())
        """,
        (interface_id, ip, prefixo, 1)
    )
    return cur.lastrowid


def upsert_rota(cur, equipamento_id, destino, prefixo, gateway, tipo='ESTATICA'):
    cur.execute(
        'SELECT id FROM rota WHERE equipamento_id=%s AND destino=%s AND prefixo=%s AND gateway=%s',
        (equipamento_id, destino, prefixo, gateway)
    )
    r = cur.fetchone()
    if r:
        return r[0]
    cur.execute(
        """
        INSERT INTO rota (equipamento_id, destino, prefixo, gateway, tipo, created_at, updated_at)
        VALUES (%s,%s,%s,%s,%s, NOW(), NOW())
        """,
        (equipamento_id, destino, prefixo, gateway, tipo)
    )
    return cur.lastrowid


def insert_config(cur, equipamento_id, conteudo, assinatura, estado, data_coleta):
    # Garante datetime sem tz para MySQL
    if isinstance(data_coleta, datetime) and data_coleta.tzinfo is not None:
        data_coleta = data_coleta.replace(tzinfo=None)
    cur.execute(
        """
        INSERT INTO configuracao_equipamento (equipamento_id, conteudo, assinatura, estado, data_coleta, created_at, updated_at)
        VALUES (%s,%s,%s,%s,%s, NOW(), NOW())
        """,
        (equipamento_id, conteudo or '', assinatura, estado, data_coleta)
    )
    return cur.lastrowid


def upsert_usuario(cur, equipamento_id, usuario, auth=None, crypto=None):
    cur.execute('SELECT id FROM usuario_equipamento WHERE equipamento_id=%s AND usuario=%s', (equipamento_id, usuario))
    r = cur.fetchone()
    if r:
        uid = r[0]
        cur.execute(
            """
            UPDATE usuario_equipamento SET snmpv3_auth=%s, snmpv3_crypto=%s, updated_at=NOW() WHERE id=%s
            """,
            (auth, crypto, uid)
        )
        return uid
    cur.execute(
        """
        INSERT INTO usuario_equipamento (equipamento_id, usuario, snmpv3_auth, snmpv3_crypto, created_at, updated_at)
        VALUES (%s,%s,%s,%s,NOW(),NOW())
        """,
        (equipamento_id, usuario, auth, crypto)
    )
    return cur.lastrowid


def insert_temperatura(cur, equipamento_id, valor_c, ts):
    if valor_c is None:
        return None

    # Garante datetime sem tz para MySQL
    if isinstance(ts, datetime) and ts.tzinfo is not None:
        ts = ts.replace(tzinfo=None)

    cur.execute(
        """
        INSERT INTO temperatura_leitura (
            equipamento_id,
            `timestamp`,
            valor_celsius,
            created_at,
            updated_at
        )
        VALUES (%s, %s, %s, NOW(), NOW())
        """,
        (equipamento_id, ts, valor_c)
    )
    return cur.lastrowid


def upsert_teste_ping(cur, equipamento_id, tempo_max_ms=None, intervalo_s=None):
    # Upsert em teste_rede para o protocolo 'PING'
    cur.execute(
        """
        SELECT id FROM teste_rede 
        WHERE equipamento_id=%s AND protocolo='PING'
        """,
        (equipamento_id,)
    )
    r = cur.fetchone()

    if r:
        tid = r[0]
        cur.execute(
            """
            UPDATE teste_rede 
               SET tempo_max_resposta_ms=%s, 
                   intervalo_segundos=%s, 
                   updated_at=NOW()
             WHERE id=%s
            """,
            (tempo_max_ms, intervalo_s, tid)
        )
        return tid

    cur.execute(
        """
        INSERT INTO teste_rede 
            (equipamento_id, protocolo, tempo_max_resposta_ms, intervalo_segundos, estado, created_at, updated_at)
        VALUES 
            (%s, 'PING', %s, %s, 'SEM_ESTADO', NOW(), NOW())
        """,
        (equipamento_id, tempo_max_ms, intervalo_s)
    )
    return cur.lastrowid

# ------------------------------
# Main
# ------------------------------

def main():
    ap = argparse.ArgumentParser(description='Ingestão de RelatorioAFS (CSV/XLSX) -> MySQL')
    ap.add_argument('--csv', required=True, help='Caminho do arquivo (CSV OU XLSX)')
    ap.add_argument('--sheet', default=None, help='Nome da planilha (aba) se for XLSX; padrão: primeira aba')
    ap.add_argument('--mysql-host', default='localhost')
    ap.add_argument('--mysql-db', default='relatorioAfs')   # ajuste para seu schema se quiser
    ap.add_argument('--mysql-user', default='root')
    ap.add_argument('--mysql-pass', default='Sucodeuva201212##')
    args = ap.parse_args()

    # 1) Lê arquivo (CSV ou XLSX)
    rows = read_rows_any(args.csv, sheet_name=args.sheet)

    # 2) Agrupa por IP do Componente (cada IP = 1 equipamento)
    blocos_por_ip = group_rows_by_ip(rows)
    if not blocos_por_ip:
        print('[WARN] Nenhum bloco (IP) encontrado no arquivo.')
        return

    # 3) Conecta MySQL e persiste CADA BLOCO
    conn = connect_mysql(args.mysql_host, args.mysql_db, args.mysql_user, args.mysql_pass)
    total = 0
    try:
        cur = conn.cursor()

        for ip, bloco in blocos_por_ip.items():
            try:
                data = extract_from_csv_block(bloco)

                # ---- Persistência por bloco (IP) ----
                eid = upsert_equipamento(cur, data['equipamento'])

                for v in data['vlans']:
                    upsert_vlan(cur, eid, v)

                for iface in data['interfaces']:
                    iid = upsert_interface(cur, eid, iface)
                    for ipd in iface['ips']:
                        if ipd['ip'] and ipd['prefixo'] is not None:
                            upsert_ip_interface(cur, iid, ipd['ip'], ipd['prefixo'])

                for r in data['rotas']:
                    upsert_rota(cur, eid, r['destino'], r['prefixo'], r['gateway'], r['tipo'])

                cfg = data['config']
                ts_cfg = cfg['data_coleta']
                if isinstance(ts_cfg, datetime) and ts_cfg.tzinfo is not None:
                    ts_cfg = ts_cfg.replace(tzinfo=None)

                # Só insere Config se existir conteúdo
                if cfg['conteudo']:
                    insert_config(cur, eid, cfg['conteudo'], cfg['assinatura'], cfg['estado'], ts_cfg)

                # Usuários (se vieram do config)
                for u in data['usuarios']:
                    upsert_usuario(cur, eid, u['usuario'], u['snmpv3_auth'], u['snmpv3_crypto'])

                # Telemetria simples
                monitor = data['monitor']
                insert_temperatura(cur, eid, monitor['temperatura_c'], ts_cfg)
                upsert_teste_ping(cur, eid, monitor['ping_tempo_max_ms'], monitor['ping_intervalo_s'])

                total += 1
                print(f"[OK] Ingestão do bloco {ip} concluída. Equipamento id = {eid}")

            except Exception as bex:
                # Não derruba os outros IPs; loga e segue.
                print(f"[WARN] Falha ao processar bloco {ip}: {bex}", file=sys.stderr)

        conn.commit()
        print(f"[OK] Ingestão finalizada ({total} equipamento(s)).")

    except Exception as ex:
        conn.rollback()
        print(f"[ERRO] {ex}", file=sys.stderr)
        raise
    finally:
        conn.close()

if __name__ == '__main__':
    main()
