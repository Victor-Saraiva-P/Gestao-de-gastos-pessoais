package br.com.gestorfinanceiro.controllers.AdminControllerTest;

import br.com.gestorfinanceiro.TestDataUtil;
import br.com.gestorfinanceiro.controller.AdminController;
import br.com.gestorfinanceiro.dto.user.UserForAdminDTO;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AdminControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminController adminController;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void deveCarregarAuthController() {
        assertNotNull(adminController, "O AdminController não deveria ser nulo!");
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    //------------------TESTES DO FIND ALL USERS ----------------------//
    @Test
    void deveListarUsers() throws Exception {
        adicionarUsuario("Usuario A");
        adicionarUsuario("Usuario B");
        adicionarUsuario("Usuario C");

        mockMvc.perform(get("/admin/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].username").value("Usuario A"))
                .andExpect(jsonPath("$[1].username").value("Usuario B"))
                .andExpect(jsonPath("$[2].username").value("Usuario C"));
    }

    @Test
    void deveListarUsersVazioQuandoNaoTiverUsers() throws Exception {
        mockMvc.perform(get("/admin/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    //------------------TESTES DO ATUALIZAR UPDATE ESTA ATIVO ----------------------//
    @Test
    void deveAtualizarUserEstaAtivo() throws Exception {
        // Cria os dados de teste
        UserEntity userA = adicionarUsuario("Usuario A");
        userA.setEstaAtivo(false); // Importante definir o para false

        UserForAdminDTO userForAdminDTO = TestDataUtil.criarUserForAdminDTOUtil("Usuario A");
        userForAdminDTO.setEstaAtivo(false); // Importante definir o valor

        // Cria um objeto para representar o corpo da requisição
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(Map.of(
                "estaAtivo", false,
                "role", "USER"
        ));


        // Realiza a requisição PATCH
        mockMvc.perform(patch("/admin/users/{userID}", userA.getUuid()).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Usuario A"))
                .andExpect(jsonPath("$.estaAtivo").value(false));
    }

    //-------------------------------MÉTODOS AUXILIARES-------------------------------//

    public UserEntity adicionarUsuario(String nome) {
        UserEntity user = TestDataUtil.criarUsuarioEntityUtil(nome);

        userRepository.save(user);

        return user;
    }
}


