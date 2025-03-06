package br.com.gestorfinanceiro.exceptions.receita;

public class ReceitaNotFoundException extends RuntimeException {
    public ReceitaNotFoundException(String message) {
        super(message);
    }
}