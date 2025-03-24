package br.com.gestorfinanceiro.controllers.AdminControllerTest;

import br.com.gestorfinanceiro.TestDataUtil;
import br.com.gestorfinanceiro.config.security.JwtFilter;
import br.com.gestorfinanceiro.controller.AdminController;
import br.com.gestorfinanceiro.dto.user.UserForAdminDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.services.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class))
// Desabilita os filtros de segurança para facilitar testes
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private Mapper<UserEntity, UserForAdminDTO> mapper;

    //------------------TESTES DO FIND ALL USERS ----------------------//
    @Test
    void deveListarUsers() throws Exception {
        UserEntity userA = TestDataUtil.criarUsuarioEntityUtil("Usuario A");
        UserEntity userB = TestDataUtil.criarUsuarioEntityUtil("Usuario B");
        UserEntity userC = TestDataUtil.criarUsuarioEntityUtil("Usuario C");

        List<UserEntity> users = List.of(userA, userB, userC);
        when(adminService.listUsers()).thenReturn(users);

        when(mapper.mapTo(userA)).thenReturn(TestDataUtil.criarUserForAdminDTOUtil("Usuario A"));
        when(mapper.mapTo(userB)).thenReturn(TestDataUtil.criarUserForAdminDTOUtil("Usuario B"));
        when(mapper.mapTo(userC)).thenReturn(TestDataUtil.criarUserForAdminDTOUtil("Usuario C"));


        mockMvc.perform(get("/admin/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].username").value("Usuario A"))
                .andExpect(jsonPath("$[1].username").value("Usuario B"))
                .andExpect(jsonPath("$[2].username").value("Usuario C"));
    }

    @Test
    void deveListarUsersVazioQuandoNaoTiverUsers() throws Exception {
        when(adminService.listUsers()).thenReturn((List.of()));

        mockMvc.perform(get("/admin/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    //------------------TESTES DO ATUALIZAR UPDATE ESTA ATIVO ----------------------//
    @Test
    void deveAtualizarUserEstaAtivo() throws Exception {
        // Cria os dados de teste
        UserEntity userA = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456");
        userA.setEstaAtivo(false); // Importante definir o para false

        UserForAdminDTO userForAdminDTO = TestDataUtil.criarUserForAdminDTOUtil("Usuario A");
        userForAdminDTO.setEstaAtivo(false); // Importante definir o valor

        // Define o comportamento esperado para o serviço e o mapper
        when(adminService.atualizarUserStatus(userA.getUuid(), false)).thenReturn(userA);
        when(mapper.mapTo(userA)).thenReturn(userForAdminDTO);

        // Cria um objeto para representar o corpo da requisição
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(Map.of("estaAtivo", false));

        // Realiza a requisição PATCH
        mockMvc.perform(patch("/admin/users/{userID}", userA.getUuid()).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Usuario A"))
                .andExpect(jsonPath("$.estaAtivo").value(false));
    }
}
