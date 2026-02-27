package com.example.telecom.repository;

import com.example.telecom.domain.model.Vlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VlanRepository extends JpaRepository<Vlan, Long> { }
