package br.com.gestorfinanceiro.dto.categoria;

import jakarta.validation.constraints.NotBlank;

public class CategoriaUpdateDTO {
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    // Construtores


    public CategoriaUpdateDTO(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
