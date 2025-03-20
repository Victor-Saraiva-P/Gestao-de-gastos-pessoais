package br.com.gestorfinanceiro.exceptions;

public class MissingUuidException extends RuntimeException {
    public MissingUuidException() {
        super("O UUID não pode ser nulo ou vazio");
    }
}
