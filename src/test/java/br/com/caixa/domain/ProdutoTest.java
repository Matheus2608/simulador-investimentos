package br.com.caixa.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProdutoTest {

    @Test
    void deveSerElegivelQuandoDentroDoRange() {
        Produto produto = criarProduto();
        assertThat(produto.isElegivel(new BigDecimal("10000"), 12)).isTrue();
    }

    @Test
    void deveSerElegivelNoLimiteMinimo() {
        Produto produto = criarProduto();
        assertThat(produto.isElegivel(new BigDecimal("1000"), 3)).isTrue();
    }

    @Test
    void deveSerElegivelNoLimiteMaximo() {
        Produto produto = criarProduto();
        assertThat(produto.isElegivel(new BigDecimal("500000"), 36)).isTrue();
    }

    @Test
    void naoDeveSerElegivelComValorAbaixoDoMinimo() {
        Produto produto = criarProduto();
        assertThat(produto.isElegivel(new BigDecimal("999.99"), 12)).isFalse();
    }

    @Test
    void naoDeveSerElegivelComValorAcimaDoMaximo() {
        Produto produto = criarProduto();
        assertThat(produto.isElegivel(new BigDecimal("500000.01"), 12)).isFalse();
    }

    @Test
    void naoDeveSerElegivelComPrazoAbaixoDoMinimo() {
        Produto produto = criarProduto();
        assertThat(produto.isElegivel(new BigDecimal("10000"), 2)).isFalse();
    }

    @Test
    void naoDeveSerElegivelComPrazoAcimaDoMaximo() {
        Produto produto = criarProduto();
        assertThat(produto.isElegivel(new BigDecimal("10000"), 37)).isFalse();
    }

    private Produto criarProduto() {
        Produto p = new Produto();
        p.nome = "CDB CAIXA 2026";
        p.tipoProduto = TipoProduto.CDB;
        p.rentabilidadeAnual = new BigDecimal("0.12");
        p.risco = Risco.BAIXO;
        p.prazoMinMeses = 3;
        p.prazoMaxMeses = 36;
        p.valorMin = new BigDecimal("1000");
        p.valorMax = new BigDecimal("500000");
        return p;
    }
}
