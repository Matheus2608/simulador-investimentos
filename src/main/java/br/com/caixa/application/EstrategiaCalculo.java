package br.com.caixa.application;

import br.com.caixa.domain.Produto;

import java.math.BigDecimal;

@FunctionalInterface
public interface EstrategiaCalculo {
    BigDecimal calcular(BigDecimal valor, int prazoMeses, Produto produto);
}
