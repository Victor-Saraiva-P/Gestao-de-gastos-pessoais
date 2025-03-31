package br.com.gestorfinanceiro.dto.dashboard;

import java.math.BigDecimal;
import java.time.YearMonth;

public class SaldoTotalDTO {
    private YearMonth periodo;
    private BigDecimal saldo;

    // Construtor
    public SaldoTotalDTO(YearMonth periodo, BigDecimal saldo) {
        this.periodo = periodo;
        this.saldo = saldo;
    }

    // Getters e Setters (necessários para serialização JSON)
    public YearMonth getPeriodo() {
        return periodo;
    }

    public void setPeriodo(YearMonth periodo) {
        this.periodo = periodo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}