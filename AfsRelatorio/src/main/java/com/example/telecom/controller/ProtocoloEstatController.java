package com.example.telecom.controller;

import com.example.telecom.domain.model.*;
import com.example.telecom.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;   // <-- este é o correto!

import java.time.OffsetDateTime;                            // <-- garanta este import

import java.util.List;



@RestController
@RequestMapping("/api/protocolos")
public class ProtocoloEstatController {
  private final PingEstatisticaRepository pingRepo;
  private final SnmpEstatisticaRepository snmpRepo;

  public ProtocoloEstatController(PingEstatisticaRepository p, SnmpEstatisticaRepository s){
    this.pingRepo = p; this.snmpRepo = s;
  }

  @GetMapping("/ping")
  public List<PingEstatistica> ping(@RequestParam Long equipamentoId,
                                    @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime ini,
                                    @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fim){
    return pingRepo.findByEquipamento_IdAndTimestampBetween(equipamentoId, ini, fim);
  }

  @GetMapping("/snmp")
  public List<SnmpEstatistica> snmp(@RequestParam Long equipamentoId,
                                    @RequestParam String versao, // "V1" ou "V3"
                                    @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime ini,
                                    @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fim){
    return snmpRepo.findByEquipamento_IdAndVersaoAndTimestampBetween(equipamentoId, versao, ini, fim);
  }
}