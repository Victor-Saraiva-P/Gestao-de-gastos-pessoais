package br.com.gestorfinanceiro.services.impl;

import br.com.gestorfinanceiro.exceptions.auth.login.EmailNotFoundException;
import br.com.gestorfinanceiro.exceptions.auth.login.InvalidPasswordException;
import br.com.gestorfinanceiro.exceptions.auth.register.EmailAlreadyExistsException;
import br.com.gestorfinanceiro.exceptions.auth.register.UsernameAlreadyExistsException;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity register(@Valid UserEntity userEntity) {

        if (userRepository.findByEmail(userEntity.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(userEntity.getEmail());
        }

        if (userRepository.findByUsername(userEntity.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(userEntity.getUsername());
        }

        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity login(String email, String password) {
        UserEntity userFoundByEmail = userRepository.findByEmail(email).orElseThrow(() -> new EmailNotFoundException(email));

        if (passwordEncoder.matches(password, userFoundByEmail.getPassword())) {
            return userFoundByEmail;
        } else {
            throw new InvalidPasswordException();
        }
    }
}