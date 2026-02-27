
package com.example.telecom.domain.model;

import com.example.telecom.domain.core.Auditavel;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "temperatura_leitura",
       indexes = {
           @Index(name = "idx_temp_equip_ts", columnList = "equipamento_id, timestamp")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TemperaturaLeitura extends Auditavel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Column(nullable = false)
    private Integer valorCelsius;

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

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getValorCelsius() {
        return valorCelsius;
    }

    public void setValorCelsius(Integer valorCelsius) {
        this.valorCelsius = valorCelsius;
    }


}
