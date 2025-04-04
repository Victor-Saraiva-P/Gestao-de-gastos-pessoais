package br.com.gestorfinanceiro.services.impl;

import br.com.gestorfinanceiro.dto.grafico.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoPizzaDTO;
import br.com.gestorfinanceiro.dto.receita.ReceitaCreateDTO;
import br.com.gestorfinanceiro.dto.receita.ReceitaUpdateDTO;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaNameNotFoundException;
import br.com.gestorfinanceiro.exceptions.common.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.common.InvalidUuidException;
import br.com.gestorfinanceiro.exceptions.receita.ReceitaNotFoundException;
import br.com.gestorfinanceiro.exceptions.receita.ReceitaOperationException;
import br.com.gestorfinanceiro.exceptions.user.InvalidUserIdException;
import br.com.gestorfinanceiro.exceptions.user.UserNotFoundException;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.repositories.CategoriaRepository;
import br.com.gestorfinanceiro.repositories.ReceitaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.ReceitaService;
import br.com.gestorfinanceiro.utils.DataUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReceitaServiceImpl implements ReceitaService {

    private final ReceitaRepository receitaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UserRepository userRepository;
    private final Mapper<ReceitaEntity, ReceitaCreateDTO> receitaCreateDTOMapper;

    public ReceitaServiceImpl(ReceitaRepository receitaRepository, CategoriaRepository categoriaRepository, UserRepository userRepository, Mapper<ReceitaEntity, ReceitaCreateDTO> receitaCreateDTOMapper) {
        this.receitaRepository = receitaRepository;
        this.categoriaRepository = categoriaRepository;
        this.userRepository = userRepository;
        this.receitaCreateDTOMapper = receitaCreateDTOMapper;
    }

    @Override
    @Transactional
    public ReceitaEntity criarReceita(ReceitaCreateDTO receitaCreateDTO, String userId) {
        // Verifica se o valor da receita é nulo ou menor ou igual a zero
        if (receitaCreateDTO.getValor() == null || receitaCreateDTO.getValor()
                .compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDataException("O valor da receita deve ser maior que zero.");
        }

        // Verifica se o usuario existe
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Verifica se a categoria customizada da receita existe
        CategoriaEntity categoria = categoriaRepository.findByNomeAndTipoAndUserUuid(
                        receitaCreateDTO.getCategoria(),
                        CategoriaType.RECEITAS, userId)
                .orElseThrow(() -> new CategoriaNameNotFoundException(receitaCreateDTO.getCategoria()));

        try {
            ReceitaEntity receitaParaCriar = receitaCreateDTOMapper.mapFrom(receitaCreateDTO);

            // Adiciona a categoria e o usuário à receita
            receitaParaCriar.setCategoria(categoria);
            receitaParaCriar.setUser(user);

            return receitaRepository.save(receitaParaCriar);
        } catch (Exception e) {
            throw new ReceitaOperationException("Erro ao criar receita. Por favor, tente novamente.", e);
        }
    }

    @Override
    public List<ReceitaEntity> listarReceitasUsuario(String userId) {
        if (userId == null || userId.trim()
                .isEmpty()) {
            throw new InvalidUserIdException();
        }

        List<ReceitaEntity> receitas = receitaRepository.findAllByUserUuid(userId);

        if (receitas.isEmpty()) {
            throw new ReceitaNotFoundException(userId);
        } else {
            return receitas;
        }
    }

    @Override
    public ReceitaEntity buscarReceitaPorId(String uuid) {
        if (uuid == null || uuid.trim()
                .isEmpty()) {
            throw new InvalidUuidException();
        }

        return receitaRepository.findById(uuid)
                .orElseThrow(() -> new ReceitaNotFoundException(uuid));
    }


    @Override
    @Transactional
    public ReceitaEntity atualizarReceita(String uuid, ReceitaUpdateDTO receitaUpdateDTO) {
        if (uuid == null || uuid.trim()
                .isEmpty()) {
            throw new InvalidUuidException();
        }

        if (receitaUpdateDTO == null) {
            throw new InvalidDataException("Os dados da receita não podem ser nulos.");
        }

        ReceitaEntity receita = receitaRepository.findById(uuid)
                .orElseThrow(() -> new ReceitaNotFoundException(uuid));

        // Coloca os novos valores na despesa
        receita.setData(receitaUpdateDTO.getData());

        // Verifica se a categoria customizada da despesaAtualizada existe
        receita.setCategoria(
                categoriaRepository.findByNomeAndTipoAndUserUuid(receitaUpdateDTO.getCategoria(),
                                CategoriaType.RECEITAS,
                                receita.getUser()
                                        .getUuid())
                        .orElseThrow(
                                () -> new CategoriaNameNotFoundException(receitaUpdateDTO.getCategoria())));

        receita.setValor(receitaUpdateDTO.getValor());
        receita.setOrigemDoPagamento(receitaUpdateDTO.getOrigemDoPagamento());
        receita.setObservacoes(receitaUpdateDTO.getObservacoes());

        try {
            return receitaRepository.save(receita);
        } catch (Exception e) {
            throw new ReceitaOperationException("Erro ao atualizar despesa. Por favor, tente novamente.", e);
        }
    }

    @Override
    @Transactional
    public void excluirReceita(String uuid) {
        if (uuid == null || uuid.trim()
                .isEmpty()) {
            throw new InvalidUuidException();
        }

        ReceitaEntity receita = receitaRepository.findById(uuid)
                .orElseThrow(() -> new ReceitaNotFoundException(uuid));

        try {
            receitaRepository.delete(receita);
        } catch (Exception e) {
            throw new ReceitaOperationException("Erro ao excluir receita. Por favor, tente novamente.", e);
        }
    }

    @Override
    public GraficoPizzaDTO gerarGraficoPizza(String userId, LocalDate inicio, LocalDate fim) {
        List<ReceitaEntity> receitas = receitaRepository.findByUserAndDateRange(userId, inicio, fim);

        Map<String, BigDecimal> categorias = receitas.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCategoria()
                                .getNome(),
                        Collectors.mapping(ReceitaEntity::getValor,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        return new GraficoPizzaDTO(categorias);
    }

    @Override
    public GraficoBarraDTO gerarGraficoBarras(String userId, YearMonth inicio, YearMonth fim) {
        List<ReceitaEntity> receitas = receitaRepository.findByUserAndYearMonthRange(userId, inicio, fim);
        
        Map<String, BigDecimal> dadosMensais = receitas.stream()
            .collect(Collectors.groupingBy(
                d -> DataUtils.formatarMesAno(d.getData()),
                Collectors.reducing(BigDecimal.ZERO, ReceitaEntity::getValor, BigDecimal::add)
            ));
        
        DataUtils.preencherMesesVazios(dadosMensais, inicio, fim);
        
        return new GraficoBarraDTO(dadosMensais);
    }

    @Override
    public List<ReceitaEntity> buscarReceitasPorIntervaloDeDatas(String userId, LocalDate inicio, LocalDate fim) {
        if (userId == null || userId.trim()
                .isEmpty()) {
            throw new InvalidUserIdException();
        }

        if (inicio == null || fim == null) {
            throw new InvalidDataException("As datas de início e fim não podem ser nulas.");
        }

        if (inicio.isAfter(fim)) {
            throw new InvalidDataException("A data de início não pode ser após a data de fim.");
        }

        try {
            return receitaRepository.findByUserAndDateRange(userId, inicio, fim);
        } catch (Exception e) {
            throw new ReceitaOperationException(
                    "Erro ao buscar receitas por intervalo de datas. Por favor, tente novamente.", e);
        }
    }

    @Override
    public List<ReceitaEntity> buscarReceitasPorIntervaloDeValores(String userId, BigDecimal min, BigDecimal max) {
        if (userId == null || userId.trim()
                .isEmpty()) {
            throw new InvalidUserIdException();
        }

        if (min == null || max == null) {
            throw new InvalidDataException("Os valores mínimo e máximo não podem ser nulos.");
        }

        if (min.compareTo(BigDecimal.ZERO) <= 0 || max.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDataException("Os valores mínimo e máximo devem ser maiores que zero.");
        }

        if (min.compareTo(max) > 0) {
            throw new InvalidDataException("O valor mínimo não pode ser maior que o valor máximo.");
        }

        try {
            return receitaRepository.findByUserAndValueBetween(userId, min, max);
        } catch (Exception e) {
            throw new ReceitaOperationException(
                    "Erro ao buscar receitas por intervalo de valores. Por favor, tente novamente.", e);
        }
    }

}