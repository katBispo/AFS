
package com.example.telecom.domain.model;

import com.example.telecom.domain.core.Auditavel;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ping_amostra",
       indexes = {
           @Index(name = "idx_ping_equip_ts", columnList = "equipamento_id, timestamp")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PingAmostra extends Auditavel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @Column(length = 15)
    private String destino;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Column(name = "rtt_ms")
    private Integer rttMs;

    @Column(name = "perda_percentual")
    private Integer perdaPercentual;

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

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getRttMs() {
        return rttMs;
    }

    public void setRttMs(Integer rttMs) {
        this.rttMs = rttMs;
    }

    public Integer getPerdaPercentual() {
        return perdaPercentual;
    }

    public void setPerdaPercentual(Integer perdaPercentual) {
        this.perdaPercentual = perdaPercentual;
    }


}
