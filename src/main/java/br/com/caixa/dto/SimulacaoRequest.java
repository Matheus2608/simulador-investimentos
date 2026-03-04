package br.com.caixa.dto;

import br.com.caixa.domain.Risco;
import br.com.caixa.domain.TipoProduto;
import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

public record SimulacaoRequest(

        @NotNull(message = "clienteId e obrigatorio")
        @Positive(message = "clienteId deve ser positivo")
        Long clienteId,

        @NotNull(message = "valor e obrigatorio")
        @Positive(message = "valor deve ser positivo")
        @DecimalMin(value = "0.01", message = "valor deve ser maior que zero")
        BigDecimal valor,

        @NotNull(message = "prazoMeses e obrigatorio")
        @Positive(message = "prazoMeses deve ser positivo")
        @Min(value = 1, message = "prazoMeses deve ser no minimo 1")
        Integer prazoMeses,

        @NotNull(message = "tipoProduto e obrigatorio")
        TipoProduto tipoProduto,

        @Schema(description = "Nivel de risco desejado. Opcional: se nao informado, considera todos")
        Risco risco
) {
}
