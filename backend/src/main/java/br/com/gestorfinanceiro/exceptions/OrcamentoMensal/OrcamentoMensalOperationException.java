package br.com.gestorfinanceiro.exceptions.OrcamentoMensal;

public class OrcamentoMensalOperationException extends RuntimeException {
    public OrcamentoMensalOperationException(String message) {
        super(message);
    }

    public OrcamentoMensalOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
