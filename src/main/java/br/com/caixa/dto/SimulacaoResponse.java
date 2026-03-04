package br.com.caixa.dto;

import br.com.caixa.domain.Risco;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SimulacaoResponse(
        ProdutoValidado produtoValidado,
        ResultadoSimulacao resultadoSimulacao,
        LocalDateTime dataSimulacao
) {

    public record ProdutoValidado(
            Long id,
            String nome,
            String tipo,
            BigDecimal rentabilidade,
            Risco risco
    ) {
    }

    public record ResultadoSimulacao(
            BigDecimal valorFinal,
            Integer prazoMeses
    ) {
    }
}
