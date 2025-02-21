package br.com.gestorfinanceiro.services;

import br.com.gestorfinanceiro.models.UserEntity;

public interface AuthService {
    UserEntity register(UserEntity userEntity);
}
