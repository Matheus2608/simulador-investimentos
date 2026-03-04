package br.com.caixa.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Registro historico de uma simulacao realizada")
public record HistoricoSimulacaoResponse(

        @Schema(description = "Identificador unico da simulacao", example = "1")
        Long id,

        @Schema(description = "Identificador do cliente que realizou a simulacao", example = "123")
        Long clienteId,

        @Schema(description = "Nome do produto utilizado na simulacao", example = "CDB Pos-fixado")
        String produto,

        @Schema(description = "Valor investido em reais", example = "10000.00")
        BigDecimal valorInvestido,

        @Schema(description = "Valor final estimado em reais (calculado com juros compostos)", example = "11268.25")
        BigDecimal valorFinal,

        @Schema(description = "Prazo da simulacao em meses", example = "12")
        Integer prazoMeses,

        @Schema(description = "Data e hora em que a simulacao foi realizada", example = "2026-03-04T10:30:00")
        LocalDateTime dataSimulacao
) {
}
