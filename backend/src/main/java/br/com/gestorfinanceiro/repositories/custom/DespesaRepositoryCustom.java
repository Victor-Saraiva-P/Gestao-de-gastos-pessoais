package br.com.gestorfinanceiro.repositories.custom;

import br.com.gestorfinanceiro.models.DespesaEntity;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Repository
public interface DespesaRepositoryCustom {
    List<DespesaEntity>findByUserAndYearMonthRange (String userId, YearMonth inicio, YearMonth fim);
    List<DespesaEntity> findByUserAndDateRange(String userId, LocalDate inicio, LocalDate fim);
    List<DespesaEntity> findByUserAndValueBetween(String userId, BigDecimal min, BigDecimal max);
    BigDecimal sumDespesasByUserIdAndYearMonth(String userId, int year, int month);
    DespesaEntity findTopByUserIdAndYearMonthOrderByValorDesc(String userId, int year, int month);
    Map<String, BigDecimal> findCategoriaWithHighestDespesaByUserIdAndYearMonth(String userId, int year, int month);
    BigDecimal sumDespesasByUserIdAndYearMonth(String userId, YearMonth yearMonth);
}
