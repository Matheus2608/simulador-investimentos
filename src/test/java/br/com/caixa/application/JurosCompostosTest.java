package br.com.caixa.application;

import br.com.caixa.domain.Produto;
import br.com.caixa.domain.Risco;
import br.com.caixa.domain.TipoProduto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class JurosCompostosTest {

    // nao e usado a anotacao de inject porque seria necessario ser um quarkus test
    // e como essa classe nao precisa de um servidor para ser testada
    // sua instancia e feita de forma simples com o construtor
    JurosCompostos jurosCompostos = new JurosCompostos();

    @Test
    void deveCalcularJurosCompostosParaCDB() {
        // 10000 * (1 + 0.12/12)^12 = 10000 * 1.01^12 = 11268.25
        Produto produto = criarProduto(TipoProduto.CDB, "0.12");
        BigDecimal resultado = jurosCompostos.calcular(new BigDecimal("10000"), 12, produto);
        assertThat(resultado).isEqualByComparingTo("11268.25");
    }

    @Test
    void deveCalcularParaLCI() {
        // 5000 * (1 + 0.095/12)^6
        Produto produto = criarProduto(TipoProduto.LCI, "0.095");
        BigDecimal resultado = jurosCompostos.calcular(new BigDecimal("5000"), 6, produto);
        assertThat(resultado).isGreaterThan(new BigDecimal("5000"));
    }

    @Test
    void deveCalcularParaLCA() {
        // 100000 * (1 + 0.10/12)^24
        Produto produto = criarProduto(TipoProduto.LCA, "0.10");
        BigDecimal resultado = jurosCompostos.calcular(new BigDecimal("100000"), 24, produto);
        assertThat(resultado).isGreaterThan(new BigDecimal("100000"));
    }

    @Test
    void deveRetornarValorOriginalParaPrazoZero() {
        Produto produto = criarProduto(TipoProduto.CDB, "0.12");
        BigDecimal resultado = jurosCompostos.calcular(new BigDecimal("10000"), 0, produto);
        assertThat(resultado).isEqualByComparingTo("10000.00");
    }

    @Test
    void deveArredondarComDuasCasasDecimais() {
        Produto produto = criarProduto(TipoProduto.CDB, "0.135");
        BigDecimal resultado = jurosCompostos.calcular(new BigDecimal("7777"), 7, produto);
        assertThat(resultado.scale()).isEqualTo(2);
    }

    private Produto criarProduto(TipoProduto tipo, String rentabilidade) {
        Produto p = new Produto();
        p.nome = "Produto Teste";
        p.tipoProduto = tipo;
        p.rentabilidadeAnual = new BigDecimal(rentabilidade);
        p.risco = Risco.BAIXO;
        p.prazoMinMeses = 1;
        p.prazoMaxMeses = 60;
        p.valorMin = new BigDecimal("100");
        p.valorMax = new BigDecimal("1000000");
        return p;
    }
}
