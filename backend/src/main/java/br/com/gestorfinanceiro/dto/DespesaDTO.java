package br.com.gestorfinanceiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DespesaDTO {

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

    @NotBlank(message = "O destino do pagamento é obrigatória.")
    private String destinoPagamento;

    @NotBlank(message = "As observações são obrigatórias.")
    private String observacoes;

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

    public String getDestinoPagamento() {
        return destinoPagamento;
    }

    public void setDestinoPagamento(String destinoPagamento) {
        this.destinoPagamento = destinoPagamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
