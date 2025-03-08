package br.com.gestorfinanceiro.repositories.custom;

import br.com.gestorfinanceiro.models.ReceitaEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Repository
public interface ReceitaRepositoryCustom {
    List<ReceitaEntity> findByUserAndDateRange(String userId, LocalDate inicio, LocalDate fim);
    List<ReceitaEntity>findByUserAndYearMonthRange (String userId, YearMonth inicio, YearMonth fim);
}
