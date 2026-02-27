
package com.example.telecom.domain.model;

import com.example.telecom.domain.core.Auditavel;
import com.example.telecom.domain.core.DurationToLongConverter;
import com.example.telecom.domain.enums.EstadoMonitoramento;
import com.example.telecom.domain.enums.TipoMonitoramento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;

@Entity
@Table(name = "monitoramento",
       indexes = @Index(name = "idx_monit_equipamento", columnList = "equipamento_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Monitoramento extends Auditavel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private TipoMonitoramento tipo;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(length = 10)
    private String unidade;

    @Convert(converter = DurationToLongConverter.class)
    @Column(name = "intervalo_segundos")
    private Duration intervalo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoMonitoramento estado = EstadoMonitoramento.SEM_ESTADO;

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

    public TipoMonitoramento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMonitoramento tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public Duration getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Duration intervalo) {
        this.intervalo = intervalo;
    }

    public EstadoMonitoramento getEstado() {
        return estado;
    }

    public void setEstado(EstadoMonitoramento estado) {
        this.estado = estado;
    }


}
