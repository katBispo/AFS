
package com.example.telecom.domain.model;

import com.example.telecom.domain.core.Auditavel;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "vlan",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_vlan_equip_numero", columnNames = {"equipamento_id", "numero"})
       },
       indexes = @Index(name = "idx_vlan_equipamento", columnList = "equipamento_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vlan extends Auditavel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @Min(1) @Max(4094)
    @Column(nullable = false)
    private Integer numero;

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

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }


}
