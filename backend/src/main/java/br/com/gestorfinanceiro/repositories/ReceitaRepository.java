package br.com.gestorfinanceiro.repositories;

import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.repositories.custom.ReceitaRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceitaRepository extends JpaRepository<ReceitaEntity, String>, ReceitaRepositoryCustom {
    List<ReceitaEntity> findAllByUserUuid(String userId);
}
