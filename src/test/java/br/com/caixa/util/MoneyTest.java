package br.com.caixa.util;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void deveCriarAPartirDeBigDecimal() {
        Money money = Money.of(new BigDecimal("100.50"));
        assertThat(money.toBigDecimal()).isEqualByComparingTo("100.50");
    }

    @Test
    void deveCriarAPartirDeString() {
        Money money = Money.of("250.75");
        assertThat(money.toBigDecimal()).isEqualByComparingTo("250.75");
    }

    @Test
    void deveLancarExcecaoParaValorNulo() {
        assertThatThrownBy(() -> Money.of((BigDecimal) null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveMultiplicarComBigDecimal() {
        Money resultado = Money.of("100").multiplicar(new BigDecimal("1.5"));
        assertThat(resultado.toBigDecimal()).isEqualByComparingTo("150");
    }

    @Test
    void deveMultiplicarComMoney() {
        Money resultado = Money.of("100").multiplicar(Money.of("2"));
        assertThat(resultado.toBigDecimal()).isEqualByComparingTo("200");
    }

    @Test
    void deveDividirComBigDecimal() {
        Money resultado = Money.of("100").dividir(new BigDecimal("3"));
        assertThat(resultado.toBigDecimal().doubleValue()).isCloseTo(33.3333, org.assertj.core.data.Offset.offset(0.001));
    }

    @Test
    void deveDividirComMoney() {
        Money resultado = Money.of("100").dividir(Money.of("4"));
        assertThat(resultado.toBigDecimal()).isEqualByComparingTo("25");
    }

    @Test
    void deveSomarComBigDecimal() {
        Money resultado = Money.of("100").somar(new BigDecimal("50.25"));
        assertThat(resultado.toBigDecimal()).isEqualByComparingTo("150.25");
    }

    @Test
    void deveSomarComMoney() {
        Money resultado = Money.of("100").somar(Money.of("75"));
        assertThat(resultado.toBigDecimal()).isEqualByComparingTo("175");
    }

    @Test
    void deveCalcularPotencia() {
        Money resultado = Money.of("1.01").pow(12);
        assertThat(resultado.toBigDecimal().doubleValue()).isCloseTo(1.12682503, org.assertj.core.data.Offset.offset(0.00001));
    }

    @Test
    void deveArredondarParaDuasCasasComHalfEven() {
        Money money = Money.of("100.555");
        assertThat(money.arredondar()).isEqualByComparingTo("100.56");

        Money money2 = Money.of("100.545");
        assertThat(money2.arredondar()).isEqualByComparingTo("100.54");
    }

    @Test
    void deveManterPrecisaoDuranteOperacoesEncadeadas() {
        // simula: 10000 * (1 + 0.12/12)^12
        Money taxaMensal = Money.of("0.12").dividir(new BigDecimal("12"));
        Money fator = taxaMensal.somar(BigDecimal.ONE).pow(12);
        BigDecimal resultado = Money.of("10000").multiplicar(fator).arredondar();

        assertThat(resultado).isEqualByComparingTo("11268.25");
    }
}
