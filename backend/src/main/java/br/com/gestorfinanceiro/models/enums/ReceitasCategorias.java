package br.com.gestorfinanceiro.models.enums;

public enum ReceitasCategorias {
    SALARIO,
    RENDIMENTO_DE_INVESTIMENTO,
    COMISSOES,
    BONUS,
    BOLSA_DE_ESTUDOS;

    @Override
    public String toString() {
        return this.name();
    }
}
