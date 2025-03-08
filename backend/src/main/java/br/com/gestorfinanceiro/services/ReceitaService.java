package br.com.gestorfinanceiro.services;

import br.com.gestorfinanceiro.dto.GraficoPizzaDTO;
import br.com.gestorfinanceiro.models.ReceitaEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReceitaService {
    ReceitaEntity criarReceita(ReceitaEntity receita, String userId);

    List<ReceitaEntity> listarReceitasUsuario(String userId);

    ReceitaEntity buscarReceitaPorId(String uuid);

    ReceitaEntity atualizarReceita(String uuid, ReceitaEntity receitaAtualizada);

    void excluirReceita(String uuid);

    GraficoPizzaDTO gerarGraficoPizza(String userId, LocalDate inicio, LocalDate fim);
}
