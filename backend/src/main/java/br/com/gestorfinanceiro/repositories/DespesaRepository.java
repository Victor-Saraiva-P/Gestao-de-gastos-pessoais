package br.com.gestorfinanceiro.repositories;

import br.com.gestorfinanceiro.models.DespesaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DespesaRepository extends JpaRepository<DespesaEntity, String> {}
