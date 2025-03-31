package br.com.gestorfinanceiro.services;

import br.com.gestorfinanceiro.dto.categoria.CategoriaCreateDTO;
import br.com.gestorfinanceiro.dto.categoria.CategoriaUpdateDTO;
import br.com.gestorfinanceiro.models.CategoriaEntity;

import java.util.List;

public interface CategoriaService {
    CategoriaEntity criarCategoria(CategoriaCreateDTO categoriaCreateDTO, String userId);

    List<CategoriaEntity> listarCategorias(String userId);

    List<CategoriaEntity> listarCategoriasDespesas(String userId);

    List<CategoriaEntity> listarCategoriasReceitas(String userId);

    CategoriaEntity atualizarCategoria(String uuid, CategoriaUpdateDTO novaCategoria, String userId);

    void excluirCategoria(String uuid, String userId);

    CategoriaEntity criarSemCategoria(String userId, String tipo);
}
