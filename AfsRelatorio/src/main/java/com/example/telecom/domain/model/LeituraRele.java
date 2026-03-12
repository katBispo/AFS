package com.example.telecom.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "leitura_rele",
       indexes = @Index(name = "idx_leitura_rele_equip_ts", columnList = "equipamento_id, timestamp"))
public class LeituraRele {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @Column(name = "rele_num", nullable = false)   // "Relé 1" -> 1
    private Integer releNum;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Column(length = 20) // "Fechado"/"Aberto"
    private String estado;

    // getters/setters
}