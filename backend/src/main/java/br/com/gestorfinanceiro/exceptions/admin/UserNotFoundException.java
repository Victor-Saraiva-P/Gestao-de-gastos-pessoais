package br.com.gestorfinanceiro.exceptions.admin;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String uuid) {
        super("Usuário com UUID " + uuid + " não encontrada");
    }
}
