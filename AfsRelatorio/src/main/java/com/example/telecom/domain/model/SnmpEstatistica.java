package com.example.telecom.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "snmp_estatistica",
       indexes = @Index(name = "idx_snmp_estat_equip_ts", columnList = "equipamento_id, timestamp"))
public class SnmpEstatistica {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Column(length = 5, nullable = false) // "V1" ou "V3"
    private String versao;

    @Column(name = "max_ms") private Integer maxMs;
    @Column(name = "min_ms") private Integer minMs;
    @Column(name = "media_ms") private Integer mediaMs;
    @Column(name = "desvio_ms") private Integer desvioMs;
    @Column(name = "perda_percentual") private Integer perdaPercentual;
    @Column(name = "media_mov_ms") private Integer mediaMovMs;
    @Column(name = "detectado") private Boolean detectado;

    // getters/setters
}