
package com.example.telecom.controller;

import com.example.telecom.repository.RotaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rotas")
public class RotaController {
    private final RotaRepository repo;
    public RotaController(RotaRepository repo){ this.repo = repo; }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam("equipamentoId") Long equipId){
        var list = repo.findByEquipamento_Id(equipId).stream()
                .map(r -> new java.util.LinkedHashMap<String,Object>(){{
                    put("id", r.getId());
                    put("destino", r.getDestino());
                    put("prefixo", r.getPrefixo());
                    put("gateway", r.getGateway());
                    put("tipo", r.getTipo());
                }}).toList();
        return ResponseEntity.ok(list);
    }
}
