package com.example.telecom.repository;

import com.example.telecom.domain.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;

public interface PingEstatisticaRepository extends JpaRepository<PingEstatistica, Long> {
  List<PingEstatistica> findByEquipamento_IdAndTimestampBetween(Long equipId, OffsetDateTime ini, OffsetDateTime fim);
}
