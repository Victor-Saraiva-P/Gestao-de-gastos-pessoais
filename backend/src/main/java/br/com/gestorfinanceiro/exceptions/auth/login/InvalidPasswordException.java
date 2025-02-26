package br.com.gestorfinanceiro.exceptions.auth.login;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Senha inválida");
    }
}
