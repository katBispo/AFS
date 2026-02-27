
package com.example.telecom.domain.model;

import com.example.telecom.domain.core.Auditavel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interface_vlan",
       uniqueConstraints = @UniqueConstraint(name = "uk_iface_vlan", columnNames = {"interface_id", "vlan_id"}),
       indexes = {
           @Index(name = "idx_iface_vlan_iface", columnList = "interface_id"),
           @Index(name = "idx_iface_vlan_vlan", columnList = "vlan_id")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InterfaceVlan extends Auditavel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "interface_id", nullable = false)
    private InterfaceRede interfaceRede;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vlan_id", nullable = false)
    private Vlan vlan;

    @Column(nullable = false)
    private Boolean tagged = Boolean.TRUE;

    @Column(nullable = false)
    private Boolean pvid = Boolean.FALSE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InterfaceRede getInterfaceRede() {
        return interfaceRede;
    }

    public void setInterfaceRede(InterfaceRede interfaceRede) {
        this.interfaceRede = interfaceRede;
    }

    public Vlan getVlan() {
        return vlan;
    }

    public void setVlan(Vlan vlan) {
        this.vlan = vlan;
    }

    public Boolean getTagged() {
        return tagged;
    }

    public void setTagged(Boolean tagged) {
        this.tagged = tagged;
    }

    public Boolean getPvid() {
        return pvid;
    }

    public void setPvid(Boolean pvid) {
        this.pvid = pvid;
    }


}
