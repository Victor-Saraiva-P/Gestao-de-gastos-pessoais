package br.com.gestorfinanceiro.exceptions;

public class InvalidUuidException extends RuntimeException {
    public InvalidUuidException() {
        super("O UUID não pode ser nulo ou vazio");
    }
}
