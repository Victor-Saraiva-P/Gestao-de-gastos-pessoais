package br.com.gestorfinanceiro.exceptions.user;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Username " + username + " já cadastrado");
    }
}
