package br.com.gestorfinanceiro.services;

import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.Status;

import java.util.List;

public interface AdminService {
    List<UserEntity> listUsers();

    UserEntity atualizarUserStatus(String userID, Status status);
}
