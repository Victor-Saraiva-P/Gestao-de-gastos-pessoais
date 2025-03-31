package br.com.gestorfinanceiro.services.impl;

import br.com.gestorfinanceiro.exceptions.dashboard.DashboardOperationException;
import br.com.gestorfinanceiro.exceptions.user.InvalidUserIdException;
import br.com.gestorfinanceiro.exceptions.user.UserNotFoundException;
import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.repositories.DespesaRepository;
import br.com.gestorfinanceiro.repositories.ReceitaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.DashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;

    private final DespesaRepository despesaRepository;

    private final ReceitaRepository receitaRepository;

    public DashboardServiceImpl(UserRepository userRepository, DespesaRepository despesaRepository, ReceitaRepository receitaRepository) {
        this.userRepository = userRepository;
        this.despesaRepository = despesaRepository;
        this.receitaRepository = receitaRepository;
    }


    @Override
    public BigDecimal getSaldoTotal(String userId, YearMonth yearMonth) {
        validateUserId(userId);
        validateYearMonth(yearMonth);

        try {
            BigDecimal totalReceitas = receitaRepository.sumReceitasByUserIdAndYearMonth(
                    userId, yearMonth.getYear(), yearMonth.getMonthValue());

            BigDecimal totalDespesas = despesaRepository.sumDespesasByUserIdAndYearMonth(
                    userId, yearMonth.getYear(), yearMonth.getMonthValue());

            return totalReceitas.subtract(totalDespesas);
        } catch (Exception e) {
            throw new DashboardOperationException("Erro ao calcular saldo total. Por favor, tente novamente.", e);
        }
    }

    @Override
    public DespesaEntity getMaiorDespesa(String userId, YearMonth yearMonth) {
        validateUserId(userId);
        validateYearMonth(yearMonth);

        try {
            return despesaRepository.findTopByUserIdAndYearMonthOrderByValorDesc(
                    userId, yearMonth.getYear(), yearMonth.getMonthValue());
        } catch (Exception e) {
            throw new DashboardOperationException("Erro ao buscar maior despesa. Por favor, tente novamente.", e);
        }
    }

    @Override
    public ReceitaEntity getMaiorReceita(String userId, YearMonth yearMonth) {
        validateUserId(userId);
        validateYearMonth(yearMonth);

        try {
            return receitaRepository.findTopByUserIdAndYearMonthOrderByValorDesc(
                    userId, yearMonth.getYear(), yearMonth.getMonthValue());
        } catch (Exception e) {
            throw new DashboardOperationException("Erro ao buscar maior receita. Por favor, tente novamente.", e);
        }
    }

    @Override
    public Map<String, BigDecimal> getCategoriaComMaiorDespesa(String userId, YearMonth yearMonth) {
        validateUserId(userId);
        validateYearMonth(yearMonth);

        try {
            return despesaRepository.findCategoriaWithHighestDespesaByUserIdAndYearMonth(
                    userId, yearMonth.getYear(), yearMonth.getMonthValue());
        } catch (Exception e) {
            throw new DashboardOperationException("Erro ao buscar categoria com maior despesa. Por favor, tente novamente.", e);
        }
    }

    @Override
    public Map<String, BigDecimal> getCategoriaComMaiorReceita(String userId, YearMonth yearMonth) {
        validateUserId(userId);
        validateYearMonth(yearMonth);

        try {
            return receitaRepository.findCategoriaWithHighestReceitaByUserIdAndYearMonth(
                    userId, yearMonth.getYear(), yearMonth.getMonthValue());
        } catch (Exception e) {
            throw new DashboardOperationException("Erro ao buscar categoria com maior receita. Por favor, tente novamente.", e);
        }
    }

    @Override
    public BigDecimal calcularTotalDespesasNoMes(String userId, YearMonth mes) {
        validateUserId(userId);
        validateYearMonth(mes);

        try {
            return despesaRepository.sumDespesasByUserIdAndYearMonth(userId, mes);
        } catch (Exception e) {
            throw new DashboardOperationException("Erro ao calcular total de despesas do mês. Por favor, tente novamente.", e);
        }
    }

    @Override
    public BigDecimal calcularTotalReceitasNoMes(String userId, YearMonth mes) {
        validateUserId(userId);
        validateYearMonth(mes);

        try {
            return receitaRepository.sumReceitasByUserIdAndYearMonth(userId, mes);
        } catch (Exception e) {
            throw new DashboardOperationException("Erro ao calcular total de receitas do mês. Por favor, tente novamente.", e);
        }
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidUserIdException();
        }

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    private void validateYearMonth(YearMonth yearMonth) {
        if (yearMonth == null) {
            throw new IllegalArgumentException("O período (YearMonth) não pode ser nulo");
        }
    }
}
