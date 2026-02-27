
package com.example.telecom.domain.model;

import com.example.telecom.domain.core.Auditavel;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "rota",
       indexes = @Index(name = "idx_rota_equipamento", columnList = "equipamento_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rota extends Auditavel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @NotBlank
    @Pattern(regexp = "^((25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.|$)){4}$")
    private String destino; // rede destino

    @Min(0) @Max(32)
    @Column(name = "prefixo", nullable = false)
    private Integer prefixo; // CIDR

    @NotBlank
    @Pattern(regexp = "^((25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.|$)){4}$")
    private String gateway; // next-hop

    @Column(length = 20, nullable = false)
    private String tipo = "ESTATICA";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interface_saida_id")
    private InterfaceRede interfaceSaida; // opcional

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

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Integer getPrefixo() {
        return prefixo;
    }

    public void setPrefixo(Integer prefixo) {
        this.prefixo = prefixo;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public InterfaceRede getInterfaceSaida() {
        return interfaceSaida;
    }

    public void setInterfaceSaida(InterfaceRede interfaceSaida) {
        this.interfaceSaida = interfaceSaida;
    }


}
