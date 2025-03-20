package br.com.gestorfinanceiro.exceptions.despesa;

public class DespesaNotFoundException extends RuntimeException {
    public DespesaNotFoundException(String uuid) {
        super("Despesa com UUID " + uuid + " não encontrada");
    }
}