package br.com.caixa.mapper;

import br.com.caixa.domain.Produto;
import br.com.caixa.domain.Simulacao;
import br.com.caixa.dto.HistoricoSimulacaoResponse;
import br.com.caixa.dto.SimulacaoResponse;

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
}
