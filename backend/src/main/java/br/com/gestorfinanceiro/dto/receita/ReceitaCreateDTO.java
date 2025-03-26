package br.com.gestorfinanceiro.dto.receita;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReceitaCreateDTO {
    @NotBlank(message = "A categoria customizada é obrigatória.")
    private String categoriaCustomizada;

    @NotNull(message = "A data é obrigatória.")
    private LocalDate data;

    @NotBlank(message = "A categoria é obrigatória.")
    private String categoria;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    private BigDecimal valor;

    @NotBlank(message = "A origemDoPagamento é obrigatória.")
    private String origemDoPagamento;

    @NotBlank(message = "As observacoes é obrigatória.")
    private String observacoes;

    public ReceitaCreateDTO() {
    }

    // Getters and Setters
    public String getCategoriaCustomizada() {
        return categoriaCustomizada;
    }

    public void setCategoriaCustomizada(String categoriaCustomizada) {
        this.categoriaCustomizada = categoriaCustomizada;
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
