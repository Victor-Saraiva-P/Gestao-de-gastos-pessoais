package br.com.gestorfinanceiro.dto;

import java.math.BigDecimal;
import java.util.Map;

public record GraficoPizzaDTO(Map<String, BigDecimal> categorias) {}
