package br.com.gestorfinanceiro.exceptions.auth.register;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Username " + username + " já cadastrado");
    }
}
