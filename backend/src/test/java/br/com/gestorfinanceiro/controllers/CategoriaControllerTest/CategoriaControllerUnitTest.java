package br.com.gestorfinanceiro.controllers.CategoriaControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.gestorfinanceiro.config.security.JwtUtil;
import br.com.gestorfinanceiro.controller.CategoriaController;
import br.com.gestorfinanceiro.dto.categoria.CategoriaCreateDTO;
import br.com.gestorfinanceiro.dto.categoria.CategoriaDTO;
import br.com.gestorfinanceiro.dto.categoria.CategoriaUpdateDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.services.CategoriaService;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerUnitTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CategoriaService categoriaService;

    @Mock
    private Mapper<CategoriaEntity, CategoriaDTO> categoriaMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CategoriaController categoriaController;

    private final String userId = UUID.randomUUID().toString();
    private final String validToken = "valid.token.here";
    private final String authorizationHeader = "Authorization";
    private final String bearerToken = "Bearer " + validToken;
    private final String categoriaId = "12345";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoriaController).build();
        // Configuração comum para os mocks
        lenient().when(request.getHeader(authorizationHeader)).thenReturn(bearerToken);
        lenient().when(jwtUtil.extractUserId(validToken)).thenReturn(userId);
    }

    private UserEntity createUserEntity(String userId) {
        UserEntity user = new UserEntity();
        user.setUuid(userId);
        return user;
    }

    private CategoriaEntity createCategoriaEntity(String nome, CategoriaType tipo, String userId) {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setUuid(UUID.randomUUID().toString());
        categoria.setNome(nome);
        categoria.setTipo(tipo);
        categoria.setUser(createUserEntity(userId));
        return categoria;
    }

    @Test
    void criarCategoria_DeveRetornar201ComLocationHeader_QuandoSucesso() throws Exception {
        CategoriaCreateDTO createDTO = new CategoriaCreateDTO("Alimentação", "DESPESAS");
        CategoriaEntity entity = createCategoriaEntity("Alimentação", CategoriaType.DESPESAS, userId);
        entity.setUuid(categoriaId);
        CategoriaDTO dto = new CategoriaDTO(categoriaId, "Alimentação", "DESPESAS", userId);

        when(categoriaService.criarCategoria(any(CategoriaCreateDTO.class), anyString())).thenReturn(entity);
        when(categoriaMapper.mapTo(any(CategoriaEntity.class))).thenReturn(dto);

        mockMvc.perform(post("/categorias")
                .header(authorizationHeader, bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/categorias/" + categoriaId))
                .andExpect(jsonPath("$.uuid").value(categoriaId))
                .andExpect(jsonPath("$.nome").value("Alimentação"))
                .andExpect(jsonPath("$.tipo").value("DESPESAS"))
                .andExpect(jsonPath("$.userUuid").value(userId));
    }

    @Test
    void criarCategoria_DeveRetornar400_QuandoDadosInvalidos() throws Exception {
        String invalidJson = "{\"nome\":\"\",\"tipo\":\"\"}";

        mockMvc.perform(post("/categorias")
                .header(authorizationHeader, bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarCategorias_DeveRetornar200ComListaDeCategorias_QuandoSucesso() throws Exception {
        CategoriaEntity entity1 = createCategoriaEntity("Alimentação", CategoriaType.DESPESAS, userId);
        CategoriaEntity entity2 = createCategoriaEntity("Salário", CategoriaType.RECEITAS, userId);
        List<CategoriaEntity> entities = Arrays.asList(entity1, entity2);

        CategoriaDTO dto1 = new CategoriaDTO("1", "Alimentação", "DESPESAS", userId);
        CategoriaDTO dto2 = new CategoriaDTO("2", "Salário", "RECEITAS", userId);

        when(categoriaService.listarCategorias(anyString())).thenReturn(entities);
        when(categoriaMapper.mapTo(entity1)).thenReturn(dto1);
        when(categoriaMapper.mapTo(entity2)).thenReturn(dto2);

        mockMvc.perform(get("/categorias")
                .header(authorizationHeader, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uuid").value("1"))
                .andExpect(jsonPath("$[0].nome").value("Alimentação"))
                .andExpect(jsonPath("$[0].tipo").value("DESPESAS"))
                .andExpect(jsonPath("$[1].uuid").value("2"))
                .andExpect(jsonPath("$[1].nome").value("Salário"))
                .andExpect(jsonPath("$[1].tipo").value("RECEITAS"));
    }

    @Test
    void listarCategoriasDespesas_DeveRetornar200ComListaDeDespesas_QuandoSucesso() throws Exception {
        CategoriaEntity entity1 = createCategoriaEntity("Alimentação", CategoriaType.DESPESAS, userId);
        CategoriaEntity entity2 = createCategoriaEntity("Transporte", CategoriaType.DESPESAS, userId);
        List<CategoriaEntity> entities = Arrays.asList(entity1, entity2);

        CategoriaDTO dto1 = new CategoriaDTO("1", "Alimentação", "DESPESAS", userId);
        CategoriaDTO dto2 = new CategoriaDTO("2", "Transporte", "DESPESAS", userId);

        when(categoriaService.listarCategoriasDespesas(anyString())).thenReturn(entities);
        when(categoriaMapper.mapTo(entity1)).thenReturn(dto1);
        when(categoriaMapper.mapTo(entity2)).thenReturn(dto2);

        mockMvc.perform(get("/categorias/despesas")
                .header(authorizationHeader, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("DESPESAS"))
                .andExpect(jsonPath("$[1].tipo").value("DESPESAS"));
    }

    @Test
    void listarCategoriasReceitas_DeveRetornar200ComListaDeReceitas_QuandoSucesso() throws Exception {
        CategoriaEntity entity1 = createCategoriaEntity("Salário", CategoriaType.RECEITAS, userId);
        CategoriaEntity entity2 = createCategoriaEntity("Investimentos", CategoriaType.RECEITAS, userId);
        List<CategoriaEntity> entities = Arrays.asList(entity1, entity2);

        CategoriaDTO dto1 = new CategoriaDTO("1", "Salário", "RECEITAS", userId);
        CategoriaDTO dto2 = new CategoriaDTO("2", "Investimentos", "RECEITAS", userId);

        when(categoriaService.listarCategoriasReceitas(anyString())).thenReturn(entities);
        when(categoriaMapper.mapTo(entity1)).thenReturn(dto1);
        when(categoriaMapper.mapTo(entity2)).thenReturn(dto2);

        mockMvc.perform(get("/categorias/receitas")
                .header(authorizationHeader, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("RECEITAS"))
                .andExpect(jsonPath("$[1].tipo").value("RECEITAS"));
    }

    @Test
    void atualizarCategoria_DeveRetornar200ComCategoriaAtualizada_QuandoSucesso() throws Exception {
        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO("Alimentação Atualizada");
        CategoriaEntity entity = createCategoriaEntity("Alimentação Atualizada", CategoriaType.DESPESAS, userId);
        entity.setUuid(categoriaId);
        CategoriaDTO dto = new CategoriaDTO(categoriaId, "Alimentação Atualizada", "DESPESAS", userId);

        when(categoriaService.atualizarCategoria(anyString(), any(CategoriaUpdateDTO.class), anyString())).thenReturn(entity);
        when(categoriaMapper.mapTo(any(CategoriaEntity.class))).thenReturn(dto);

        mockMvc.perform(patch("/categorias/{categoriaId}", categoriaId)
                .header(authorizationHeader, bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(categoriaId))
                .andExpect(jsonPath("$.nome").value("Alimentação Atualizada"));
    }

    @Test
    void atualizarCategoria_DeveRetornar400_QuandoDadosInvalidos() throws Exception {
        String invalidJson = "{\"nome\":\"\"}";

        mockMvc.perform(patch("/categorias/{categoriaId}", categoriaId)
                .header(authorizationHeader, bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletarCategoria_DeveRetornar204_QuandoSucesso() throws Exception {
        mockMvc.perform(delete("/categorias/{categoriaId}", categoriaId)
                .header(authorizationHeader, bearerToken))
                .andExpect(status().isNoContent());

        verify(categoriaService).excluirCategoria(categoriaId, userId);
    }
}