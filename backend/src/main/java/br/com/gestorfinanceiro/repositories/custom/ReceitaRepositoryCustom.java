package br.com.gestorfinanceiro.repositories.custom;

import br.com.gestorfinanceiro.models.ReceitaEntity;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Repository
public interface ReceitaRepositoryCustom {
    List<ReceitaEntity> findByUserAndDateRange(String userId, LocalDate inicio, LocalDate fim);
    List<ReceitaEntity> findByUserAndYearMonthRange (String userId, YearMonth inicio, YearMonth fim);
    List<ReceitaEntity> findByUserAndValueBetween(String userId, BigDecimal min, BigDecimal max);
    BigDecimal sumReceitasByUserIdAndYearMonth(String userId, int year, int month);
    ReceitaEntity findTopByUserIdAndYearMonthOrderByValorDesc(String userId, int year, int month);
    Map<String, BigDecimal> findCategoriaWithHighestReceitaByUserIdAndYearMonth(String userId, int year, int month);
    BigDecimal sumReceitasByUserIdAndYearMonth(String userId, YearMonth yearMonth);
}
