package com.example.telecom.repository;

import com.example.telecom.domain.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;

public interface LeituraPortaRepository extends JpaRepository<LeituraPorta, Long> {
  List<LeituraPorta> findByEquipamento_IdAndTimestampBetween(Long equipId, OffsetDateTime ini, OffsetDateTime fim);
  List<LeituraPorta> findTop100ByEquipamento_IdOrderByTimestampDesc(Long equipId);
}
