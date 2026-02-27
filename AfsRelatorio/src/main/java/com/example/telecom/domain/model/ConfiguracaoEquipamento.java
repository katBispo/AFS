
package com.example.telecom.domain.model;

import com.example.telecom.domain.core.Auditavel;
import com.example.telecom.domain.enums.EstadoConfiguracao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "configuracao_equipamento",
       indexes = @Index(name = "idx_config_equipamento", columnList = "equipamento_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConfiguracaoEquipamento extends Auditavel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @Lob
    @Column(name = "conteudo", nullable = false, columnDefinition = "text")
    private String conteudo;

    @Column(length = 120)
    private String assinatura;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoConfiguracao estado = EstadoConfiguracao.SALVO;

    @NotNull
    @Column(name = "data_coleta", nullable = false)
    private OffsetDateTime dataColeta;

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

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    public EstadoConfiguracao getEstado() {
        return estado;
    }

    public void setEstado(EstadoConfiguracao estado) {
        this.estado = estado;
    }

    public OffsetDateTime getDataColeta() {
        return dataColeta;
    }

    public void setDataColeta(OffsetDateTime dataColeta) {
        this.dataColeta = dataColeta;
    }


}
