package br.com.gestorfinanceiro.dto.categoria;

import br.com.gestorfinanceiro.models.enums.CategoriaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CategoriaCreateDTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O tipo é obrigatório.")
    @Pattern(
            regexp = "RECEITA|DESPESA",
            message = "Tipo inválido. Valores permitidos: RECEITA, DESPESA."
    )
    private String tipo;

    // Construtores
    public CategoriaCreateDTO(String nome, String tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }

    // Getters and Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public CategoriaType getTipoEnum() {
        return CategoriaType.valueOf(tipo);
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
