package br.com.gestorfinanceiro.exceptions.categoria;

public class CategoriaNotFoundException extends RuntimeException {
    public CategoriaNotFoundException(String categoriaId) {
        super("Categoria com id " + categoriaId + " não encontrada.");
    }
}
