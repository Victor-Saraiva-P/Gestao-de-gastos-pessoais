package br.com.gestorfinanceiro.repositories;

import br.com.gestorfinanceiro.models.ReceitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceitaRepository extends JpaRepository<ReceitaEntity, String> {
    List<ReceitaEntity> findAllByUserUuid(String userId);
}
