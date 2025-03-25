package br.com.gestorfinanceiro.services.impl;

import br.com.gestorfinanceiro.dto.categoria.CategoriaCreateDTO;
import br.com.gestorfinanceiro.dto.categoria.CategoriaUpdateDTO;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaAcessDeniedException;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaAlreadyExistsException;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaNotFoundException;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaOperationException;
import br.com.gestorfinanceiro.exceptions.common.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.user.UserNotFoundException;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.repositories.CategoriaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.CategoriaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UserRepository userRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, UserRepository userRepository) {
        this.categoriaRepository = categoriaRepository;
        this.userRepository = userRepository;
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

    // TODO: ver se o frontend realmente necessita de todas as categorias
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
                .orElseThrow(() -> new CategoriaNotFoundException(categoriaId));

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
                .orElseThrow(() -> new CategoriaNotFoundException(categoriaId));

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

        // Exclui a categoria
        try {
            categoriaRepository.delete(categoria);
        } catch (Exception e) {
            throw new CategoriaOperationException();
        }
    }
}
