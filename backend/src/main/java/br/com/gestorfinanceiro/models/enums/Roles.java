package br.com.gestorfinanceiro.models.enums;

public enum Roles {

    ADMIN,
    USER;

    @Override
    public String toString() {
        return this.name();
    }

}