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

    public String toNormalCase() {
        String name = this.name();
        name = name.replace("_", " ");
        return Character.toUpperCase(name.charAt(0)) + name.substring(1)
                .toLowerCase();
    }
}
