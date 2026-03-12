package com.example.telecom.repository;
import com.example.telecom.domain.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;


public interface SnmpEstatisticaRepository extends JpaRepository<SnmpEstatistica, Long> {
  List<SnmpEstatistica> findByEquipamento_IdAndVersaoAndTimestampBetween(Long equipId, String versao, OffsetDateTime ini, OffsetDateTime fim);
}
