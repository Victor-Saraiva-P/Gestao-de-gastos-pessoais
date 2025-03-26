package br.com.gestorfinanceiro.dto.common;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class TransacaoDTO {
    @NotBlank(message = "A categoria customizada é obrigatória.")
    private String categoria;

    @NotNull(message = "A data é obrigatória.")
    protected LocalDate data;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    protected BigDecimal valor;

    @NotBlank(message = "As observações são obrigatórias.")
    protected String observacoes;

    protected String uuid;

    // Construtores
    public TransacaoDTO(String categoria, LocalDate data, BigDecimal valor, String observacoes, String uuid) {
        this.categoria = categoria;
        this.data = data;
        this.valor = valor;
        this.observacoes = observacoes;
        this.uuid = uuid;
    }

    public TransacaoDTO() {
    }

    // Getters and Setters

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}