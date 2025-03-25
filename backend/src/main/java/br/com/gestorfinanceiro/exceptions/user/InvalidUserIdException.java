package br.com.gestorfinanceiro.exceptions.user;

public class InvalidUserIdException extends RuntimeException {
    public InvalidUserIdException() {
        super("O userId não pode ser nulo ou vazio");
    }
}
