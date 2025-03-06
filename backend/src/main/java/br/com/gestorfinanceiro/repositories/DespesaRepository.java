package br.com.gestorfinanceiro.repositories;

import br.com.gestorfinanceiro.models.DespesaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DespesaRepository extends JpaRepository<DespesaEntity, String> {
    List<DespesaEntity> findAllByUserUuid(String userId);
}
