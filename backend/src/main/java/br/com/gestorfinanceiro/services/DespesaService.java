package br.com.gestorfinanceiro.services;

import br.com.gestorfinanceiro.models.DespesaEntity;

import java.time.YearMonth;
import java.util.List;
import br.com.gestorfinanceiro.dto.GraficoBarraDTO;

public interface DespesaService {

    DespesaEntity criarDespesa(DespesaEntity despesa, String userId);

    List<DespesaEntity> listarDespesasUsuario(String userId);

    DespesaEntity buscarDespesaPorId(String uuid);

    DespesaEntity atualizarDespesa(String uuid, DespesaEntity despesaAtualizada);

    void excluirDespesa(String uuid);

    GraficoBarraDTO gerarGraficoBarras(String userId, YearMonth inicio, YearMonth fim);
}
