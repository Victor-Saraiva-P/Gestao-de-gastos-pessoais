package br.com.gestorfinanceiro;

import br.com.gestorfinanceiro.dto.user.UserDTO;
import br.com.gestorfinanceiro.dto.user.UserForAdminDTO;
import br.com.gestorfinanceiro.dto.categoria.CategoriaCreateDTO;
import br.com.gestorfinanceiro.dto.categoria.CategoriaUpdateDTO;
import br.com.gestorfinanceiro.models.CategoriaEntity;
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

    //------------------------------- UTILS DE CATEGORIAS -------------------------------//
    public static CategoriaEntity criarCategoriaEntityUtil(String nome, String tipo) {
        return new CategoriaEntity(nome, tipo);
    }

    public static CategoriaCreateDTO criarCategoriaCreateDTOUtil(String nome, String tipo) {
        return new CategoriaCreateDTO(nome, tipo);
    }

    public static CategoriaEntity criarCategoriaEntityComUserUtil(String nome, String tipo, UserEntity user) {
        return new CategoriaEntity(nome, tipo, user, "123-456-789");
    }

    public static CategoriaUpdateDTO criarCategoriaUpdateDTOUtil(String nome) {
        return new CategoriaUpdateDTO(nome);
    }
}
