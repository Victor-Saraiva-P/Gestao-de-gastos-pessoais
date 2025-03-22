package br.com.gestorfinanceiro.services.impl;

import br.com.gestorfinanceiro.exceptions.InvalidUserIdException;
import br.com.gestorfinanceiro.exceptions.admin.UserNotFoundException;
import br.com.gestorfinanceiro.models.UserEntity;
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
    public UserEntity atualizarUserStatus(String userID, Boolean status) {
        if (userID == null || userID.trim().isEmpty()) {
            throw new InvalidUserIdException();
        }

        UserEntity userEncontrado = userRepository.findById(userID)
                .orElseThrow(() -> new UserNotFoundException(userID));

        userEncontrado.setEstaAtivo(status);
        return userRepository.save(userEncontrado);
    }
}
