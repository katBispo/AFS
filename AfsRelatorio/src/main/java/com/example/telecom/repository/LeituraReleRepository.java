package com.example.telecom.repository;
import com.example.telecom.domain.model.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LeituraReleRepository extends JpaRepository<LeituraRele, Long> {
  List<LeituraRele> findTop100ByEquipamento_IdOrderByTimestampDesc(Long equipId);
}
