package br.com.gestorfinanceiro;

import br.com.gestorfinanceiro.dto.UserDTO;
import br.com.gestorfinanceiro.dto.UserForAdminDTO;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.Roles;

public class TestDataUtil {
    //------------------------------- UTILS DE USERS -------------------------------//
    public static UserEntity criarUsuarioEntityUtil(String nome) {
        UserEntity user = new UserEntity();
        user.setUsername(nome);
        user.setEmail(nome + "@gmail.com");
        user.setPassword("123456");
        user.setRole(Roles.USER);

        return user;
    }

    public static UserEntity criarUsuarioEntityUtil(String nome, String uuid) {
        UserEntity user = new UserEntity();
        user.setUuid(uuid);
        user.setUsername(nome);
        user.setEmail(nome + "@gmail.com");
        user.setPassword("123456");
        user.setRole(Roles.USER);

        return user;
    }


    public static UserDTO criarUsuarioDtoUtil(String nome) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(nome);
        userDTO.setEmail(nome + "@gmail.com");
        userDTO.setPassword("123456");
        userDTO.setRole("USER");

        return userDTO;
    }

    public static UserForAdminDTO criarUserForAdminDTOUtil(String nome) {
        UserForAdminDTO userForAdminDTO = new UserForAdminDTO();
        userForAdminDTO.setUsername(nome);
        userForAdminDTO.setEmail(nome + "@gmail.com");
        userForAdminDTO.setEstaAtivo(true);

        return userForAdminDTO;
    }
}
