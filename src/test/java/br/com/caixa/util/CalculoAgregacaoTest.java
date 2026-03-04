package br.com.caixa.util;

import br.com.caixa.domain.Simulacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CalculoAgregacaoTest {

    @Test
    void deveSomarValoresInvestidos() {
        List<Simulacao> simulacoes = List.of(
                criarSimulacao("10000", "11000", 12),
                criarSimulacao("20000", "23000", 24)
        );
        Money resultado = CalculoAgregacao.somarValoresInvestidos(simulacoes);
        assertThat(resultado.arredondar()).isEqualByComparingTo("30000");
    }

    @Test
    void deveSomarValoresFinais() {
        List<Simulacao> simulacoes = List.of(
                criarSimulacao("10000", "11000", 12),
                criarSimulacao("20000", "23000", 24)
        );
        Money resultado = CalculoAgregacao.somarValoresFinais(simulacoes);
        assertThat(resultado.arredondar()).isEqualByComparingTo("34000");
    }

    @Test
    void deveCalcularRendimentoPercentual() {
        Simulacao s = criarSimulacao("10000", "11268.25", 12);
        Money rendimento = CalculoAgregacao.calcularRendimentoPercentual(s);
        assertThat(rendimento.arredondar()).isEqualByComparingTo("12.68");
    }

    @Test
    void deveCalcularRendimentoPercentualMedio() {
        List<Simulacao> simulacoes = List.of(
                criarSimulacao("10000", "11000", 12),
                criarSimulacao("10000", "12000", 12)
        );
        BigDecimal media = CalculoAgregacao.calcularRendimentoPercentualMedio(simulacoes);
        assertThat(media).isEqualByComparingTo("15.00");
    }

    @Test
    void deveCalcularTicketMedio() {
        Money total = Money.of("30000");
        BigDecimal ticket = CalculoAgregacao.calcularTicketMedio(total, 3);
        assertThat(ticket).isEqualByComparingTo("10000.00");
    }

    @Test
    void deveCalcularPrazoMedioPonderado() {
        List<Simulacao> simulacoes = List.of(
                criarSimulacao("10000", "11000", 12),
                criarSimulacao("30000", "33000", 24)
        );
        Money totalInvestido = Money.of("40000");
        int prazo = CalculoAgregacao.calcularPrazoMedioPonderado(simulacoes, totalInvestido);
        assertThat(prazo).isEqualTo(21);
    }

    @Test
    void deveCalcularPrazoMedio() {
        List<Simulacao> simulacoes = List.of(
                criarSimulacao("10000", "11000", 6),
                criarSimulacao("10000", "12000", 18)
        );
        int prazo = CalculoAgregacao.calcularPrazoMedio(simulacoes);
        assertThat(prazo).isEqualTo(12);
    }

    @Test
    void deveEncontrarMaiorRendimento() {
        Simulacao s1 = criarSimulacao("10000", "11000", 12);
        Simulacao s2 = criarSimulacao("10000", "12000", 12);
        Simulacao resultado = CalculoAgregacao.encontrarMaiorRendimento(List.of(s1, s2));
        assertThat(resultado.valorFinal).isEqualByComparingTo("12000");
    }

    @Test
    void deveEncontrarMaiorValorFinal() {
        Simulacao s1 = criarSimulacao("10000", "11000", 12);
        Simulacao s2 = criarSimulacao("50000", "55000", 12);
        Simulacao resultado = CalculoAgregacao.encontrarMaiorValorFinal(List.of(s1, s2));
        assertThat(resultado.valorFinal).isEqualByComparingTo("55000");
    }

    @Test
    void deveEncontrarProdutoFavorito() {
        Simulacao s1 = criarSimulacao("10000", "11000", 12);
        s1.produtoNome = "CDB CAIXA 2026";
        Simulacao s2 = criarSimulacao("10000", "12000", 12);
        s2.produtoNome = "CDB CAIXA 2026";
        Simulacao s3 = criarSimulacao("10000", "11500", 12);
        s3.produtoNome = "LCI CAIXA";

        Map.Entry<String, Long> favorito = CalculoAgregacao.encontrarProdutoFavorito(List.of(s1, s2, s3));
        assertThat(favorito.getKey()).isEqualTo("CDB CAIXA 2026");
        assertThat(favorito.getValue()).isEqualTo(2);
    }

    private Simulacao criarSimulacao(String valorInvestido, String valorFinal, int prazoMeses) {
        Simulacao s = new Simulacao();
        s.valorInvestido = new BigDecimal(valorInvestido);
        s.valorFinal = new BigDecimal(valorFinal);
        s.prazoMeses = prazoMeses;
        s.produtoNome = "Produto Teste";
        return s;
    }
}
