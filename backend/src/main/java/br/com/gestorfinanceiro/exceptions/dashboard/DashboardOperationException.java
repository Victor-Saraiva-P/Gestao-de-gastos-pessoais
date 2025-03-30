package br.com.gestorfinanceiro.exceptions.dashboard;

public class DashboardOperationException extends RuntimeException {

    public DashboardOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
