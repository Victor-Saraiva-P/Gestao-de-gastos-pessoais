package br.com.gestorfinanceiro.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Problema {

    private int status;
    private String type;
    private String title;
    private String detail;

    // Construtor padrão
    public Problema() {
    }

    // Construtor completo
    public Problema(int status, String type, String title, String detail, LocalDateTime dataHora) {
        this.status = status;
        this.type = type;
        this.title = title;
        this.detail = detail;
    }

    // Implementação manual do Builder
    public static ProblemaBuilder builder() {
        return new ProblemaBuilder();
    }

    public int getStatus() {
        return status;
    }

    // ----- Getters e Setters -----

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public static class ProblemaBuilder {
        private int status;
        private String type;
        private String title;
        private String detail;
        private LocalDateTime dataHora;

        public ProblemaBuilder status(int status) {
            this.status = status;
            return this;
        }

        public ProblemaBuilder type(String type) {
            this.type = type;
            return this;
        }

        public ProblemaBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ProblemaBuilder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public ProblemaBuilder dataHora(LocalDateTime dataHora) {
            this.dataHora = dataHora;
            return this;
        }

        public ProblemaBuilder mensagem(String mensagem) {
            this.detail = mensagem;
            return this;
        }

        public Problema build() {
            return new Problema(status, type, title, detail, dataHora);
        }
    }
}