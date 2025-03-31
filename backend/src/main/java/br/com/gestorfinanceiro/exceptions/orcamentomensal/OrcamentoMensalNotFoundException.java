package br.com.gestorfinanceiro.exceptions.orcamentomensal;

public class OrcamentoMensalNotFoundException extends RuntimeException {

    public OrcamentoMensalNotFoundException(String uuid) {
        super(String.format("Orçamento mensal com UUID %s não encontrado", uuid));
    }

    public OrcamentoMensalNotFoundException() {
        super("Nenhum orçamento mensal encontrado para o usuário logado");
    }
}
