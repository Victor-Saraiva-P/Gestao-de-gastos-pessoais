package br.com.gestorfinanceiro.controller;

import br.com.gestorfinanceiro.config.security.JwtUtil;
import br.com.gestorfinanceiro.dto.categoria.CategoriaCreateDTO;
import br.com.gestorfinanceiro.dto.user.LoginDTO;
import br.com.gestorfinanceiro.dto.user.UserDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.DespesasCategorias;
import br.com.gestorfinanceiro.models.enums.ReceitasCategorias;
import br.com.gestorfinanceiro.services.AuthService;
import br.com.gestorfinanceiro.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private final CategoriaService categoriaService;

    private final Mapper<UserEntity, UserDTO> userMapper;

    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, CategoriaService categoriaService, Mapper<UserEntity, UserDTO> userMapper, JwtUtil jwtUtil) {
        this.authService = authService;
        this.categoriaService = categoriaService;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@Valid @RequestBody UserDTO userDTO) {
        UserEntity userEntity = userMapper.mapFrom(userDTO);
        UserEntity registeredUser = this.authService.register(userEntity);

        // Cria as categorias padrão para o usuário
        // Despesas
        for (DespesasCategorias categoria : DespesasCategorias.values()) {
            CategoriaCreateDTO categoriaDespesa = new CategoriaCreateDTO(
                    categoria.toNormalCase(), "DESPESAS");
            categoriaService.criarCategoria(categoriaDespesa, registeredUser.getUuid());
        }

        // Receitas
        for (ReceitasCategorias categoria : ReceitasCategorias.values()) {
            CategoriaCreateDTO categoriaReceita = new CategoriaCreateDTO(
                    categoria.toNormalCase(), "RECEITAS");
            categoriaService.criarCategoria(categoriaReceita, registeredUser.getUuid());
        }

        // Sem categoria
        categoriaService.criarSemCategoria(registeredUser.getUuid(), "DESPESAS");
        categoriaService.criarSemCategoria(registeredUser.getUuid(), "RECEITAS");


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginDTO loginDTO) {
        UserEntity userEntity = authService.login(loginDTO.email(), loginDTO.password());

        // Obtém a role do usuário autenticado
        String role = userEntity.getRole().name();
        String estaAtivo = userEntity.getEstaAtivo().toString();

        // Gera o token JWT com username e role
        String token = jwtUtil.generateToken(userEntity.getUuid(), userEntity.getUsername(),userEntity.getEmail(), role, estaAtivo);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserEntity> findByEmail(@PathVariable String email) {
        UserEntity userEntity = authService.findUserByEmail(email);
        return ResponseEntity.ok(userEntity);
    }
}