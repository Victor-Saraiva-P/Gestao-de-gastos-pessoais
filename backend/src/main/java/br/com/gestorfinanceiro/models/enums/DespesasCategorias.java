package br.com.gestorfinanceiro.models.enums;

public enum DespesasCategorias {
    ALIMENTACAO,
    MORADIA,
    TRANSPORTE,
    LAZER;

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
