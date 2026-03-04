package br.com.caixa.util;

import br.com.caixa.domain.Simulacao;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class CalculoAgregacao {

    private CalculoAgregacao() {
    }

    public static Money somarValoresInvestidos(List<Simulacao> simulacoes) {
        return simulacoes.stream()
                .map(s -> Money.of(s.valorInvestido))
                .reduce(Money.of(BigDecimal.ZERO), Money::somar);
    }

    public static Money somarValoresFinais(List<Simulacao> simulacoes) {
        return simulacoes.stream()
                .map(s -> Money.of(s.valorFinal))
                .reduce(Money.of(BigDecimal.ZERO), Money::somar);
    }

    public static Money calcularRendimentoPercentual(Simulacao s) {
        return Money.of(s.valorFinal)
                .dividir(s.valorInvestido)
                .multiplicar(BigDecimal.valueOf(100))
                .somar(BigDecimal.valueOf(-100));
    }

    public static BigDecimal calcularRendimentoPercentualMedio(List<Simulacao> simulacoes) {
        BigDecimal soma = simulacoes.stream()
                .map(s -> calcularRendimentoPercentual(s).toBigDecimal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Money.of(soma)
                .dividir(BigDecimal.valueOf(simulacoes.size()))
                .arredondar();
    }

    public static BigDecimal calcularTicketMedio(Money totalInvestido, int totalSimulacoes) {
        return totalInvestido.dividir(BigDecimal.valueOf(totalSimulacoes)).arredondar();
    }

    public static int calcularPrazoMedioPonderado(List<Simulacao> simulacoes, Money totalInvestido) {
        BigDecimal somaPrazoPonderado = simulacoes.stream()
                .map(s -> s.valorInvestido.multiply(BigDecimal.valueOf(s.prazoMeses)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Money.of(somaPrazoPonderado)
                .dividir(totalInvestido.toBigDecimal())
                .arredondar().intValue();
    }

    public static int calcularPrazoMedio(List<Simulacao> simulacoes) {
        int soma = simulacoes.stream().mapToInt(s -> s.prazoMeses).sum();
        return soma / simulacoes.size();
    }

    public static Simulacao encontrarMaiorRendimento(List<Simulacao> simulacoes) {
        return simulacoes.stream()
                .max(Comparator.comparing(s -> calcularRendimentoPercentual(s).toBigDecimal()))
                .orElseThrow();
    }

    public static Simulacao encontrarMaiorValorFinal(List<Simulacao> simulacoes) {
        return simulacoes.stream()
                .max(Comparator.comparing(s -> s.valorFinal))
                .orElseThrow();
    }

    public static Map.Entry<String, Long> encontrarProdutoFavorito(List<Simulacao> simulacoes) {
        return simulacoes.stream()
                .collect(Collectors.groupingBy(s -> s.produtoNome, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow();
    }
}
