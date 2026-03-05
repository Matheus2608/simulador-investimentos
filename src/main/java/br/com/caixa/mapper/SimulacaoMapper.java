package br.com.caixa.mapper;

import br.com.caixa.domain.Produto;
import br.com.caixa.domain.Simulacao;
import br.com.caixa.dto.AgregacaoResponse;
import br.com.caixa.dto.AgregacaoResponse.AgregacaoPorTipo;
import br.com.caixa.dto.AgregacaoResponse.Destaques;
import br.com.caixa.dto.AgregacaoResponse.DistribuicaoRisco;
import br.com.caixa.dto.AgregacaoResponse.ProdutoFavorito;
import br.com.caixa.dto.AgregacaoResponse.ResumoGeral;
import br.com.caixa.dto.AgregacaoResponse.SimulacaoDestaque;
import br.com.caixa.dto.HistoricoSimulacaoResponse;
import br.com.caixa.dto.SimulacaoResponse;
import br.com.caixa.util.CalculoAgregacao;
import br.com.caixa.util.Money;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class SimulacaoMapper {

    private SimulacaoMapper() {
    }

    public static SimulacaoResponse toResponse(Simulacao simulacao, Produto produto) {
        var produtoValidado = new SimulacaoResponse.ProdutoValidado(
                produto.id,
                produto.nome,
                produto.tipoProduto.name(),
                produto.rentabilidadeAnual,
                produto.risco
        );

        var resultado = new SimulacaoResponse.ResultadoSimulacao(
                simulacao.valorFinal,
                simulacao.prazoMeses
        );

        return new SimulacaoResponse(produtoValidado, resultado, simulacao.dataSimulacao);
    }

    public static HistoricoSimulacaoResponse toHistorico(Simulacao simulacao) {
        return new HistoricoSimulacaoResponse(
                simulacao.id,
                simulacao.clienteId,
                simulacao.produtoNome,
                simulacao.valorInvestido,
                simulacao.valorFinal,
                simulacao.prazoMeses,
                simulacao.dataSimulacao
        );
    }

    public static AgregacaoResponse toAgregacao(List<Simulacao> simulacoes) {
        if (simulacoes.isEmpty()) {
            return AgregacaoResponse.vazio();
        }

        ResumoGeral resumoGeral = montarResumoGeral(simulacoes);

        List<AgregacaoPorTipo> porTipo = simulacoes.stream()
                .collect(Collectors.groupingBy(s -> s.tipoProduto))
                .entrySet().stream()
                .map(e -> montarAgregacaoPorTipo(e.getKey().name(), e.getValue()))
                .sorted(Comparator.comparing(AgregacaoPorTipo::tipoProduto))
                .toList();

        Money valorTotalInvestido = Money.of(resumoGeral.valorTotalInvestido());
        List<DistribuicaoRisco> distribuicaoRisco = simulacoes.stream()
                .collect(Collectors.groupingBy(s -> s.produto.risco))
                .entrySet().stream()
                .map(e -> {
                    Money valorRisco = CalculoAgregacao.somarValoresInvestidos(e.getValue());
                    BigDecimal percentual = valorRisco
                            .dividir(valorTotalInvestido)
                            .multiplicar(Money.of("100"))
                            .arredondar();
                    return new DistribuicaoRisco(e.getKey().name(), percentual, e.getValue().size());
                })
                .sorted(Comparator.comparing(DistribuicaoRisco::risco))
                .toList();

        Destaques destaques = montarDestaques(simulacoes);

        return new AgregacaoResponse(resumoGeral, porTipo, distribuicaoRisco, destaques);
    }

    private static ResumoGeral montarResumoGeral(List<Simulacao> simulacoes) {
        int total = simulacoes.size();
        Money totalInvestido = CalculoAgregacao.somarValoresInvestidos(simulacoes);
        Money totalProjetado = CalculoAgregacao.somarValoresFinais(simulacoes);
        Money rendimentoTotal = totalProjetado.subtrair(totalInvestido);

        return new ResumoGeral(
                total,
                totalInvestido.arredondar(),
                totalProjetado.arredondar(),
                rendimentoTotal.arredondar(),
                CalculoAgregacao.calcularRendimentoPercentualMedio(simulacoes),
                CalculoAgregacao.calcularTicketMedio(totalInvestido, total),
                CalculoAgregacao.calcularPrazoMedioPonderado(simulacoes, totalInvestido)
        );
    }

    private static AgregacaoPorTipo montarAgregacaoPorTipo(String tipo, List<Simulacao> simulacoes) {
        int total = simulacoes.size();
        Money totalInvestido = CalculoAgregacao.somarValoresInvestidos(simulacoes);
        Money totalProjetado = CalculoAgregacao.somarValoresFinais(simulacoes);
        Money rendimentoTotal = totalProjetado.subtrair(totalInvestido);

        return new AgregacaoPorTipo(
                tipo, total,
                totalInvestido.arredondar(),
                totalProjetado.arredondar(),
                rendimentoTotal.arredondar(),
                CalculoAgregacao.calcularRendimentoPercentualMedio(simulacoes),
                CalculoAgregacao.calcularTicketMedio(totalInvestido, total),
                CalculoAgregacao.calcularPrazoMedio(simulacoes)
        );
    }

    private static Destaques montarDestaques(List<Simulacao> simulacoes) {
        Simulacao maiorRendimento = CalculoAgregacao.encontrarMaiorRendimento(simulacoes);
        Simulacao maiorValorFinal = CalculoAgregacao.encontrarMaiorValorFinal(simulacoes);
        Map.Entry<String, Long> favorito = CalculoAgregacao.encontrarProdutoFavorito(simulacoes);

        return new Destaques(
                toDestaque(maiorRendimento),
                toDestaque(maiorValorFinal),
                new ProdutoFavorito(favorito.getKey(), favorito.getValue().intValue())
        );
    }

    private static SimulacaoDestaque toDestaque(Simulacao s) {
        return new SimulacaoDestaque(
                s.id,
                s.produtoNome,
                s.valorInvestido,
                s.valorFinal,
                CalculoAgregacao.calcularRendimentoPercentual(s).arredondar(),
                s.prazoMeses
        );
    }
}
