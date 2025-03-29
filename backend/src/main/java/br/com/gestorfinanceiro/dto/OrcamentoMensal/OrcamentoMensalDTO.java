package br.com.gestorfinanceiro.dto.OrcamentoMensal;

import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.DespesasCategorias;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.YearMonth;

public class OrcamentoMensalDTO {

    @NotNull(message = "O usuário é obrigatório.")
    private UserEntity user;

    @NotNull(message = "A categoria é obrigatória.")
    private String categoria;

    @NotNull(message = "O valor limite é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    private BigDecimal valorLimite;

    @NotNull(message = "O período é obrigatório.")
    private YearMonth periodo;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getValorLimite() {
        return valorLimite;
    }

    public void setValorLimite(BigDecimal valorLimite) {
        this.valorLimite = valorLimite;
    }

    public YearMonth getPeriodo() {
        return periodo;
    }

    public void setPeriodo(YearMonth periodo) {
        this.periodo = periodo;
    }
}
