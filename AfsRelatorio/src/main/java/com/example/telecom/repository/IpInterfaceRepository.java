package com.example.telecom.repository;

import com.example.telecom.domain.model.IpInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IpInterfaceRepository extends JpaRepository<IpInterface, Long> {
    List<IpInterface> findByInterfaceRede_Equipamento_Id(Long equipamentoId);
}
