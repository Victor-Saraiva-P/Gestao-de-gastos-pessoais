package br.com.gestorfinanceiro.services.impl;

import br.com.gestorfinanceiro.exceptions.despesa.DespesaNotFoundException;
import br.com.gestorfinanceiro.exceptions.despesa.DespesaOperationException;
import br.com.gestorfinanceiro.exceptions.despesa.InvalidDataException;
import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.repositories.DespesaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.DespesaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DespesaServiceImpl implements DespesaService {

    private final DespesaRepository despesaRepository;
    private final UserRepository userRepository;

    public DespesaServiceImpl(DespesaRepository despesaRepository, UserRepository userRepository) {
        this.despesaRepository = despesaRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public DespesaEntity criarDespesa(DespesaEntity despesa, String userId) {
        if (despesa == null) {
            throw new InvalidDataException("A despesa não pode ser nula.");
        }

        if (despesa.getValor() == null || despesa.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDataException("O valor da despesa deve ser maior que zero.");
        }

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found."));

        try {
            despesa.setUser(user);
            return despesaRepository.save(despesa);
        } catch (Exception e) {
            throw new DespesaOperationException("Erro ao criar despesa. Por favor, tente novamente.", e);
        }
    }

    @Override
    public List<DespesaEntity> listarDespesasUsuario(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidDataException("O userId não pode ser nulo ou vazio.");
        }

        try {
            return despesaRepository.findAllByUserUuid(userId);
        } catch (Exception e) {
            throw new DespesaOperationException("Erro ao listar despesas. Por favor, tente novamente.", e);
        }
    }

    @Override
    public DespesaEntity buscarDespesaPorId(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new InvalidDataException("O UUID não pode ser nulo ou vazio.");
        }

        return despesaRepository.findById(uuid)
                .orElseThrow(() -> new DespesaNotFoundException("Despesa com UUID " + uuid + " não encontrada"));
    }


    @Override
    @Transactional
    public DespesaEntity atualizarDespesa(String uuid, DespesaEntity despesaAtualizada) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new InvalidDataException("O UUID não pode ser nulo ou vazio.");
        }

        if (despesaAtualizada == null) {
            throw new InvalidDataException("Os dados da despesa não podem ser nulos.");
        }

        try {
            DespesaEntity despesa = despesaRepository.findById(uuid)
                    .orElseThrow(() -> new DespesaNotFoundException("Despesa com UUID " + uuid + " não encontrada"));

            despesa.setData(despesaAtualizada.getData());
            despesa.setCategoria(despesaAtualizada.getCategoria());
            despesa.setValor(despesaAtualizada.getValor());
            despesa.setDestinoPagamento(despesaAtualizada.getDestinoPagamento());
            despesa.setObservacoes(despesaAtualizada.getObservacoes());

            return despesaRepository.save(despesa);
        } catch (Exception e) {
            throw new DespesaOperationException("Erro ao atualizar despesa. Por favor, tente novamente.", e);
        }
    }

    @Override
    @Transactional
    public void excluirDespesa(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new InvalidDataException("O UUID não pode ser nulo ou vazio.");
        }

        try {
            DespesaEntity despesa = despesaRepository.findById(uuid)
                    .orElseThrow(() -> new DespesaNotFoundException("Despesa com UUID " + uuid + " não encontrada"));

            despesaRepository.delete(despesa);
        } catch (Exception e) {
            throw new DespesaOperationException("Erro ao excluir despesa. Por favor, tente novamente.", e);
        }
    }
}