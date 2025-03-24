package br.com.gestorfinanceiro.repositories;

import br.com.gestorfinanceiro.models.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, String> {
    List<CategoriaEntity> findAllByUserUuid(String userId);
}
