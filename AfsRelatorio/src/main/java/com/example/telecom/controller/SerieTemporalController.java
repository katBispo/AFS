
package com.example.telecom.controller;

import com.example.telecom.repository.PingAmostraRepository;
import com.example.telecom.repository.TemperaturaLeituraRepository;
import com.example.telecom.dto.PingDTO;
import com.example.telecom.dto.TemperaturaDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/series")
public class SerieTemporalController {
    private final TemperaturaLeituraRepository tempRepo;
    private final PingAmostraRepository pingRepo;

    public SerieTemporalController(TemperaturaLeituraRepository tempRepo, PingAmostraRepository pingRepo) {
        this.tempRepo = tempRepo;
        this.pingRepo = pingRepo;
    }

    @GetMapping("/temperatura")
    public ResponseEntity<?> temperatura(@RequestParam Long equipamentoId,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime ini,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fim){
        var list = tempRepo.findByEquipamento_IdAndTimestampBetween(equipamentoId, ini, fim).stream()
                .map(t -> new TemperaturaDTO(t.getTimestamp(), t.getValorCelsius()))
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/ping")
    public ResponseEntity<?> ping(@RequestParam Long equipamentoId,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime ini,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fim){
        var list = pingRepo.findByEquipamento_IdAndTimestampBetween(equipamentoId, ini, fim).stream()
                .map(p -> new PingDTO(p.getTimestamp(), p.getRttMs(), p.getPerdaPercentual()))
                .toList();
        return ResponseEntity.ok(list);
    }
}
