package br.com.gestorfinanceiro.exceptions.categoria;

public class CategoriaAlreadyExistsException extends RuntimeException {
    public CategoriaAlreadyExistsException(String nome) {
        super("Categoria " + nome + " já cadastrada");
    }
}
