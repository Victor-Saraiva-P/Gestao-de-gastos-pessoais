package br.com.gestorfinanceiro.dto.receita;

import br.com.gestorfinanceiro.dto.common.TransacaoDTO;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReceitaDTO extends TransacaoDTO {
    @NotBlank(message = "A origem do pagamento é obrigatória.")
    private String origemDoPagamento;

    // Construtores
    public ReceitaDTO(String categoria, LocalDate data, BigDecimal valor, String observacoes, String uuid, String origemDoPagamento) {
        super(categoria, data, valor, observacoes, uuid);
        this.origemDoPagamento = origemDoPagamento;
    }

    public ReceitaDTO() {
    }

    // Getters and Setters

    public String getOrigemDoPagamento() {
        return origemDoPagamento;
    }

    public void setOrigemDoPagamento(String origemDoPagamento) {
        this.origemDoPagamento = origemDoPagamento;
    }
}