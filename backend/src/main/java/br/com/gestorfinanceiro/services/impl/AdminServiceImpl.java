package br.com.gestorfinanceiro.services.impl;

import br.com.gestorfinanceiro.dto.user.UserAdminUpdateDTO;
import br.com.gestorfinanceiro.exceptions.user.InvalidUserIdException;
import br.com.gestorfinanceiro.exceptions.user.UserNotFoundException;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.Roles;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.AdminService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserEntity> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity atualizarUserStatus(String userID, UserAdminUpdateDTO userAdminUpdateDTO) {
        // Validar o ID do usuário
        if (userID == null || userID.isEmpty()) {
            throw new InvalidUserIdException();
        }

        // Buscar o usuário pelo ID
        UserEntity user = userRepository.findById(userID)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com o ID: " + userID));

        // Atualizar os campos do usuário com base no DTO
        user.setEstaAtivo(userAdminUpdateDTO.getEstaAtivo());
        user.setRole(Roles.valueOf(userAdminUpdateDTO.getRole()));

        // Salvar e retornar o usuário atualizado
        return userRepository.save(user);
    }
}
