package br.com.gestorfinanceiro.services;

import br.com.gestorfinanceiro.dto.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.GraficoPizzaDTO;
import br.com.gestorfinanceiro.models.DespesaEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface DespesaService {

    DespesaEntity criarDespesa(DespesaEntity despesa, String userId);

    List<DespesaEntity> listarDespesasUsuario(String userId);

    DespesaEntity buscarDespesaPorId(String uuid);

    DespesaEntity atualizarDespesa(String uuid, DespesaEntity despesaAtualizada);

    void excluirDespesa(String uuid);

    GraficoBarraDTO gerarGraficoBarras(String userId, YearMonth inicio, YearMonth fim);

    GraficoPizzaDTO gerarGraficoPizza(String userId, LocalDate inicio, LocalDate fim);

    List<DespesaEntity> buscarDespesasPorIntervaloDeDatas(String userId, LocalDate inicio, LocalDate fim);

    List<DespesaEntity> buscarDespesasPorIntervaloDeValores(String userId, BigDecimal min, BigDecimal max);
}
