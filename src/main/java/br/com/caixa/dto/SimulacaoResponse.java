package br.com.caixa.dto;

import br.com.caixa.domain.Risco;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Resultado da simulacao de investimento")
public record SimulacaoResponse(

        @Schema(description = "Dados do produto selecionado para a simulacao")
        ProdutoValidado produtoValidado,

        @Schema(description = "Resultado do calculo da simulacao")
        ResultadoSimulacao resultadoSimulacao,

        @Schema(description = "Data e hora em que a simulacao foi realizada", example = "2026-03-04T10:30:00")
        LocalDateTime dataSimulacao
) {

    @Schema(description = "Produto de investimento selecionado com base nos parametros da simulacao")
    public record ProdutoValidado(
            @Schema(description = "Identificador unico do produto", example = "1")
            Long id,

            @Schema(description = "Nome do produto", example = "CDB Pos-fixado")
            String nome,

            @Schema(description = "Tipo do produto", example = "CDB")
            String tipo,

            @Schema(description = "Rentabilidade anual do produto (taxa decimal)", example = "0.1225")
            BigDecimal rentabilidade,

            @Schema(description = "Nivel de risco do produto", example = "MEDIO")
            Risco risco
    ) {
    }

    @Schema(description = "Resultado financeiro da simulacao calculado com juros compostos")
    public record ResultadoSimulacao(
            @Schema(description = "Valor final estimado do investimento em reais", example = "11268.25")
            BigDecimal valorFinal,

            @Schema(description = "Prazo da simulacao em meses", example = "12")
            Integer prazoMeses
    ) {
    }
}
