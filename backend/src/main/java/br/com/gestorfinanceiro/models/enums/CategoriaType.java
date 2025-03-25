package br.com.gestorfinanceiro.models.enums;

public enum CategoriaType {
    RECEITAS,
    DESPESAS;

    @Override
    public String toString() {
        return this.name();
    }
}
