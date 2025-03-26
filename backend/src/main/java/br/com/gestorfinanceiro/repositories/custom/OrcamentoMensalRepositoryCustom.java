package br.com.gestorfinanceiro.repositories.custom;

import br.com.gestorfinanceiro.models.OrcamentoMensalEntity;
import br.com.gestorfinanceiro.models.enums.DespesasCategorias;

import java.time.YearMonth;
import java.util.Optional;

public interface OrcamentoMensalRepositoryCustom {
    Optional<OrcamentoMensalEntity> findByCategoriaAndPeriodoAndUserUuid(DespesasCategorias categoria, YearMonth periodo, String userId);
}
