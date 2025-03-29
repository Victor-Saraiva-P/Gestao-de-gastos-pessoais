package br.com.gestorfinanceiro.exceptions.categoria;

public class CategoriaNameNotFoundException extends RuntimeException {
    public CategoriaNameNotFoundException(String categoriaNome) {
        super("Categoria com o nome '" + categoriaNome + "' não encontrada.");
    }
}
