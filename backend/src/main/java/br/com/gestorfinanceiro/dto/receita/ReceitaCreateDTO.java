package br.com.gestorfinanceiro.dto.receita;

import br.com.gestorfinanceiro.dto.common.TransacaoCreateDTO;
import jakarta.validation.constraints.NotBlank;

public class ReceitaCreateDTO extends TransacaoCreateDTO {
    @NotBlank(message = "A origemDoPagamento é obrigatória.")
    private String origemDoPagamento;

    // Construtores

    /**
     * Construtor padrão necessário para frameworks de serialização/desserialização.
     */
    public ReceitaCreateDTO() {
        // Construtor vazio necessário para desserialização JSON
    }

    // Getters and Setters

    public String getOrigemDoPagamento() {
        return origemDoPagamento;
    }

    public void setOrigemDoPagamento(String origemDoPagamento) {
        this.origemDoPagamento = origemDoPagamento;
    }
}
