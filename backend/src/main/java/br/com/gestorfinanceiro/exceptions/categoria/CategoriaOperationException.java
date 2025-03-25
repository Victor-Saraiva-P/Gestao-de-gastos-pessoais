package br.com.gestorfinanceiro.exceptions.categoria;

public class CategoriaOperationException extends RuntimeException {
    public CategoriaOperationException(String message) {
        super(message);
    }

    public CategoriaOperationException() {
        super("Erro ao criar categoria. Por favor, tente novamente");
    }
}
