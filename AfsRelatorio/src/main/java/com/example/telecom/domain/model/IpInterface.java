
package com.example.telecom.domain.model;

import com.example.telecom.domain.core.Auditavel;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "ip_interface",
       indexes = {
           @Index(name = "idx_ip_interface_iface", columnList = "interface_id"),
           @Index(name = "idx_ip_interface_cidr", columnList = "ip, prefixo")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IpInterface extends Auditavel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "interface_id", nullable = false)
    private InterfaceRede interfaceRede;

    @Column(length = 15, nullable = false)
    @Pattern(regexp = "^((25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.|$)){4}$")
    private String ip;

    @Min(0) @Max(32)
    @Column(nullable = false)
    private Integer prefixo;

    @Column(name = "principal")
    private Boolean principal = Boolean.TRUE;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPrefixo() {
        return prefixo;
    }

    public void setPrefixo(Integer prefixo) {
        this.prefixo = prefixo;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }


}
