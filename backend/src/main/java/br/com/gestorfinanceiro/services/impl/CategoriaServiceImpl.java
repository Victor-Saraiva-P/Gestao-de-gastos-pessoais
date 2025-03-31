package br.com.gestorfinanceiro.services.impl;

import br.com.gestorfinanceiro.dto.categoria.CategoriaCreateDTO;
import br.com.gestorfinanceiro.dto.categoria.CategoriaUpdateDTO;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaAcessDeniedException;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaAlreadyExistsException;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaIdNotFoundException;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaOperationException;
import br.com.gestorfinanceiro.exceptions.common.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.user.UserNotFoundException;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.repositories.CategoriaRepository;
import br.com.gestorfinanceiro.repositories.DespesaRepository;
import br.com.gestorfinanceiro.repositories.ReceitaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.CategoriaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UserRepository userRepository;
    private final DespesaRepository despesaRepository;
    private final ReceitaRepository receitaRepository;


    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, UserRepository userRepository, DespesaRepository despesaRepository, ReceitaRepository receitaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.userRepository = userRepository;
        this.despesaRepository = despesaRepository;
        this.receitaRepository = receitaRepository;
    }

    @Override
    public CategoriaEntity criarCategoria(CategoriaCreateDTO categoriaCreateDTO, String userId) {
        // Verifica se categoriaCreateDTO é nulo
        if (categoriaCreateDTO == null) {
            throw new InvalidDataException("Passar a categoria é obrigatório.");
        }

        // Validar os campos de categoriaCreateDTO

        // Nome
        if (categoriaCreateDTO.getNome() == null || categoriaCreateDTO.getNome()
                .isBlank()) {
            throw new InvalidDataException("O nome é obrigatório.");
        }

        // Tipo
        if (categoriaCreateDTO.getTipo() == null || categoriaCreateDTO.getTipo()
                .isBlank()) {
            throw new InvalidDataException("O tipo é obrigatório.");
        }

        // Verificar se o usuário existe
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Verificar se a categoria já existe
        categoriaRepository.findByNomeAndTipoAndUserUuid(categoriaCreateDTO.getNome(), categoriaCreateDTO.getTipoEnum(),
                        userId)
                .ifPresent(categoria -> {
                    throw new CategoriaAlreadyExistsException(categoriaCreateDTO.getNome());
                });

        // Criar a categoria
        try {
            CategoriaEntity novaCategoria = new CategoriaEntity(
                    categoriaCreateDTO.getNome(),
                    categoriaCreateDTO.getTipoEnum(),
                    user
            );
            return categoriaRepository.save(novaCategoria);
        } catch (Exception e) {
            throw new CategoriaOperationException();
        }
    }

    @Override
    public List<CategoriaEntity> listarCategorias(String userId) {
        // Verificar se o usuário existe
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return categoriaRepository.findAllByUserUuid(userId);
    }

    @Override
    public List<CategoriaEntity> listarCategoriasDespesas(String userId) {
        // Verificar se o usuário existe
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return categoriaRepository.findAllByUserUuidAndTipo(userId, CategoriaType.DESPESAS);
    }

    @Override
    public List<CategoriaEntity> listarCategoriasReceitas(String userId) {
        // Verificar se o usuário existe
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return categoriaRepository.findAllByUserUuidAndTipo(userId, CategoriaType.RECEITAS);
    }


    @Override
    public CategoriaEntity atualizarCategoria(String categoriaId, CategoriaUpdateDTO novaCategoria, String userId) {
        // Validações de entrada

        // Valida o DTO
        if (novaCategoria == null) {
            throw new InvalidDataException("Passar a categoria é obrigatório.");
        }

        // Valida o nome
        if (novaCategoria.getNome() == null || novaCategoria.getNome()
                .isBlank()) {
            throw new InvalidDataException("O nome é obrigatório.");
        }

        // Valida se a categoria existe
        CategoriaEntity categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new CategoriaIdNotFoundException(categoriaId));

        // Valida se o usuário é o dono da categoria
        if (!categoria.getUser()
                .getUuid()
                .equals(userId)) {
            throw new CategoriaAcessDeniedException(categoriaId);
        }

        // Valida se o novo nome já existe
        categoriaRepository.findByNomeAndTipoAndUserUuid(novaCategoria.getNome(), categoria.getTipo(), userId)
                .ifPresent(c -> {
                    throw new CategoriaAlreadyExistsException(novaCategoria.getNome());
                });

        // Atualiza a categoria
        try {
            categoria.setNome(novaCategoria.getNome());
            return categoriaRepository.save(categoria);
        } catch (Exception e) {
            throw new CategoriaOperationException();
        }
    }

    @Override
    public void excluirCategoria(String categoriaId, String userId) {
        // Verifica se o categoriaId é valido
        if (categoriaId == null || categoriaId.isBlank()) {
            throw new InvalidDataException("O id da categoria é obrigatório.");
        }

        // Verifica se a categoria existe
        CategoriaEntity categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new CategoriaIdNotFoundException(categoriaId));

        // Verifica se o usuário é o dono da categoria
        if (!categoria.getUser()
                .getUuid()
                .equals(userId)) {
            throw new CategoriaAcessDeniedException(categoriaId);
        }

        // Verifica se ela é a sem categoria
        if (categoria.isSemCategoria()) {
            throw new CategoriaOperationException("A categoria 'sem categoria' não pode ser excluída.");
        }

        try {
            // Buscar a categoria "Sem Categoria" correspondente
            CategoriaEntity semCategoria = categoriaRepository
                    .findByIsSemCategoriaAndTipoAndUserUuid(true, categoria.getTipo(), userId)
                    .orElseGet(() -> criarSemCategoria(userId, categoria.getTipo()
                            .name()));

            // Atualizar todas as referências dependendo do tipo da categoria
            if (categoria.getTipo() == CategoriaType.DESPESAS) {
                despesaRepository.findAllByCategoria(categoria)
                        .forEach(despesa -> {
                            despesa.setCategoria(semCategoria);
                            despesaRepository.save(despesa);
                        });
            } else if (categoria.getTipo() == CategoriaType.RECEITAS) {
                receitaRepository.findAllByCategoria(categoria)
                        .forEach(receita -> {
                            receita.setCategoria(semCategoria);
                            receitaRepository.save(receita);
                        });
            }

            // Exclui a categoria após atualizar todas as referências
            categoriaRepository.delete(categoria);
        } catch (Exception e) {
            throw new CategoriaOperationException("Erro ao excluir categoria: " + e.getMessage());
        }
    }

    @Override
    public CategoriaEntity criarSemCategoria(String userId, String tipo) {
        // Verifica se o usuário existe
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Verifica se o tipo é válido
        if (tipo == null || tipo.isBlank()) {
            throw new InvalidDataException("O tipo é obrigatório.");
        }

        // Verifica se a categoria 'sem categoria' já existe com o boolean isSemCategoria = true
        categoriaRepository.findByIsSemCategoriaAndTipoAndUserUuid(true, CategoriaType.valueOf(tipo), userId)
                .ifPresent(c -> {
                    throw new CategoriaAlreadyExistsException("Sem Categoria");
                });

        // Cria a categoria 'sem categoria'
        try {
            CategoriaEntity semCategoria = new CategoriaEntity(
                    "Sem Categoria",
                    CategoriaType.valueOf(tipo),
                    user
            );
            semCategoria.setSemCategoria(true);
            return categoriaRepository.save(semCategoria);
        } catch (Exception e) {
            throw new CategoriaOperationException();
        }
    }
}
