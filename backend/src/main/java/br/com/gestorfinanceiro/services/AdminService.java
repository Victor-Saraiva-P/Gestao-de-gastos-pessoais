package br.com.gestorfinanceiro.services;

import br.com.gestorfinanceiro.dto.user.UserAdminUpdateDTO;
import br.com.gestorfinanceiro.models.UserEntity;

import java.util.List;

public interface AdminService {
    List<UserEntity> listUsers();

    UserEntity atualizarUserStatus(String userID, UserAdminUpdateDTO userAdminUpdateDTO);
}
