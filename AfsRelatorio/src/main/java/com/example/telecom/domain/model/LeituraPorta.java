package com.example.telecom.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "leitura_porta",
       indexes = @Index(name = "idx_leitura_porta_equip_ts", columnList = "equipamento_id, timestamp"))
public class LeituraPorta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // dono
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @Column(name = "porta_num", nullable = false)
    private Integer portaNum;                 // ex.: "Porta 1" -> 1

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Column(name = "link_up")                 // "Ativo" => true
    private Boolean linkUp;

    @Column(name = "redundancia_ativa")      // "Redundância" => "Ativo" => true
    private Boolean redundanciaAtiva;

    @Column(name = "sfp_rx_dbm", precision = 8, scale = 2)  // -9.0, -8.7...
    private BigDecimal sfpRxDbm;

    @Column(name = "carga_entrada", precision = 10, scale = 2) // 0.08
    private BigDecimal cargaEntrada;

    // getters/setters (adicione ou use Lombok)
}