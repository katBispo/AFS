
package com.example.telecom.dto;

import java.time.OffsetDateTime;

public record PingDTO(OffsetDateTime timestamp, Integer rttMs, Integer perdaPercentual) {}
