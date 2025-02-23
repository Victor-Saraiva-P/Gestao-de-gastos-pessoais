package br.com.gestorfinanceiro.controller;

import br.com.gestorfinanceiro.dto.LoginDTO;
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
import jakarta.validation.Valid;

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
    public ResponseEntity<UserEntity> register(@Valid @RequestBody UserDTO userDTO) {
        UserEntity userEntity = userMapper.mapFrom(userDTO);
        UserEntity registeredUser = this.authService.register(userEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginDTO loginDTO) {
        UserEntity userEntity = authService.login(loginDTO.email(), loginDTO.password());
        UserDTO userDTO = userMapper.mapTo(userEntity);
        return ResponseEntity.ok(userDTO); //TODO: Posteriormente deve retornar um token JWT
    }
}
