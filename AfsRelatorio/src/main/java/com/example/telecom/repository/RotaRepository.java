package com.example.telecom.repository;

import com.example.telecom.domain.model.Rota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RotaRepository extends JpaRepository<Rota, Long> {
    List<Rota> findByEquipamento_Id(Long equipamentoId);
}
