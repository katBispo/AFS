package com.example.telecom.repository;

import com.example.telecom.domain.model.TemperaturaLeitura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;

public interface TemperaturaLeituraRepository extends JpaRepository<TemperaturaLeitura, Long> {
    List<TemperaturaLeitura> findByEquipamento_IdAndTimestampBetween(Long equipamentoId, OffsetDateTime ini, OffsetDateTime fim);
}
