package br.com.gestorfinanceiro.services.AdminServiceTest;

import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdminServiceUnitTest {
    @InjectMocks
    private AdminService adminService;

    @Mock
    private UserRepository userRepository;

    //------------------TESTES DO LIST USERS ----------------------//
    @Test
    void deveListarUsers() {
        //TODO
    }

    @Test
    void deveListarUsersVazioQuandoNaoTiverUsers() {
        //TODO
    }

    //------------------TESTES DO ATUALIZAR USER STATUS ----------------------//
    @Test
    void deveAtualizarUserStatus() {
        //TODO
    }

    @Test
    void deveLancarInvalidUserIdExceptionQuandoUserIdForNulo() {
        //TODO
    }

    @Test
    void deveLancarUserNotFoundExceptionQuandoNaoEncontrarUser() {
        //TODO
    }
}
