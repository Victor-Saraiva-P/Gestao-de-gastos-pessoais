package br.com.gestorfinanceiro.services.AdminServiceTest;

import br.com.gestorfinanceiro.TestDataUtil;
import br.com.gestorfinanceiro.dto.user.UserAdminUpdateDTO;
import br.com.gestorfinanceiro.exceptions.user.InvalidUserIdException;
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
    void deveAtualizarUser() {
        UserEntity user = adicionarUsuario("Usuario A");

        UserAdminUpdateDTO userAtualizado = criarUserAdminUpdateDTO(true);

        UserEntity userUpdated = adminService.atualizarUser(user.getUuid(), userAtualizado);

        // verifica se o usuário foi atualizado
        assertTrue(userUpdated.getEstaAtivo());

        userAtualizado.setEstaAtivo(false);

        userUpdated = adminService.atualizarUser(user.getUuid(), userAtualizado);

        assertFalse(userUpdated.getEstaAtivo());
    }

    @Test
    void deveLancarInvalidUserIdExceptionQuandoUserIdForNulo() {
        // verifica se o metodo lança a exceção quando o userId é nulo
        UserAdminUpdateDTO userAtualizado = criarUserAdminUpdateDTO(true);
        assertThrows(InvalidUserIdException.class, () -> adminService.atualizarUser(null, userAtualizado));
    }

    @Test
    void deveLancarUserNotFoundExceptionQuandoNaoEncontrarUser() {
        // verifica se o metodo lança a exceção quando o usuário não é encontrado
        UserAdminUpdateDTO userAtualizado = criarUserAdminUpdateDTO(true);
        assertThrows(Exception.class, () -> adminService.atualizarUser("123-456", userAtualizado));
    }

    //-------------------------------MÉTODOS AUXILIARES-------------------------------//
    public UserEntity adicionarUsuario(String nome) {
        UserEntity user = TestDataUtil.criarUsuarioEntityUtil(nome);

        authService.register(user);

        return user;
    }

    public UserAdminUpdateDTO criarUserAdminUpdateDTO(boolean estaAtivo) {
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO();
        userAdminUpdateDTO.setEstaAtivo(estaAtivo);
        userAdminUpdateDTO.setRole("ADMIN");

        return userAdminUpdateDTO;
    }
}
