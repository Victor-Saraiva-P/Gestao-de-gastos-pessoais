package br.com.gestorfinanceiro.exceptions.categoria;

public class CategoriaAcessDeniedException extends RuntimeException {
    public CategoriaAcessDeniedException(String categoriaId) {
        super("Acesso a categoria" + categoriaId + " negado: esta categoria não pertence a você");
    }
}
