package br.com.gestorfinanceiro.repositories.custom.impl;

import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.repositories.custom.DespesaRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

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
}
