package br.com.gestorfinanceiro.dto;

import java.math.BigDecimal;
import java.util.Map;

public record GraficoPizzaDTO(Map<String, BigDecimal> categorias) {
    public static GraficoPizzaDTO from(Map<String, BigDecimal> categorias) {
        return new GraficoPizzaDTO(categorias);
    }
}
