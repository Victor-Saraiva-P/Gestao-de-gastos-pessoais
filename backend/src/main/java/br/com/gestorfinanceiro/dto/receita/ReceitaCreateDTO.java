package br.com.gestorfinanceiro.dto.receita;

import br.com.gestorfinanceiro.dto.common.TransacaoCreateDTO;
import jakarta.validation.constraints.NotBlank;

public class ReceitaCreateDTO extends TransacaoCreateDTO {
    @NotBlank(message = "A origemDoPagamento é obrigatória.")
    private String origemDoPagamento;

    // Construtores
    public ReceitaCreateDTO() {
    }

    // Getters and Setters

    public String getOrigemDoPagamento() {
        return origemDoPagamento;
    }

    public void setOrigemDoPagamento(String origemDoPagamento) {
        this.origemDoPagamento = origemDoPagamento;
    }
}
