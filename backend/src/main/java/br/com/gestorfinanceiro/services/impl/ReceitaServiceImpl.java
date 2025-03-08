package br.com.gestorfinanceiro.services.impl;

import br.com.gestorfinanceiro.dto.GraficoPizzaDTO;
import br.com.gestorfinanceiro.exceptions.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.receita.ReceitaNotFoundException;
import br.com.gestorfinanceiro.exceptions.receita.ReceitaOperationException;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.repositories.ReceitaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.repositories.custom.ReceitaRepositoryCustom;
import br.com.gestorfinanceiro.services.ReceitaService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReceitaServiceImpl implements ReceitaService {

    private final ReceitaRepository receitaRepository;
    private final UserRepository userRepository;

    public ReceitaServiceImpl(ReceitaRepository receitaRepository ,UserRepository userRepository) {
        this.receitaRepository = receitaRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ReceitaEntity criarReceita(ReceitaEntity receita, String userId) {
        if (receita == null) {
            throw new InvalidDataException("A receita não pode ser nula.");
        }

        if (receita.getValor() == null || receita.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDataException("O valor da receita deve ser maior que zero.");
        }

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found."));

        try {
            receita.setUser(user);
            return receitaRepository.save(receita);
        } catch (Exception e) {
            throw new ReceitaOperationException("Erro ao criar receita. Por favor, tente novamente.", e);
        }
    }

    @Override
    public List<ReceitaEntity> listarReceitasUsuario(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidDataException("O userId não pode ser nulo ou vazio.");
        }

        try {
            return receitaRepository.findAllByUserUuid(userId);
        } catch (Exception e) {
            throw new ReceitaOperationException("Erro ao listar receitas. Por favor, tente novamente.", e);
        }
    }

    @Override
    public ReceitaEntity buscarReceitaPorId(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new InvalidDataException("O UUID não pode ser nulo ou vazio.");
        }

        return receitaRepository.findById(uuid)
                .orElseThrow(() -> new ReceitaNotFoundException("Receita com UUID " + uuid + " não encontrada"));
    }


    @Override
    @Transactional
    public ReceitaEntity atualizarReceita(String uuid, ReceitaEntity receitaAtualizada) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new InvalidDataException("O UUID não pode ser nulo ou vazio.");
        }

        if (receitaAtualizada == null) {
            throw new InvalidDataException("Os dados da receita não podem ser nulos.");
        }

        try {
            ReceitaEntity receita = receitaRepository.findById(uuid)
                    .orElseThrow(() -> new ReceitaNotFoundException("Receita com UUID " + uuid + " não encontrada"));

            receita.setData(receitaAtualizada.getData());
            receita.setCategoria(receitaAtualizada.getCategoria());
            receita.setValor(receitaAtualizada.getValor());
            receita.setOrigemDoPagamento(receitaAtualizada.getOrigemDoPagamento());
            receita.setObservacoes(receitaAtualizada.getObservacoes());


            return receitaRepository.save(receita);
        } catch (Exception e) {
            throw new ReceitaOperationException("Erro ao atualizar receita. Por favor, tente novamente.", e);
        }
    }

    @Override
    @Transactional
    public void excluirReceita(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new InvalidDataException("O UUID não pode ser nulo ou vazio.");
        }

        try {
            ReceitaEntity receita = receitaRepository.findById(uuid)
                    .orElseThrow(() -> new ReceitaNotFoundException("Receita com UUID " + uuid + " não encontrada"));

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
                        r -> r.getCategoria().name(),
                        Collectors.mapping(ReceitaEntity::getValor, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        return new GraficoPizzaDTO(categorias);
    }
}