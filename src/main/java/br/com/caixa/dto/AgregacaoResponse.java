package br.com.caixa.dto;

import java.math.BigDecimal;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Metricas consolidadas das simulacoes do investidor")
public record AgregacaoResponse(

        @Schema(description = "Resumo geral de todas as simulacoes")
        ResumoGeral resumoGeral,

        @Schema(description = "Metricas agrupadas por tipo de produto")
        List<AgregacaoPorTipo> porTipoProduto,

        @Schema(description = "Distribuicao do valor investido por nivel de risco")
        List<DistribuicaoRisco> distribuicaoRisco,

        @Schema(description = "Destaques das simulacoes")
        Destaques destaques
) {

    @Schema(description = "Resumo geral consolidado")
    public record ResumoGeral(
            @Schema(description = "Total de simulacoes realizadas", example = "5")
            int totalSimulacoes,

            @Schema(description = "Soma de todos os valores investidos", example = "50000.00")
            BigDecimal valorTotalInvestido,

            @Schema(description = "Soma de todos os valores finais projetados", example = "56000.00")
            BigDecimal valorTotalProjetado,

            @Schema(description = "Rendimento total (valorTotalProjetado - valorTotalInvestido)", example = "6000.00")
            BigDecimal rendimentoTotal,

            @Schema(description = "Media do rendimento percentual por simulacao", example = "12.50")
            BigDecimal rendimentoPercentualMedio,

            @Schema(description = "Ticket medio (valorTotalInvestido / totalSimulacoes)", example = "10000.00")
            BigDecimal ticketMedio,

            @Schema(description = "Prazo medio ponderado pelo valor investido em meses", example = "18")
            int prazoMedioPonderado
    ) {
    }

    @Schema(description = "Metricas de simulacoes agrupadas por tipo de produto")
    public record AgregacaoPorTipo(
            @Schema(description = "Tipo do produto", example = "CDB")
            String tipoProduto,

            @Schema(description = "Total de simulacoes deste tipo", example = "3")
            int totalSimulacoes,

            @Schema(description = "Soma dos valores investidos neste tipo", example = "30000.00")
            BigDecimal valorTotalInvestido,

            @Schema(description = "Soma dos valores finais projetados neste tipo", example = "33600.00")
            BigDecimal valorTotalProjetado,

            @Schema(description = "Rendimento total deste tipo", example = "3600.00")
            BigDecimal rendimentoTotal,

            @Schema(description = "Media do rendimento percentual deste tipo", example = "12.00")
            BigDecimal rendimentoPercentualMedio,

            @Schema(description = "Ticket medio deste tipo", example = "10000.00")
            BigDecimal ticketMedio,

            @Schema(description = "Prazo medio em meses deste tipo", example = "12")
            int prazoMedio
    ) {
    }

    @Schema(description = "Percentual do valor investido por nivel de risco")
    public record DistribuicaoRisco(
            @Schema(description = "Nivel de risco", example = "MEDIO")
            String risco,

            @Schema(description = "Percentual do valor total investido neste nivel de risco", example = "60.00")
            BigDecimal percentualValor,

            @Schema(description = "Total de simulacoes neste nivel de risco", example = "3")
            int totalSimulacoes
    ) {
    }

    @Schema(description = "Destaques das simulacoes do investidor")
    public record Destaques(
            @Schema(description = "Simulacao com maior rendimento percentual")
            SimulacaoDestaque maiorRendimento,

            @Schema(description = "Simulacao com maior valor final")
            SimulacaoDestaque maiorValorFinal,

            @Schema(description = "Produto mais simulado pelo investidor")
            ProdutoFavorito produtoFavorito
    ) {
    }

    @Schema(description = "Dados resumidos de uma simulacao em destaque")
    public record SimulacaoDestaque(
            @Schema(description = "Identificador da simulacao", example = "1")
            Long id,

            @Schema(description = "Nome do produto", example = "CDB Pos-fixado")
            String produto,

            @Schema(description = "Valor investido", example = "10000.00")
            BigDecimal valorInvestido,

            @Schema(description = "Valor final projetado", example = "11268.25")
            BigDecimal valorFinal,

            @Schema(description = "Rendimento percentual", example = "12.68")
            BigDecimal rendimentoPercentual,

            @Schema(description = "Prazo em meses", example = "12")
            int prazoMeses
    ) {
    }

    @Schema(description = "Produto mais frequente nas simulacoes")
    public record ProdutoFavorito(
            @Schema(description = "Nome do produto", example = "CDB Pos-fixado")
            String produto,

            @Schema(description = "Total de simulacoes com este produto", example = "3")
            int totalSimulacoes
    ) {
    }

    public static AgregacaoResponse vazio() {
        return new AgregacaoResponse(
                new ResumoGeral(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO, 0),
                List.of(),
                List.of(),
                new Destaques(null, null, null)
        );
    }
}
