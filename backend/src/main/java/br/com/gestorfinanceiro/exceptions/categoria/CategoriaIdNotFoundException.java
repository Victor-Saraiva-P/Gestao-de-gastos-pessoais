package br.com.gestorfinanceiro.exceptions.categoria;

public class CategoriaIdNotFoundException extends RuntimeException {
    public CategoriaIdNotFoundException(String categoriaId) {
        super("Categoria com id " + categoriaId + " não encontrada.");
    }
}
