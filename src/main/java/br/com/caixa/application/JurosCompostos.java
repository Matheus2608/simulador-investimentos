package br.com.caixa.application;

import br.com.caixa.domain.Produto;
import br.com.caixa.util.Money;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

@ApplicationScoped
public class JurosCompostos implements EstrategiaCalculo {

    private static final BigDecimal MESES_ANO = BigDecimal.valueOf(12);

    @Override
    public BigDecimal calcular(BigDecimal valor, int prazoMeses, Produto produto) {
        Money taxaMensal = Money.of(produto.rentabilidadeAnual).dividir(MESES_ANO);
        Money fator = taxaMensal.somar(BigDecimal.ONE).pow(prazoMeses);
        return Money.of(valor).multiplicar(fator).arredondar();
    }
}
