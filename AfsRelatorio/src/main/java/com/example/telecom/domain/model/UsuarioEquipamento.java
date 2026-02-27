
package com.example.telecom.domain.model;

import com.example.telecom.domain.core.Auditavel;
import com.example.telecom.domain.enums.AuthSnmpv3;
import com.example.telecom.domain.enums.CryptoSnmpv3;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "usuario_equipamento",
       uniqueConstraints = @UniqueConstraint(name = "uk_usuario_equipamento", columnNames = {"equipamento_id", "usuario"}),
       indexes = @Index(name = "idx_usuario_equipamento_equip", columnList = "equipamento_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioEquipamento extends Auditavel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @NotBlank @Size(max = 60)
    private String usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "snmpv3_auth", length = 10)
    private AuthSnmpv3 snmpv3Auth;

    @Enumerated(EnumType.STRING)
    @Column(name = "snmpv3_crypto", length = 10)
    private CryptoSnmpv3 snmpv3Crypto;

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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public AuthSnmpv3 getSnmpv3Auth() {
        return snmpv3Auth;
    }

    public void setSnmpv3Auth(AuthSnmpv3 snmpv3Auth) {
        this.snmpv3Auth = snmpv3Auth;
    }

    public CryptoSnmpv3 getSnmpv3Crypto() {
        return snmpv3Crypto;
    }

    public void setSnmpv3Crypto(CryptoSnmpv3 snmpv3Crypto) {
        this.snmpv3Crypto = snmpv3Crypto;
    }



}
