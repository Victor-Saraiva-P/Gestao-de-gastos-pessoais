package br.com.gestorfinanceiro.exceptions.receita;

public class ReceitaOperationException extends RuntimeException {
    public ReceitaOperationException(String message) {
        super(message);
    }

    public ReceitaOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
