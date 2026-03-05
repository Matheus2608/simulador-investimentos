package br.com.caixa.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public record Money(BigDecimal valor) {

    private static final MathContext MC = MathContext.DECIMAL128;
    private static final int SCALE_APRESENTACAO = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        if (valor == null) {
            throw new IllegalArgumentException("valor nao pode ser nulo");
        }
    }

    public static Money of(BigDecimal valor) {
        return new Money(valor);
    }

    public static Money of(String valor) {
        return new Money(new BigDecimal(valor));
    }

    public Money multiplicar(BigDecimal fator) {
        return new Money(valor.multiply(fator, MC));
    }

    public Money multiplicar(Money fator) {
        return multiplicar(fator.valor);
    }

    public Money dividir(BigDecimal divisor) {
        return new Money(valor.divide(divisor, MC));
    }

    public Money dividir(Money divisor) {
        return dividir(divisor.valor);
    }

    public Money somar(BigDecimal parcela) {
        return new Money(valor.add(parcela, MC));
    }

    public Money somar(Money outro) {
        return somar(outro.valor);
    }

    public Money subtrair(BigDecimal parcela) {
        return new Money(valor.subtract(parcela, MC));
    }

    public Money subtrair(Money outro) {
        return subtrair(outro.valor);
    }

    public Money pow(int expoente) {
        return new Money(valor.pow(expoente, MC));
    }

    public BigDecimal arredondar() {
        return valor.setScale(SCALE_APRESENTACAO, ROUNDING);
    }

    public BigDecimal toBigDecimal() {
        return valor;
    }
}
