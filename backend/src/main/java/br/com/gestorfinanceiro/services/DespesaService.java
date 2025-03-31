package br.com.gestorfinanceiro.services;

import br.com.gestorfinanceiro.dto.despesa.DespesaCreateDTO;
import br.com.gestorfinanceiro.dto.despesa.DespesaUpdateDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoPizzaDTO;
import br.com.gestorfinanceiro.models.DespesaEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface DespesaService {

    DespesaEntity criarDespesa(DespesaCreateDTO despesaCreateDTO, String userId);

    List<DespesaEntity> listarDespesasUsuario(String userId);

    DespesaEntity buscarDespesaPorId(String uuid);

    DespesaEntity atualizarDespesa(String uuid, DespesaUpdateDTO despesaUpdateDTO);

    void excluirDespesa(String uuid);

    GraficoBarraDTO gerarGraficoBarras(String userId, YearMonth inicio, YearMonth fim);

    GraficoPizzaDTO gerarGraficoPizza(String userId, LocalDate inicio, LocalDate fim);

    List<DespesaEntity> buscarDespesasPorIntervaloDeDatas(String userId, LocalDate inicio, LocalDate fim);

    List<DespesaEntity> buscarDespesasPorIntervaloDeValores(String userId, BigDecimal min, BigDecimal max);
}
