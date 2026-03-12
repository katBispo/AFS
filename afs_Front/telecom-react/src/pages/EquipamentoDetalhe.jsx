import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import dayjs from 'dayjs'
import {
  obterEquipamento, listarInterfaces, listarVlans, listarRotas,
  listarPortasUltimas, estatPing, estatSnmp, serieTemperatura
} from '../services/api'
import Table from '../components/Table'
import { TemperaturaChart } from '../components/Charts'

export default function EquipamentoDetalhe(){
  const { id } = useParams()
  const [equip, setEquip] = useState(null)
  const [ifaces, setIfaces] = useState([])
  const [vlans, setVlans] = useState([])
  const [rotas, setRotas] = useState([])

  const [portas, setPortas] = useState([])

  const [pingEst, setPingEst] = useState([])
  const [snmpV1,  setSnmpV1 ] = useState([])
  const [snmpV3,  setSnmpV3 ] = useState([])

  const [tempSerie, setTempSerie] = useState([])

  // filtros de tempo
  const [ini, setIni] = useState(dayjs().subtract(2,'hour').format('YYYY-MM-DDTHH:mm'))
  const [fim, setFim] = useState(dayjs().format('YYYY-MM-DDTHH:mm'))

  useEffect(()=> {
    obterEquipamento(id).then(setEquip)

    // Estas 3 podem vir vazias se não houver "Arquivo de Configuração" no bloco
    listarInterfaces(id).then(setIfaces).catch(()=> setIfaces([]))
    listarVlans(id).then(setVlans).catch(()=> setVlans([]))
    listarRotas(id).then(setRotas).catch(()=> setRotas([]))

    // Portas (últimas)
    listarPortasUltimas(id).then(setPortas).catch(()=> setPortas([]))
  }, [id])

  useEffect(()=> {
    const iniIso = dayjs(ini).toISOString()
    const fimIso = dayjs(fim).toISOString()

    // Séries
    serieTemperatura(id, iniIso, fimIso).then(setTempSerie).catch(()=> setTempSerie([]))

    // Estatísticas
    estatPing(id, iniIso, fimIso).then(setPingEst).catch(()=> setPingEst([]))
    estatSnmp(id, 'V1', iniIso, fimIso).then(setSnmpV1).catch(()=> setSnmpV1([]))
    estatSnmp(id, 'V3', iniIso, fimIso).then(setSnmpV3).catch(()=> setSnmpV3([]))
  }, [id, ini, fim])

  if(!equip) return <div>Carregando...</div>

  return (
    <div className="row">
      <div className="col">
        <h2>{equip.nome ? `${equip.nome} — ${equip.ipPrincipal}` : equip.ipPrincipal}</h2>
        <div className="card" style={{marginBottom:16}}>
          <div>Modelo: {equip.modelo || '—'} · Versão: {equip.versaoSistema || '—'}</div>
          <div>Localização: {equip.localizacao || '—'}</div>
        </div>

        {/** Configuração (pode estar vazia se não houver arquivo) */}
        <h3>Interfaces</h3>
        <Table keyField="id" columns={[
          {key:'porta', header:'Porta'},
          {key:'nome', header:'Nome'},
          {key:'tipo', header:'Tipo'},
          {key:'velocidadeMbps', header:'Velocidade (Mbps)'},
        ]} rows={ifaces} />

        <h3>VLANs</h3>
        <Table keyField="id" columns={[{key:'numero', header:'VLAN'}]} rows={vlans} />

        <h3>Rotas</h3>
        <Table keyField="id" columns={[
          {key:'destino', header:'Destino'},
          {key:'prefixo', header:'Prefixo'},
          {key:'gateway', header:'Gateway'},
          {key:'tipo', header:'Tipo'}
        ]} rows={rotas} />

        {/** Portas (últimas) */}
        <h3 style={{marginTop:24}}>Portas (últimas leituras)</h3>
        <Table keyField="id" columns={[
          {key:'portaNum', header:'Porta'},
          {key:'linkUp', header:'Link', render:r => r.linkUp ? 'Ativo' : 'Inativo'},
          {key:'redundanciaAtiva', header:'Redundância', render:r => r.redundanciaAtiva ? 'Ativa' : '—'},
          {key:'sfpRxDbm', header:'SFP Rx (dBm)', render:r => r.sfpRxDbm ?? '—'},
          {key:'cargaEntrada', header:'Carga Entrada', render:r => r.cargaEntrada ?? '—'},
        ]} rows={portas} />
      </div>

      <div className="col">
        {/** Filtros de período */}
        <div className="card" style={{marginBottom:16}}>
          <div style={{display:'flex', gap:12, alignItems:'center'}}>
            <label>Início:</label>
            <input type="datetime-local" value={ini} onChange={e=>setIni(e.target.value)} />
            <label>Fim:</label>
            <input type="datetime-local" value={fim} onChange={e=>setFim(e.target.value)} />
          </div>
        </div>

        {/** Temperatura */}
        <TemperaturaChart data={tempSerie} />

        {/** Protocolos: Ping estatística */}
        <div className="card" style={{marginTop:16}}>
          <h3>Ping (estatística)</h3>
          <Table keyField="timestamp" columns={[
            {key:'timestamp', header:'Coleta'},
            {key:'maxMs', header:'Máx (ms)'},
            {key:'minMs', header:'Mín (ms)'},
            {key:'mediaMs', header:'Médio (ms)'},
            {key:'desvioMs', header:'Desvio (ms)'},
            {key:'perdaPercentual', header:'Perda (%)'},
            {key:'mediaMovMs', header:'Média Mov (ms)'},
            {key:'detectado', header:'Detectado', render:r => r.detectado ? 'Sim' : 'Não'},
          ]} rows={pingEst} />
        </div>

        {/** Protocolos: SNMP estatística */}
        <div className="card" style={{marginTop:16}}>
          <h3>SNMP V1 (estatística)</h3>
          <Table keyField="timestamp" columns={[
            {key:'timestamp', header:'Coleta'},
            {key:'maxMs', header:'Máx (ms)'},
            {key:'minMs', header:'Mín (ms)'},
            {key:'mediaMs', header:'Médio (ms)'},
            {key:'desvioMs', header:'Desvio (ms)'},
            {key:'perdaPercentual', header:'Perda (%)'},
            {key:'mediaMovMs', header:'Média Mov (ms)'},
            {key:'detectado', header:'Detectado', render:r => r.detectado ? 'Sim' : 'Não'},
          ]} rows={snmpV1} />
        </div>
        <div className="card" style={{marginTop:16}}>
          <h3>SNMP V3 (estatística)</h3>
          <Table keyField="timestamp" columns={[
            {key:'timestamp', header:'Coleta'},
            {key:'maxMs', header:'Máx (ms)'},
            {key:'minMs', header:'Mín (ms)'},
            {key:'mediaMs', header:'Médio (ms)'},
            {key:'desvioMs', header:'Desvio (ms)'},
            {key:'perdaPercentual', header:'Perda (%)'},
            {key:'mediaMovMs', header:'Média Mov (ms)'},
            {key:'detectado', header:'Detectado', render:r => r.detectado ? 'Sim' : 'Não'},
          ]} rows={snmpV3} />
        </div>
      </div>
    </div>
  )
}