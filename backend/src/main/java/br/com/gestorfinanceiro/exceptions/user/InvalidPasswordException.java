package br.com.gestorfinanceiro.exceptions.user;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Senha inválida");
    }
}
