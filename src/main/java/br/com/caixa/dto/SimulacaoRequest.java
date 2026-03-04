package br.com.caixa.dto;

import br.com.caixa.domain.Risco;
import br.com.caixa.domain.TipoProduto;
import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Parametros para simulacao de investimento")
public record SimulacaoRequest(

        @NotNull(message = "valor e obrigatorio")
        @Positive(message = "valor deve ser positivo")
        @DecimalMin(value = "0.01", message = "valor deve ser maior que zero")
        @Schema(description = "Valor a ser investido em reais", example = "10000.00", minimum = "0.01")
        BigDecimal valor,

        @NotNull(message = "prazoMeses e obrigatorio")
        @Positive(message = "prazoMeses deve ser positivo")
        @Min(value = 1, message = "prazoMeses deve ser no minimo 1")
        @Schema(description = "Prazo do investimento em meses", example = "12", minimum = "1")
        Integer prazoMeses,

        @NotNull(message = "tipoProduto e obrigatorio")
        @Schema(description = "Tipo do produto de investimento", example = "CDB", enumeration = {"CDB", "LCI", "LCA"})
        TipoProduto tipoProduto,

        @Schema(description = "Nivel de risco desejado. Opcional: se nao informado, considera qualquer nivel de risco",
                example = "MEDIO", enumeration = {"BAIXO", "MEDIO", "ALTO"}, nullable = true)
        Risco risco
) {
}
