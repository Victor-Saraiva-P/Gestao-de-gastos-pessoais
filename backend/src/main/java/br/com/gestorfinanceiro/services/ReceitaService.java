package br.com.gestorfinanceiro.services;

import br.com.gestorfinanceiro.dto.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.GraficoPizzaDTO;
import br.com.gestorfinanceiro.models.ReceitaEntity;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface ReceitaService {
    ReceitaEntity criarReceita(ReceitaEntity receita, String userId);

    List<ReceitaEntity> listarReceitasUsuario(String userId);

    ReceitaEntity buscarReceitaPorId(String uuid);

    ReceitaEntity atualizarReceita(String uuid, ReceitaEntity receitaAtualizada);

    void excluirReceita(String uuid);

    GraficoPizzaDTO gerarGraficoPizza(String userId, LocalDate inicio, LocalDate fim);

    GraficoBarraDTO gerarGraficoBarras(String userId, YearMonth inicio, YearMonth fim);
}
