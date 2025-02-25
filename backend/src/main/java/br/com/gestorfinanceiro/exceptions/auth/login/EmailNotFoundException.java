package br.com.gestorfinanceiro.exceptions.auth.login;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String email) {
        super("Email " + email + " não encontrado");
    }
}
