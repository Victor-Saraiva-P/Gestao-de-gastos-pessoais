package br.com.gestorfinanceiro.controllers.UserControllerTest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import br.com.gestorfinanceiro.controller.UserController;
import java.util.Map;

@SpringBootTest
public class UserControllerUnitTest {

    @InjectMocks
    private UserController userController; 

    @Test
    public void testHelloAllUsers() {
        //chama o método helloAllUsers() do controlador
        Map<String, String> response = userController.helloAllUsers();
        //verificando se a resposta contém uma chave "message" com o valor "Hello World!"
        assertThat(response).containsEntry("message", "Hello World!");
    }

    @Test
    public void testHelloAdminUsers() {
        //chama o método helloAdminUsers() do controlador
        //verificando se a resposta contém uma chave "message" com o valor "Hello Admin!"
        Map<String, String> response = userController.helloAdminUsers();
        assertThat(response).containsEntry("message", "Hello Admin!");
    }
}
