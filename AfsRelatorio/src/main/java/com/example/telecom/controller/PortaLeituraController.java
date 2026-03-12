package com.example.telecom.controller;

import com.example.telecom.domain.model.*;
import com.example.telecom.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/portas")
public class PortaLeituraController {
  private final LeituraPortaRepository repo;
  public PortaLeituraController(LeituraPortaRepository repo){ this.repo = repo; }

  @GetMapping("/ultimas/{equipamentoId}")
  public List<LeituraPorta> ultimas(@PathVariable Long equipamentoId){
    return repo.findTop100ByEquipamento_IdOrderByTimestampDesc(equipamentoId);
  }
}