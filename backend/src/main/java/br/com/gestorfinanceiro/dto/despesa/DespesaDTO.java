package br.com.gestorfinanceiro.dto.despesa;

import br.com.gestorfinanceiro.dto.common.TransacaoDTO;
import jakarta.validation.constraints.NotBlank;

public class DespesaDTO extends TransacaoDTO {
    @NotBlank(message = "O destino do pagamento é obrigatória.")
    private String destinoPagamento;

    // Getters and Setters
    public String getDestinoPagamento() {
        return destinoPagamento;
    }

    public void setDestinoPagamento(String destinoPagamento) {
        this.destinoPagamento = destinoPagamento;
    }
}