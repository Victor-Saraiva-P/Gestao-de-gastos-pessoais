package br.com.gestorfinanceiro.exceptions.despesa;

public class DespesaNotFoundException extends RuntimeException {
    public DespesaNotFoundException(String message) {
        super(message);
    }
}