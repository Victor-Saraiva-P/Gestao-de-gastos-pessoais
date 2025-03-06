package br.com.gestorfinanceiro.repositories;

import br.com.gestorfinanceiro.models.ReceitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceitaRepository extends JpaRepository<ReceitaEntity, String> {
}
