package br.com.gestorfinanceiro.repositories.custom.impl;

import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.repositories.custom.DespesaRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Repository
public class DespesaRepositoryCustomImpl implements DespesaRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<DespesaEntity> findByUserAndYearMonthRange(String userId, YearMonth inicio, YearMonth fim) {
        String jpql = "SELECT d FROM DespesaEntity d WHERE d.user.uuid = :userId AND d.data BETWEEN :inicio AND :fim ORDER BY d.data";

        return entityManager.createQuery(jpql, DespesaEntity.class)
                .setParameter("userId", userId)
                .setParameter("inicio", inicio.atDay(1))
                .setParameter("fim", fim.atEndOfMonth())
                .getResultList();
    }

    @Override
    public List<DespesaEntity> findByUserAndDateRange(String userId, LocalDate inicio, LocalDate fim){
        String jpql = "SELECT r FROM DespesaEntity r WHERE r.user.uuid = :userId AND r.data BETWEEN :inicio AND :fim";

        TypedQuery<DespesaEntity> query = entityManager.createQuery(jpql, DespesaEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);

        return query.getResultList();
    }
}
