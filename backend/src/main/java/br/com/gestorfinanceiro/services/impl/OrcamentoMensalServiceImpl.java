package br.com.gestorfinanceiro.services.impl;

import br.com.gestorfinanceiro.exceptions.categoria.CategoriaNameNotFoundException;
import br.com.gestorfinanceiro.exceptions.orcamentomensal.OrcamentoMensalAlreadyExistsException;
import br.com.gestorfinanceiro.exceptions.orcamentomensal.OrcamentoMensalNotFoundException;
import br.com.gestorfinanceiro.exceptions.orcamentomensal.OrcamentoMensalOperationException;
import br.com.gestorfinanceiro.exceptions.common.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.common.InvalidUuidException;
import br.com.gestorfinanceiro.exceptions.user.UserNotFoundException;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.OrcamentoMensalEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.repositories.CategoriaRepository;
import br.com.gestorfinanceiro.repositories.OrcamentoMensalRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.OrcamentoMensalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
public class OrcamentoMensalServiceImpl implements OrcamentoMensalService {

    private final OrcamentoMensalRepository orcamentoMensalRepository;
    private final CategoriaRepository categoriaRepository;
    private final UserRepository userRepository;

    public OrcamentoMensalServiceImpl(OrcamentoMensalRepository orcamentoMensalRepository,
                                      UserRepository userRepository,
                                      CategoriaRepository categoriaRepository) {
        this.orcamentoMensalRepository = orcamentoMensalRepository;
        this.userRepository = userRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<OrcamentoMensalEntity> listarTodosPorUsuario(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidUuidException();
        }

        List<OrcamentoMensalEntity> orcamentosMensais = orcamentoMensalRepository.findByUserId(userId);

        if (orcamentosMensais.isEmpty()) {
            throw new OrcamentoMensalNotFoundException();
        }

        return orcamentosMensais;
    }

    @Override
    public List<OrcamentoMensalEntity> listarPorPeriodo(String userId, YearMonth periodo) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidUuidException();
        }

        if (periodo == null) {
            throw new InvalidDataException("O período não pode ser nulo.");
        }

        List<OrcamentoMensalEntity> orcamentosMensais = orcamentoMensalRepository.findByPeriodo(periodo);

        if (orcamentosMensais.isEmpty()) {
            throw new OrcamentoMensalNotFoundException("Nenhum orçamento encontrado para o período: " + periodo);
        }

        return orcamentosMensais;
    }

    @Override
    public OrcamentoMensalEntity buscarPorId(String userId, String uuid) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidUuidException();
        }

        if (uuid == null || uuid.trim().isEmpty()) {
            throw new InvalidUuidException();
        }

        return orcamentoMensalRepository.findByUuidAndUserUuid(uuid, userId)
                .orElseThrow(() -> new OrcamentoMensalNotFoundException(uuid));
    }

    @Override
    @Transactional
    public OrcamentoMensalEntity criarOrcamentoMensal(String userId, String categoria, BigDecimal valorLimite, YearMonth periodo) {
        validarParametros(userId, categoria, valorLimite, periodo);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        CategoriaEntity categoriaEntity = categoriaRepository.findByNomeAndUserUuid(categoria, userId)
                .orElseThrow(() -> new CategoriaNameNotFoundException("Categoria não encontrada: " + categoria));

        verificarOrcamentoDuplicado(userId, categoriaEntity, periodo, null);

        try {
            OrcamentoMensalEntity orcamentoMensal = new OrcamentoMensalEntity();
            orcamentoMensal.setCategoria(categoriaEntity);
            orcamentoMensal.setValorLimite(valorLimite);
            orcamentoMensal.setPeriodo(periodo);
            orcamentoMensal.setUser(user);

            return orcamentoMensalRepository.save(orcamentoMensal);
        } catch (Exception e) {
            throw new OrcamentoMensalOperationException("Erro ao criar orçamento mensal. Por favor, tente novamente.", e);
        }
    }

    @Override
    @Transactional
    public OrcamentoMensalEntity atualizarOrcamentoMensal(String userId, String uuid, String categoria, BigDecimal valorLimite, YearMonth periodo) {
        validarParametros(userId, categoria, valorLimite, periodo);

        if (uuid == null || uuid.trim().isEmpty()) {
            throw new InvalidUuidException();
        }

        OrcamentoMensalEntity orcamentoMensal = buscarPorId(userId, uuid);
        CategoriaEntity categoriaEntity = categoriaRepository.findByNomeAndUserUuid(categoria, userId)
                .orElseThrow(() -> new CategoriaNameNotFoundException("Categoria não encontrada: " + categoria));

        verificarOrcamentoDuplicado(userId, categoriaEntity, periodo, uuid);

        try {
            orcamentoMensal.setCategoria(categoriaEntity);
            orcamentoMensal.setValorLimite(valorLimite);
            orcamentoMensal.setPeriodo(periodo);

            return orcamentoMensalRepository.save(orcamentoMensal);
        } catch (Exception e) {
            throw new OrcamentoMensalOperationException("Erro ao atualizar orçamento mensal. Por favor, tente novamente.", e);
        }
    }

    @Override
    @Transactional
    public void excluirOrcamentoMensal(String userId, String uuid) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidUuidException();
        }

        if (uuid == null || uuid.trim().isEmpty()) {
            throw new InvalidUuidException();
        }

        OrcamentoMensalEntity orcamentoMensal = buscarPorId(userId, uuid);

        try {
            orcamentoMensalRepository.delete(orcamentoMensal);
        } catch (Exception e) {
            throw new OrcamentoMensalOperationException("Erro ao excluir orçamento mensal. Por favor, tente novamente.", e);
        }
    }

    private void validarParametros(String userId, String categoria, BigDecimal valorLimite, YearMonth periodo) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidUuidException();
        }

        if (categoria == null || categoria.trim().isEmpty()) {
            throw new InvalidDataException("A categoria não pode ser nula ou vazia.");
        }

        if (valorLimite == null || valorLimite.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDataException("O valor limite deve ser maior que zero.");
        }

        if (periodo == null) {
            throw new InvalidDataException("O período não pode ser nulo.");
        }
    }

    private void verificarOrcamentoDuplicado(String userId, CategoriaEntity categoria, YearMonth periodo, String uuidAtual) {
        orcamentoMensalRepository.findByCategoriaAndPeriodoAndUserUuid(categoria, periodo, userId)
                .filter(existing -> uuidAtual == null || !existing.getUuid().equals(uuidAtual))
                .ifPresent(existing -> {
                    throw new OrcamentoMensalAlreadyExistsException(categoria, periodo);
                });
    }
}