package br.com.gestorfinanceiro.services.impl;

import br.com.gestorfinanceiro.exceptions.OrcamentoMensal.OrcamentoMensalAlreadyExistsException;
import br.com.gestorfinanceiro.exceptions.OrcamentoMensal.OrcamentoMensalNotFoundException;
import br.com.gestorfinanceiro.exceptions.user.UserNotFoundException;
import br.com.gestorfinanceiro.models.OrcamentoMensalEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.DespesasCategorias;
import br.com.gestorfinanceiro.repositories.OrcamentoMensalRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.OrcamentoMensalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

@Service
public class OrcamentoMensalServiceImpl implements OrcamentoMensalService {

    private final OrcamentoMensalRepository orcamentoMensalRepository;
    private final UserRepository userRepository;

    public OrcamentoMensalServiceImpl(OrcamentoMensalRepository orcamentoMensalRepository,
                                      UserRepository userRepository) {
        this.orcamentoMensalRepository = Objects.requireNonNull(orcamentoMensalRepository,
                "OrcamentoMensalRepository não pode ser nulo");
        this.userRepository = Objects.requireNonNull(userRepository,
                "UserRepository não pode ser nulo");
    }

    @Override
    public List<OrcamentoMensalEntity> listarTodosPorUsuario(String userId) {
        List<OrcamentoMensalEntity> orcamentosMensais = this.orcamentoMensalRepository.findByUserId(userId);
        if (orcamentosMensais.isEmpty()) throw new OrcamentoMensalNotFoundException(userId);
        return orcamentosMensais;
    }

    @Override
    public List<OrcamentoMensalEntity> listarPorPeriodo(String userId, YearMonth periodo) {
        List<OrcamentoMensalEntity> orcamentosMensais = this.orcamentoMensalRepository.findByPeriodo(periodo);
        if (orcamentosMensais.isEmpty()) throw new OrcamentoMensalNotFoundException(userId);
        return orcamentosMensais;
    }

    @Override
    public OrcamentoMensalEntity buscarPorId(String userId, String uuid) {
        return this.orcamentoMensalRepository.findByUuidAndUserUuid(uuid, userId)
                .orElseThrow(() -> new OrcamentoMensalNotFoundException(uuid));
    }

    @Override
    @Transactional
    public OrcamentoMensalEntity criarOrcamentoMensal(String userId, DespesasCategorias categoria,
                                                      BigDecimal valorLimite, YearMonth periodo) {
        validarParametros(userId, categoria, valorLimite, periodo);

        UserEntity user = buscarUsuarioPorId(userId);

        // Verifica se já existe um orçamento mensal para essa categoria e período
        this.orcamentoMensalRepository.findByCategoriaAndPeriodoAndUserUuid(categoria, periodo, userId)
                .ifPresent(orcamento -> {
                    throw new OrcamentoMensalAlreadyExistsException(categoria, periodo);
                });

        // Cria e salva o orçamento
        OrcamentoMensalEntity orcamentoMensal = new OrcamentoMensalEntity();
        orcamentoMensal.setCategoria(categoria);
        orcamentoMensal.setValorLimite(valorLimite);
        orcamentoMensal.setPeriodo(periodo);
        orcamentoMensal.setUser(user);

        return this.orcamentoMensalRepository.save(orcamentoMensal);
    }

    @Override
    @Transactional
    public OrcamentoMensalEntity atualizarOrcamentoMensal(String userId, String uuid,
                                                          DespesasCategorias categoria,
                                                          BigDecimal valorLimite,
                                                          YearMonth periodo) {
        validarParametros(userId, categoria, valorLimite, periodo);
        Objects.requireNonNull(uuid, "UUID não pode ser nulo");

        buscarUsuarioPorId(userId);

        // Busca orçamento e verifica se existe
        OrcamentoMensalEntity orcamentoMensal = buscarPorId(userId, uuid);

        // Verifica se já existe outro orçamento com a mesma categoria e período
        this.orcamentoMensalRepository.findByCategoriaAndPeriodoAndUserUuid(categoria, periodo, userId)
                .filter(existing -> !existing.getUuid().equals(uuid))
                .ifPresent(existing -> {
                    throw new OrcamentoMensalAlreadyExistsException(categoria, periodo);
                });

        // Atualiza e salva
        orcamentoMensal.setCategoria(categoria);
        orcamentoMensal.setValorLimite(valorLimite);
        orcamentoMensal.setPeriodo(periodo);

        return this.orcamentoMensalRepository.save(orcamentoMensal);
    }

    @Override
    @Transactional
    public void excluirOrcamentoMensal(String userId, String uuid) {
        Objects.requireNonNull(userId, "UserId não pode ser nulo");
        Objects.requireNonNull(uuid, "UUID não pode ser nulo");

        buscarUsuarioPorId(userId);

        OrcamentoMensalEntity orcamentoMensal = buscarPorId(userId, uuid);

        this.orcamentoMensalRepository.delete(orcamentoMensal);
    }

    /**
     * Método auxiliar para validar parâmetros obrigatórios.
     */
    private void validarParametros(String userId, DespesasCategorias categoria,
                                   BigDecimal valorLimite, YearMonth periodo) {
        Objects.requireNonNull(userId, "UserId não pode ser nulo");
        Objects.requireNonNull(categoria, "Categoria não pode ser nula");
        Objects.requireNonNull(valorLimite, "ValorLimite não pode ser nulo");
        Objects.requireNonNull(periodo, "Período não pode ser nulo");

        if (valorLimite.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("ValorLimite deve ser maior que zero");
        }
    }

    /**
     * Método auxiliar para buscar um usuário por ID e lançar exceção caso não exista.
     */
    private UserEntity buscarUsuarioPorId(String userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
