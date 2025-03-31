package br.com.gestorfinanceiro.dto.despesa;

import br.com.gestorfinanceiro.dto.common.TransacaoCreateDTO;
import jakarta.validation.constraints.NotBlank;

public class DespesaCreateDTO extends TransacaoCreateDTO {
    @NotBlank(message = "A origemDoPagamento é obrigatória.")
    private String destinoPagamento;

    // Construtores

    /**
     * Construtor padrão necessário para frameworks de serialização/desserialização.
     */
    public DespesaCreateDTO() {
        // Construtor vazio necessário para desserialização JSON
    }

    // Getters and Setters
    public String getDestinoPagamento() {
        return destinoPagamento;
    }

    public void setDestinoPagamento(String destinoPagamento) {
        this.destinoPagamento = destinoPagamento;
    }
}
