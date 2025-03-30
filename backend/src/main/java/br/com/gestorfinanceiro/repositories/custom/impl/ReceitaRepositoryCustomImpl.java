package br.com.gestorfinanceiro.repositories.custom.impl;

import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.repositories.custom.ReceitaRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static br.com.gestorfinanceiro.repositories.custom.impl.DespesaRepositoryCustomImpl.getStringBigDecimalMap;

@Repository
public class ReceitaRepositoryCustomImpl implements ReceitaRepositoryCustom {

    private static final String USER_ID = "userId";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ReceitaEntity> findByUserAndDateRange(String userId, LocalDate inicio, LocalDate fim) {
        String jpql = "SELECT r FROM ReceitaEntity r WHERE r.user.uuid = :userId AND r.data BETWEEN :inicio AND :fim";

        TypedQuery<ReceitaEntity> query = entityManager.createQuery(jpql, ReceitaEntity.class);
        query.setParameter(USER_ID, userId);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);

        return query.getResultList();
    }

    @Override
    public List<ReceitaEntity> findByUserAndYearMonthRange(String userId, YearMonth inicio, YearMonth fim) {
        String jpql = "SELECT r FROM ReceitaEntity r WHERE r.user.uuid = :userId AND r.data BETWEEN :inicio AND :fim ORDER BY r.data";

        return entityManager.createQuery(jpql, ReceitaEntity.class)
                .setParameter(USER_ID, userId)
                .setParameter("inicio", inicio.atDay(1))
                .setParameter("fim", fim.atEndOfMonth())
                .getResultList();
    }

    @Override
    public List<ReceitaEntity> findByUserAndValueBetween(String userId, BigDecimal min, BigDecimal max) {
        String jpql = "SELECT r FROM ReceitaEntity r WHERE r.user.uuid = :userId AND r.valor BETWEEN :min AND :max";

        return entityManager.createQuery(jpql, ReceitaEntity.class)
                .setParameter(USER_ID, userId)
                .setParameter("min", min)
                .setParameter("max", max)
                .getResultList();
    }

    @Override
    public BigDecimal sumReceitasByUserIdAndYearMonth(String userId, int year, int month) {
        String jpql = "SELECT SUM(r.valor) FROM ReceitaEntity r WHERE r.user.uuid = :userId AND YEAR(r.data) = :year AND MONTH(r.data) = :month";

        BigDecimal result = entityManager.createQuery(jpql, BigDecimal.class)
                .setParameter(USER_ID, userId)
                .setParameter("year", year)
                .setParameter("month", month)
                .getSingleResult();

        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public ReceitaEntity findTopByUserIdAndYearMonthOrderByValorDesc(String userId, int year, int month) {
        String jpql = "SELECT r FROM ReceitaEntity r WHERE r.user.uuid = :userId AND YEAR(r.data) = :year AND MONTH(r.data) = :month ORDER BY r.valor DESC";

        List<ReceitaEntity> result = entityManager.createQuery(jpql, ReceitaEntity.class)
                .setParameter(USER_ID, userId)
                .setParameter("year", year)
                .setParameter("month", month)
                .setMaxResults(1)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public Map<String, BigDecimal> findCategoriaWithHighestReceitaByUserIdAndYearMonth(String userId, int year, int month) {
        String jpql = "SELECT r.categoria.nome AS categoria, SUM(r.valor) AS total " +
                "FROM ReceitaEntity r " +
                "WHERE r.user.uuid = :userId AND YEAR(r.data) = :year AND MONTH(r.data) = :month " +
                "GROUP BY r.categoria.nome " +
                "ORDER BY total DESC";

        return getStringBigDecimalMap(userId, year, month, jpql, entityManager, USER_ID);
    }

}
