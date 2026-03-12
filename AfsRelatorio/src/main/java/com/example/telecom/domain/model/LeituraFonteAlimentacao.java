package com.example.telecom.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "leitura_fonte_alimentacao",
       indexes = @Index(name = "idx_leitura_fonte_equip_ts", columnList = "equipamento_id, timestamp"))
public class LeituraFonteAlimentacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @Column(name = "fonte_num", nullable = false)  // "Fonte de Alimentação 1" -> 1
    private Integer fonteNum;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Column(length = 20) // "Ativo", "Inativo", etc.
    private String estado;

    // getters/setters
}