package br.com.gestorfinanceiro.models.enums;

public enum Status {
    ACTIVE,
    INACTIVE;

    @Override
    public String toString() {
        return this.name();
    }
}
