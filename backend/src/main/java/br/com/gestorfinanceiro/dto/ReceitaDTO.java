package br.com.gestorfinanceiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReceitaDTO {
    @NotNull(message = "A data é obrigatória.")
    private LocalDate data;

    @NotBlank(message = "A categoria é obrigatória.")
    @Pattern(
            regexp = "SALARIO|RENDIMENTO_DE_INVESTIMENTO|COMISSOES|BONUS|BOLSA_DE_ESTUDOS",
            message = "Categoria inválida. Valores permitidos: SALARIO, RENDIMENTO_DE_INVESTIMENTO, COMISSOES, BONUS, BOLSA_DE_ESTUDOS."
    )
    private String categoria;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    private BigDecimal valor;

    @NotBlank(message = "A origem do pagamento é obrigatória.")
    private String origemDoPagamento;

    @NotBlank(message = "As observações são obrigatórias.")
    private String observacoes;

    private String uuid; // Pegar o ID para rotas específicas

    // Getters and Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getOrigemDoPagamento() {
        return origemDoPagamento;
    }

    public void setOrigemDoPagamento(String origemDoPagamento) {
        this.origemDoPagamento = origemDoPagamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
