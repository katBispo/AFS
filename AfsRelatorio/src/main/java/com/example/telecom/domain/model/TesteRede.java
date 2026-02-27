
package com.example.telecom.domain.model;

import com.example.telecom.domain.core.Auditavel;
import com.example.telecom.domain.core.DurationToLongConverter;
import com.example.telecom.domain.enums.EstadoMonitoramento;
import com.example.telecom.domain.enums.ProtocoloTeste;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;

@Entity
@Table(name = "teste_rede",
       indexes = @Index(name = "idx_teste_equipamento", columnList = "equipamento_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TesteRede extends Auditavel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ProtocoloTeste protocolo = ProtocoloTeste.PING;

    @Column(name = "tempo_max_resposta_ms")
    private Integer tempoMaxRespostaMs;

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

    public ProtocoloTeste getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(ProtocoloTeste protocolo) {
        this.protocolo = protocolo;
    }

    public Integer getTempoMaxRespostaMs() {
        return tempoMaxRespostaMs;
    }

    public void setTempoMaxRespostaMs(Integer tempoMaxRespostaMs) {
        this.tempoMaxRespostaMs = tempoMaxRespostaMs;
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
