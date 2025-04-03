package br.com.gestorfinanceiro.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class DataUtils {
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter MES_ANO_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", PT_BR);

    public static String formatarMesAno(LocalDate data) {
        return data.format(MES_ANO_FORMATTER).toLowerCase();
    }

    public static void preencherMesesVazios(Map<String, BigDecimal> map, YearMonth inicio, YearMonth fim) {
        YearMonth current = inicio;
        while (!current.isAfter(fim)) {
            String mesAno = current.format(MES_ANO_FORMATTER).toLowerCase();
            map.putIfAbsent(mesAno, BigDecimal.ZERO);
            current = current.plusMonths(1);
        }
    }
}
