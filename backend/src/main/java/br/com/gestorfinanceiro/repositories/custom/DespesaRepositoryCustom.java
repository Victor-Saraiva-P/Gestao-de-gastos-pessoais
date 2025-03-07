package br.com.gestorfinanceiro.repositories.custom;

import br.com.gestorfinanceiro.models.DespesaEntity;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

@Repository
public interface DespesaRepositoryCustom {
    List<DespesaEntity>findByUserAndYearMonthRange (String userId, YearMonth inicio, YearMonth fim);
}
