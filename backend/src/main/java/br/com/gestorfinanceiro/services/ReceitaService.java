package br.com.gestorfinanceiro.services;

import br.com.gestorfinanceiro.models.ReceitaEntity;

import java.util.List;

public interface ReceitaService {
    ReceitaEntity criarReceita(ReceitaEntity receita, String userId);

    List<ReceitaEntity> listarReceitasUsuario(String userId);

    ReceitaEntity buscarReceitaPorId(String uuid);

    ReceitaEntity atualizarReceita(String uuid, ReceitaEntity receitaAtualizada);

    void excluirReceita(String uuid);
}
