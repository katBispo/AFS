
package com.example.telecom.controller;

import com.example.telecom.dto.EquipamentoDTO;
import com.example.telecom.domain.model.Equipamento;
import com.example.telecom.repository.EquipamentoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/equipamentos")
public class EquipamentoController {

    private final EquipamentoRepository repo;

    public EquipamentoController(EquipamentoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<List<EquipamentoDTO>> listar(){
        var list = repo.findAll().stream().map(e -> new EquipamentoDTO(
                e.getId(), e.getNome(), e.getModelo(), e.getVersaoSistema(), e.getFabricante(),
                e.getLocalizacao(), e.getIpPrincipal(), e.getDescricao(),
                e.getStatus() != null ? e.getStatus().name() : null
        )).toList();
        return ResponseEntity.ok(list);
    }

@GetMapping("/{id}")
public ResponseEntity<EquipamentoDTO> obter(@PathVariable Long id){
    var e = repo.findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipamento não encontrado")
    );
    return ResponseEntity.ok(new EquipamentoDTO(
        e.getId(), e.getNome(), e.getModelo(), e.getVersaoSistema(), e.getFabricante(),
        e.getLocalizacao(), e.getIpPrincipal(), e.getDescricao(),
        e.getStatus() != null ? e.getStatus().name() : null
    ));
}



    
}
