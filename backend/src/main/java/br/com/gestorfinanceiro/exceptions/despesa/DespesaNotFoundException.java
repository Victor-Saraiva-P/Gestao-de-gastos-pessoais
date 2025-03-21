package br.com.gestorfinanceiro.exceptions.despesa;

public class DespesaNotFoundException extends RuntimeException {
    public DespesaNotFoundException(String uuid) {
        super("Nenhuma despesa encontrada para o usuário de ID: " + uuid + ".");
    }
}