
package com.example.telecom.domain.model;

import com.example.telecom.domain.core.Auditavel;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "subrede",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_subrede_cidr_vlan", columnNames = {"rede", "prefixo", "vlan_id"})
       },
       indexes = {
           @Index(name = "idx_subrede_vlan", columnList = "vlan_id"),
           @Index(name = "idx_subrede_cidr", columnList = "rede, prefixo")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Subrede extends Auditavel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 15, nullable = false)
    @Pattern(regexp = "^((25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.|$)){4}$")
    private String rede; // base do CIDR

    @Min(0) @Max(32)
    @Column(nullable = false)
    private Integer prefixo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vlan_id")
    private Vlan vlan; // opcional

    @Column(length = 120)
    private String descricao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRede() {
        return rede;
    }

    public void setRede(String rede) {
        this.rede = rede;
    }

    public Integer getPrefixo() {
        return prefixo;
    }

    public void setPrefixo(Integer prefixo) {
        this.prefixo = prefixo;
    }

    public Vlan getVlan() {
        return vlan;
    }

    public void setVlan(Vlan vlan) {
        this.vlan = vlan;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


}
