package br.com.gestorfinanceiro.controllers.AdminControllerTest;

import br.com.gestorfinanceiro.controller.AdminController;
import br.com.gestorfinanceiro.services.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class AdminControllerUnitTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private AdminService adminService;

    //------------------TESTES DO FIND ALL USERS ----------------------//
    @Test
    void deveListarUsers() {
        //TODO
    }

    @Test
    void deveListarUsersVazioQuandoNaoTiverUsers() {
        //TODO
    }


    //------------------TESTES DO ATUALIZAR UPDATE ESTA ATIVO ----------------------//
    @Test
    void deveAtualizarUserEstaAtivo() {
        //TODO
    }

    @Test
    void deveLancarInvalidUserIdExceptionQuandoUserIdForNull() {
        //TODO
    }

    @Test
    void deveLancarUserNotFoundExceptionQuandoNaoEncontrarUser() {
        //TODO
    }
}
