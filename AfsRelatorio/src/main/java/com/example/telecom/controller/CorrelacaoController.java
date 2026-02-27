package com.example.telecom.controller;

import com.example.telecom.dto.IpVlanDTO; 
import com.example.telecom.service.CorrelacaoService;
import com.example.telecom.domain.model.InterfaceRede;
import com.example.telecom.repository.IpInterfaceRepository;
import com.example.telecom.repository.InterfaceVlanRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/correlacoes")
public class CorrelacaoController {

    private final CorrelacaoService service;
    private final IpInterfaceRepository ipRepo;
    private final InterfaceVlanRepository ifaceVlanRepo;

    public CorrelacaoController(CorrelacaoService service, IpInterfaceRepository ipRepo, InterfaceVlanRepository ifaceVlanRepo) {
        this.service = service;
        this.ipRepo = ipRepo;
        this.ifaceVlanRepo = ifaceVlanRepo;
    }

    @GetMapping("/rotas-vs-interfaces/{equipamentoId}")
    public ResponseEntity<List<String>> rotasVsInterfaces(@PathVariable Long equipamentoId){
        return ResponseEntity.ok(service.validarRotasVsInterfaces(equipamentoId));
    }

    @GetMapping("/ip-vlan/{equipamentoId}")
    public ResponseEntity<List<IpVlanDTO>> ipVlan(@PathVariable Long equipamentoId){
        var ips = ipRepo.findByInterfaceRede_Equipamento_Id(equipamentoId);
        var list = ips.stream().flatMap(ip -> {
            InterfaceRede iface = ip.getInterfaceRede();
            var assoc = iface.getVlans();
            if (assoc==null || assoc.isEmpty()) {
                return java.util.stream.Stream.of(new IpVlanDTO(ip.getIp(), ip.getPrefixo(), null, iface.getPorta()));
            }
            return assoc.stream().map(iv -> new IpVlanDTO(ip.getIp(), ip.getPrefixo(), iv.getVlan()!=null? iv.getVlan().getNumero(): null, iface.getPorta()));
        }).distinct().toList();
        return ResponseEntity.ok(list);
    }
}
