package com.example.telecom.repository;

import com.example.telecom.domain.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
    Optional<Equipamento> findByIpPrincipal(String ipPrincipal);
}
