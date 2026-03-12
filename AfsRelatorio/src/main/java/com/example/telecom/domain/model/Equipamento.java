package com.example.telecom.domain.model;

import com.example.telecom.domain.core.Auditavel;
import com.example.telecom.domain.enums.StatusEquipamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipamento", indexes = {
        @Index(name = "idx_equipamento_nome", columnList = "nome"),
        @Index(name = "idx_equipamento_ip", columnList = "ip_principal", unique = true)
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Equipamento extends Auditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    // Agora nome é opcional: alguns blocos do CSV trazem "Nome", outros não.
    @Size(max = 120)
    private String nome; // ex.: "ARM KM186"

    @Size(max = 80)
    private String modelo; // do "System Description" (no arquivo de configuração), quando existir

    @Column(name = "versao_sistema", length = 80)
    private String versaoSistema; // do "System Version", quando existir

    @Size(max = 120)
    private String fabricante; // do "snmp-server contact", quando existir

    @Size(max = 160)
    private String localizacao; // do "snmp-server location", quando existir

    @Column(name = "ip_principal", length = 15, unique = true, nullable = false)
    @Pattern(regexp = "^((25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.|$)){4}$", message = "IP inválido (IPv4)")
    @ToString.Include
    private String ipPrincipal; // CHAVE do equipamento (cada IP = 1 equipamento)

    @Column(columnDefinition = "text")
    private String descricao; // pode receber o pre-login-banner, quando existir no arquivo de configuração

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatusEquipamento status = StatusEquipamento.DESCONHECIDO;



    // As coleções abaixo são úteis no domínio, mas geralmente você NÃO quer serializá-las em cada resposta.
    // Para evitar recursão e payloads gigantes, use @JsonIgnore (ou retorne DTOs).
    @JsonIgnore
    @OneToMany(mappedBy = "equipamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InterfaceRede> interfaces = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "equipamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Vlan> vlans = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "equipamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Rota> rotas = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "equipamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ConfiguracaoEquipamento> configuracoes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "equipamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Monitoramento> monitoramentos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "equipamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TesteRede> testes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "equipamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UsuarioEquipamento> usuarios = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getVersaoSistema() {
        return versaoSistema;
    }

    public void setVersaoSistema(String versaoSistema) {
        this.versaoSistema = versaoSistema;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getIpPrincipal() {
        return ipPrincipal;
    }

    public void setIpPrincipal(String ipPrincipal) {
        this.ipPrincipal = ipPrincipal;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusEquipamento getStatus() {
        return status;
    }

    public void setStatus(StatusEquipamento status) {
        this.status = status;
    }

    public List<InterfaceRede> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<InterfaceRede> interfaces) {
        this.interfaces = interfaces;
    }

    public List<Vlan> getVlans() {
        return vlans;
    }

    public void setVlans(List<Vlan> vlans) {
        this.vlans = vlans;
    }

    public List<Rota> getRotas() {
        return rotas;
    }

    public void setRotas(List<Rota> rotas) {
        this.rotas = rotas;
    }

    public List<ConfiguracaoEquipamento> getConfiguracoes() {
        return configuracoes;
    }

    public void setConfiguracoes(List<ConfiguracaoEquipamento> configuracoes) {
        this.configuracoes = configuracoes;
    }

    public List<Monitoramento> getMonitoramentos() {
        return monitoramentos;
    }

    public void setMonitoramentos(List<Monitoramento> monitoramentos) {
        this.monitoramentos = monitoramentos;
    }

    public List<TesteRede> getTestes() {
        return testes;
    }

    public void setTestes(List<TesteRede> testes) {
        this.testes = testes;
    }

    public List<UsuarioEquipamento> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioEquipamento> usuarios) {
        this.usuarios = usuarios;
    }
}