package br.com.gestorfinanceiro.controllers.AdminControllerTest;

import br.com.gestorfinanceiro.TestDataUtil;
import br.com.gestorfinanceiro.config.security.JwtFilter;
import br.com.gestorfinanceiro.controller.AdminController;
import br.com.gestorfinanceiro.dto.user.UserAdminUpdateDTO;
import br.com.gestorfinanceiro.dto.user.UserForAdminDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.Roles;
import br.com.gestorfinanceiro.services.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
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
        UserEntity userA = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456");
        userA.setEstaAtivo(false);
        userA.setRole(Roles.USER);

        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO();
        userAdminUpdateDTO.setEstaAtivo(true);
        userAdminUpdateDTO.setRole("ADMIN");

        UserForAdminDTO userForAdminDTO = new UserForAdminDTO();
        userForAdminDTO.setUsername("Usuario A");
        userForAdminDTO.setEstaAtivo(true);
        userForAdminDTO.setRole("ADMIN");

        when(adminService.atualizarUser(eq(userA.getUuid()), any(UserAdminUpdateDTO.class)))
                .thenReturn(userA);

        when(mapper.mapTo(userA)).thenReturn(userForAdminDTO);

        String jsonContent = new ObjectMapper().writeValueAsString(userAdminUpdateDTO);

        mockMvc.perform(patch("/admin/users/{userID}", userA.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(result -> System.out.println("Resposta da API: " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Usuario A"))
                .andExpect(jsonPath("$.estaAtivo").value(true))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
}
