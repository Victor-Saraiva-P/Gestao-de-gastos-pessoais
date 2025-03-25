package br.com.gestorfinanceiro.exceptions.user;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String email) {
        super("Email " + email + " não encontrado");
    }
}
