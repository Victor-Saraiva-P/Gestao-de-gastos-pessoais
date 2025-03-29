package br.com.gestorfinanceiro.dto.categoria;

public class CategoriaDTO {
    private String uuid;
    private String nome;
    private String tipo;
    private String userUuid;

    // Construtores
    public CategoriaDTO() {
    }

    public CategoriaDTO(String uuid, String nome, String tipo, String userUuid) {
        this.uuid = uuid;
        this.nome = nome;
        this.tipo = tipo;
        this.userUuid = userUuid;
    }

    // Getters and Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }
}
