package br.com.caixa.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistoricoSimulacaoResponse(
        Long id,
        Long clienteId,
        String produto,
        BigDecimal valorInvestido,
        BigDecimal valorFinal,
        Integer prazoMeses,
        LocalDateTime dataSimulacao
) {
}
