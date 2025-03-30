package br.com.gestorfinanceiro.repositories.custom.impl;

import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.repositories.custom.DespesaRepositoryCustom;
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

@Repository
public class DespesaRepositoryCustomImpl implements DespesaRepositoryCustom {

    private static final String USER_ID = "userId";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DespesaEntity> findByUserAndYearMonthRange(String userId, YearMonth inicio, YearMonth fim) {
        String jpql = "SELECT d FROM DespesaEntity d WHERE d.user.uuid = :userId AND d.data BETWEEN :inicio AND :fim ORDER BY d.data";

        return entityManager.createQuery(jpql, DespesaEntity.class)
                .setParameter(USER_ID, userId)
                .setParameter("inicio", inicio.atDay(1))
                .setParameter("fim", fim.atEndOfMonth())
                .getResultList();
    }

    @Override
    public List<DespesaEntity> findByUserAndDateRange(String userId, LocalDate inicio, LocalDate fim) {
        String jpql = "SELECT d FROM DespesaEntity d WHERE d.user.uuid = :userId AND d.data BETWEEN :inicio AND :fim";

        TypedQuery<DespesaEntity> query = entityManager.createQuery(jpql, DespesaEntity.class);
        query.setParameter(USER_ID, userId);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);

        return query.getResultList();
    }

    @Override
    public List<DespesaEntity> findByUserAndValueBetween(String userId, BigDecimal min, BigDecimal max) {
        String jpql = "SELECT r FROM DespesaEntity r WHERE r.user.uuid = :userId AND r.valor BETWEEN :min AND :max";

        return entityManager.createQuery(jpql, DespesaEntity.class)
                .setParameter(USER_ID, userId)
                .setParameter("min", min)
                .setParameter("max", max)
                .getResultList();
    }

    @Override
    public BigDecimal sumDespesasByUserIdAndYearMonth(String userId, int year, int month) {
        String jpql = "SELECT SUM(d.valor) FROM DespesaEntity d WHERE d.user.uuid = :userId AND YEAR(d.data) = :year AND MONTH(d.data) = :month";

        BigDecimal result = entityManager.createQuery(jpql, BigDecimal.class)
                .setParameter(USER_ID, userId)
                .setParameter("year", year)
                .setParameter("month", month)
                .getSingleResult();

        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public DespesaEntity findTopByUserIdAndYearMonthOrderByValorDesc(String userId, int year, int month) {
        String jpql = "SELECT d FROM DespesaEntity d WHERE d.user.uuid = :userId AND YEAR(d.data) = :year AND MONTH(d.data) = :month ORDER BY d.valor DESC";

        List<DespesaEntity> result = entityManager.createQuery(jpql, DespesaEntity.class)
                .setParameter(USER_ID, userId)
                .setParameter("year", year)
                .setParameter("month", month)
                .setMaxResults(1)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public Map<String, BigDecimal> findCategoriaWithHighestDespesaByUserIdAndYearMonth(String userId, int year, int month) {
        String jpql = "SELECT d.categoria.nome AS categoria, SUM(d.valor) AS total " +
                "FROM DespesaEntity d " +
                "WHERE d.user.uuid = :userId AND YEAR(d.data) = :year AND MONTH(d.data) = :month " +
                "GROUP BY d.categoria.nome " +
                "ORDER BY total DESC";

        return getStringBigDecimalMap(userId, year, month, jpql, entityManager, USER_ID);
    }

    static Map<String, BigDecimal> getStringBigDecimalMap(String userId, int year, int month, String jpql, EntityManager entityManager, String userId2) {
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
                .setParameter(userId2, userId)
                .setParameter("year", year)
                .setParameter("month", month)
                .setMaxResults(1)
                .getResultList();

        if (results.isEmpty()) {
            return Collections.emptyMap();
        }

        Object[] result = results.get(0);
        return Map.of((String) result[0], (BigDecimal) result[1]);
    }
}
