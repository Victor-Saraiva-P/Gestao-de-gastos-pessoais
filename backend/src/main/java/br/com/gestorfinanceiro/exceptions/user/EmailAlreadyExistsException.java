package br.com.gestorfinanceiro.exceptions.user;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email " + email + " já cadastrado");
    }
}
