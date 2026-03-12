package com.example.telecom.repository;

import com.example.telecom.domain.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeituraFonteAlimentacaoRepository extends JpaRepository<LeituraFonteAlimentacao, Long> {
    List<LeituraFonteAlimentacao> findTop100ByEquipamento_IdOrderByTimestampDesc(Long equipId);
}
