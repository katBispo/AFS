package com.example.telecom.repository;

import com.example.telecom.domain.model.PingAmostra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;

public interface PingAmostraRepository extends JpaRepository<PingAmostra, Long> {
    List<PingAmostra> findByEquipamento_IdAndTimestampBetween(Long equipamentoId, OffsetDateTime ini, OffsetDateTime fim);
}
