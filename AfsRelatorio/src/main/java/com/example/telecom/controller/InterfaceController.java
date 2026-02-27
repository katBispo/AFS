
package com.example.telecom.controller;

import com.example.telecom.repository.InterfaceRedeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interfaces")
public class InterfaceController {

    private final InterfaceRedeRepository repo;

    public InterfaceController(InterfaceRedeRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<?> listarPorEquip(@RequestParam("equipamentoId") Long equipId){
        var list = repo.findAll().stream()
                .filter(i -> i.getEquipamento() != null && equipId.equals(i.getEquipamento().getId()))
                .map(i -> new java.util.LinkedHashMap<String,Object>(){{
                    put("id", i.getId());
                    put("porta", i.getPorta());
                    put("nome", i.getNome());
                    put("tipo", i.getTipo()!=null? i.getTipo().name(): null);
                    put("velocidadeMbps", i.getVelocidadeMbps());
                }}).toList();
        return ResponseEntity.ok(list);
    }
}
