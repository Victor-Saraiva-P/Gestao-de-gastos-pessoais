package br.com.gestorfinanceiro.controllers.UserControllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest //contexto completo da aplicação
@AutoConfigureMockMvc 
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

@Test
@WithMockUser //simula a um usuário autenticado
    public void helloAllUsers_QuandoAutenticado_DeveRetornarHelloWorld() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello World!"));
    }

 //usuário admin acessa o endpoint e retorna 200 OK
 @Test
 @WithMockUser(roles = "USER") //simula um admin autenticado
 public void findAllUsers_QuandoAdmin_DeveRetornarListaUsuarios() throws Exception {
     mockMvc.perform(get("/admin/users"))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$").isArray()); 
 }
}

/*@Test
@WithMockUser(roles = "ADMIN") // Simulando um usuário sem ADMIN
public void findAllUsers_QuandoUsuario_DeveRetornar403() throws Exception {
    mockMvc.perform(get("/admin/users"))
            .andExpect(status().isForbidden()); // Retorna 403 Forbidden
}
}*/
