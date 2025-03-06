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
}
