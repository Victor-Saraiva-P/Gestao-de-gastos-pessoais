package br.com.gestorfinanceiro.exceptions.receita;

public class ReceitaNotFoundException extends RuntimeException {
    public ReceitaNotFoundException(String uuid) {
        super("Receita com UUID " + uuid + " não encontrada");
    }

}