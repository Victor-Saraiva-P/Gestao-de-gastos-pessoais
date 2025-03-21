package br.com.gestorfinanceiro.services.AdminServiceTest;

import br.com.gestorfinanceiro.TestDataUtil;
import br.com.gestorfinanceiro.exceptions.InvalidUserIdException;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.AdminService;
import br.com.gestorfinanceiro.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AdminServiceIntegrationTest {
    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // Limpa o banco antes de cada teste para evitar inconsistências
    }

    // Teste para ver se o AdminService foi carregado
    @Test
    void deveCarregarAuthService() {
        assertNotNull(adminService, "O AuthService não deveria ser nulo!");
    }

    //------------------TESTES DO LIST USERS ----------------------//
    @Test
    void deveListarUsers() {
        adicionarUsuario("Usuario A");
        adicionarUsuario("Usuario B");
        adicionarUsuario("Usuario C");

        List<UserEntity> users = adminService.listUsers();

        //verifica se a lista de usuários tem 3 usuários
        assertEquals(3, users.size());
        assertEquals("Usuario A", users.get(0).getUsername());
        assertEquals("Usuario B", users.get(1).getUsername());
        assertEquals("Usuario C", users.get(2).getUsername());
    }

    @Test
    void deveListarUsersVazioQuandoNaoTiverUsers() {
        List<UserEntity> users = adminService.listUsers();

        //verifica se a lista de usuários está vazia
        assertTrue(users.isEmpty());
    }

    //------------------TESTES DO ATUALIZAR USER STATUS ----------------------//
    @Test
    void deveAtualizarUserStatus() {
        UserEntity user = adicionarUsuario("Usuario A");
        user.setEstaAtivo(true);

        UserEntity userUpdated = adminService.atualizarUserStatus(user.getUuid(), false);

        // verifica se o usuário foi atualizado
        assertFalse(userUpdated.getEstaAtivo());
    }

    @Test
    void deveLancarInvalidUserIdExceptionQuandoUserIdForNulo() {
        // verifica se o metodo lança a exceção quando o userId é nulo
        assertThrows(InvalidUserIdException.class, () -> adminService.atualizarUserStatus(null, false));
    }

    @Test
    void deveLancarUserNotFoundExceptionQuandoNaoEncontrarUser() {
        // verifica se o metodo lança a exceção quando o usuário não é encontrado
        assertThrows(Exception.class, () -> adminService.atualizarUserStatus("123-456", false));
    }

    //-------------------------------MÉTODOS AUXILIARES-------------------------------//
    public UserEntity adicionarUsuario(String nome) {
        UserEntity user = TestDataUtil.criarUsuarioEntityUtil(nome);

        authService.register(user);

        return user;
    }
}
