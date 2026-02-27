
package com.example.telecom.controller;

import com.example.telecom.repository.VlanRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vlans")
public class VlanController {
    private final VlanRepository repo;
    public VlanController(VlanRepository repo){ this.repo = repo; }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam("equipamentoId") Long equipId){
        var list = repo.findAll().stream()
                .filter(v -> v.getEquipamento()!=null && equipId.equals(v.getEquipamento().getId()))
                .map(v -> new java.util.LinkedHashMap<String,Object>(){{
                    put("id", v.getId());
                    put("numero", v.getNumero());
                }}).toList();
        return ResponseEntity.ok(list);
    }
}
