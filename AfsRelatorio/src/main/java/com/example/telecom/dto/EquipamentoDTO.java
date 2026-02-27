
package com.example.telecom.dto;

public record EquipamentoDTO(Long id, String nome, String modelo, String versaoSistema,
                             String fabricante, String localizacao, String ipPrincipal,
                             String descricao, String status) {}
