package br.com.gestorfinanceiro.exceptions.despesa;

public class DespesaOperationException extends RuntimeException {
    public DespesaOperationException(String message) {
        super(message);
    }

    public DespesaOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
