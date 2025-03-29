package br.com.gestorfinanceiro.services;

import br.com.gestorfinanceiro.dto.grafico.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoPizzaDTO;
import br.com.gestorfinanceiro.dto.receita.ReceitaCreateDTO;
import br.com.gestorfinanceiro.dto.receita.ReceitaUpdateDTO;
import br.com.gestorfinanceiro.models.ReceitaEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface ReceitaService {
    ReceitaEntity criarReceita(ReceitaCreateDTO receitaCreateDTO, String userId);

    List<ReceitaEntity> listarReceitasUsuario(String userId);

    ReceitaEntity buscarReceitaPorId(String uuid);

    ReceitaEntity atualizarReceita(String uuid, ReceitaUpdateDTO receitaUpdateDTO);

    void excluirReceita(String uuid);

    GraficoPizzaDTO gerarGraficoPizza(String userId, LocalDate inicio, LocalDate fim);

    GraficoBarraDTO gerarGraficoBarras(String userId, YearMonth inicio, YearMonth fim);

    List<ReceitaEntity> buscarReceitasPorIntervaloDeDatas(String userId, LocalDate inicio, LocalDate fim);

    List<ReceitaEntity> buscarReceitasPorIntervaloDeValores(String userId, BigDecimal min, BigDecimal max);
}
