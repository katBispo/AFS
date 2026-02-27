
package com.example.telecom.domain.model;

import com.example.telecom.domain.core.Auditavel;
import com.example.telecom.domain.enums.Duplex;
import com.example.telecom.domain.enums.InterfaceTipo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interface_rede",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_interface_equip_porta", columnNames = {"equipamento_id", "porta"})
       },
       indexes = @Index(name = "idx_interface_equipamento", columnList = "equipamento_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InterfaceRede extends Auditavel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @NotBlank @Size(max = 15)
    private String porta;

    @Size(max = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private InterfaceTipo tipo = InterfaceTipo.OUTRO;

    @Column(name = "velocidade_mbps")
    private Integer velocidadeMbps;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Duplex duplex;

    @Column(name = "auto_negociacao")
    private Boolean autoNegociacao;

    @Column(name = "spanning_tree")
    private Boolean spanningTree;

    @OneToMany(mappedBy = "interfaceRede", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<IpInterface> ips = new ArrayList<>();

    @OneToMany(mappedBy = "interfaceRede", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InterfaceVlan> vlans = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }

    public String getPorta() {
        return porta;
    }

    public void setPorta(String porta) {
        this.porta = porta;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public InterfaceTipo getTipo() {
        return tipo;
    }

    public void setTipo(InterfaceTipo tipo) {
        this.tipo = tipo;
    }

    public Integer getVelocidadeMbps() {
        return velocidadeMbps;
    }

    public void setVelocidadeMbps(Integer velocidadeMbps) {
        this.velocidadeMbps = velocidadeMbps;
    }

    public Duplex getDuplex() {
        return duplex;
    }

    public void setDuplex(Duplex duplex) {
        this.duplex = duplex;
    }

    public Boolean getAutoNegociacao() {
        return autoNegociacao;
    }

    public void setAutoNegociacao(Boolean autoNegociacao) {
        this.autoNegociacao = autoNegociacao;
    }

    public Boolean getSpanningTree() {
        return spanningTree;
    }

    public void setSpanningTree(Boolean spanningTree) {
        this.spanningTree = spanningTree;
    }

    public List<IpInterface> getIps() {
        return ips;
    }

    public void setIps(List<IpInterface> ips) {
        this.ips = ips;
    }

    public List<InterfaceVlan> getVlans() {
        return vlans;
    }

    public void setVlans(List<InterfaceVlan> vlans) {
        this.vlans = vlans;
    }


}
