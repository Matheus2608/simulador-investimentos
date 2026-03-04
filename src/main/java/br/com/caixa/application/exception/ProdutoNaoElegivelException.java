package br.com.caixa.application.exception;

public class ProdutoNaoElegivelException extends RuntimeException {

    public ProdutoNaoElegivelException(String mensagem) {
        super(mensagem);
    }
}
