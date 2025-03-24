package br.com.gestorfinanceiro.exceptions;

public enum ProblemaType {
    ERRO_DE_AUTENTICACAO("Erro de autenticação", "/erro-de-autenticacao"),
    ERRO_DE_SISTEMA("Erro de sistema", "/erro-de-sistema"),
    DADOS_INVALIDOS("Dados inválidos", "/dados-invalidos");


    private final String title;
    private final String uri;

    ProblemaType(String title, String path) {
        this.title = title;
        this.uri = "https://gestor-financeiro.com" + path;
    }

    public String getTitle() {
        return title;
    }

    public String getUri() {
        return uri;
    }
}
