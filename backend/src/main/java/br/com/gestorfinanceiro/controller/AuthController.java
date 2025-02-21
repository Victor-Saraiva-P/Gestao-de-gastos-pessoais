package br.com.gestorfinanceiro.controller;

import br.com.gestorfinanceiro.dto.UserDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private final Mapper<UserEntity, UserDTO> userMapper;

    public AuthController(AuthService authService, Mapper<UserEntity, UserDTO> userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@RequestBody UserDTO userDTO) {
        UserEntity userEntity = userMapper.mapFrom(userDTO);
        UserEntity registeredUser = this.authService.register(userEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }
    
}
