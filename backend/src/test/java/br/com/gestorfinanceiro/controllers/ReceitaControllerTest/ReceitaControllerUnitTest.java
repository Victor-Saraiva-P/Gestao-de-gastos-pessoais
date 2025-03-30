package br.com.gestorfinanceiro.controllers.ReceitaControllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.gestorfinanceiro.config.security.JwtUtil;
import br.com.gestorfinanceiro.controller.ReceitaController;
import br.com.gestorfinanceiro.dto.receita.ReceitaDTO;
import br.com.gestorfinanceiro.dto.receita.ReceitaCreateDTO;
import br.com.gestorfinanceiro.dto.receita.ReceitaUpdateDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoBarraDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.models.enums.ReceitasCategorias;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.services.impl.ReceitaServiceImpl;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class ReceitaControllerUnitTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ReceitaServiceImpl receitaService;

    @Mock
    private Mapper<ReceitaEntity, ReceitaDTO> receitaMapper;

    @Mock
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;

    @InjectMocks
    private ReceitaController receitaController;

    private UserEntity user;
    private ReceitaEntity receita;
    private ReceitaDTO receitaDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    
        mockMvc = MockMvcBuilders.standaloneSetup(receitaController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    
        user = new UserEntity();
        user.setUuid(UUID.randomUUID().toString());
        user.setUsername("Vini");
        user.setEmail("vini@gmail.com");
        user.setPassword("123456");
    
        CategoriaEntity categoria = new CategoriaEntity(
            ReceitasCategorias.SALARIO.name(),
            CategoriaType.RECEITAS,
            user
        );
        categoria.setUuid(UUID.randomUUID().toString());
    
        receita = new ReceitaEntity();
        receita.setUuid(UUID.randomUUID().toString());
        receita.setData(LocalDate.now());
        receita.setValor(BigDecimal.valueOf(5000));
        receita.setCategoria(categoria);
        receita.setOrigemDoPagamento("Empresa X");
        receita.setObservacoes("Salário do mês");
        receita.setUser(user);
    
        receitaDTO = new ReceitaDTO();
        receitaDTO.setData(LocalDate.now());
        receitaDTO.setCategoria(ReceitasCategorias.SALARIO.name());
        receitaDTO.setValor(BigDecimal.valueOf(5000));
        receitaDTO.setObservacoes("Salário mensal");
        receitaDTO.setOrigemDoPagamento("Empresa X");
        receitaDTO.setUuid(UUID.randomUUID().toString());
    }

    @Test
    void deveCarregarReceitaController() {
        assertNotNull(receitaController, "O ReceitaController não deveria ser nulo!");
    }

    @Nested
    class CriarReceitaTest {
        @Test
        void deveCriarReceita() throws Exception {
            ReceitaCreateDTO requestDTO = new ReceitaCreateDTO();
            requestDTO.setData(LocalDate.now());
            requestDTO.setCategoria(ReceitasCategorias.BONUS.name());
            requestDTO.setValor(new BigDecimal("1500.50"));
            requestDTO.setObservacoes("Trabalho Extra");
            requestDTO.setOrigemDoPagamento("Empresa X");
        
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(receitaService.criarReceita(any(ReceitaCreateDTO.class), anyString())).thenReturn(receita);
            when(receitaMapper.mapTo(any(ReceitaEntity.class))).thenReturn(receitaDTO);
        
            mockMvc.perform(post("/receitas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO))
                    .header("Authorization", "Bearer token_valido"))
                .andExpect(status().isCreated());
        }
        
        @Test
        void deveRetornarBadRequestParaCriacaoInvalida() throws Exception {
            ReceitaCreateDTO requestDTO = new ReceitaCreateDTO();
            
            mockMvc.perform(post("/receitas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO))
                    .header("Authorization", "Bearer token_valido"))
                .andExpect(status().isBadRequest());
        }        

        @Test
        void deveExtrairTokenCorretamente() throws Exception {
            ReceitaCreateDTO requestDTO = new ReceitaCreateDTO();
            requestDTO.setData(LocalDate.now());
            requestDTO.setCategoria(ReceitasCategorias.RENDIMENTO_DE_INVESTIMENTO.name());
            requestDTO.setValor(new BigDecimal("750.00"));
            requestDTO.setObservacoes("Dividendos");
            requestDTO.setOrigemDoPagamento("Ações");

            String expectedToken = "token_esperado";
            when(jwtUtil.extractUserId(expectedToken)).thenReturn(user.getUuid());
            when(receitaService.criarReceita(any(ReceitaCreateDTO.class), anyString())).thenReturn(receita);
            when(receitaMapper.mapTo(any(ReceitaEntity.class))).thenReturn(receitaDTO);

            mockMvc.perform(post("/receitas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO))
                    .header("Authorization", "Bearer " + expectedToken));

            verify(jwtUtil).extractUserId(expectedToken);
        }
    }

    @Nested
    class ListarReceitasTest {
        @Test
        void deveListarReceitas() throws Exception {
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(receitaService.listarReceitasUsuario(anyString())).thenReturn(List.of(receita));
            when(receitaMapper.mapTo(any(ReceitaEntity.class))).thenReturn(receitaDTO);

            mockMvc.perform(MockMvcRequestBuilders.get("/receitas")
                    .header("Authorization", "Bearer token_exemplo"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].valor").value(5000));
        }
    }

    @Nested
    class BuscarReceitaPorIdTest {
        @Test
        void deveBuscarReceitaPorId() throws Exception {
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(receitaService.buscarReceitaPorId(anyString())).thenReturn(receita);
            when(receitaMapper.mapTo(any(ReceitaEntity.class))).thenReturn(receitaDTO);

            mockMvc.perform(MockMvcRequestBuilders.get("/receitas/" + receita.getUuid())
                    .header("Authorization", "Bearer token_exemplo"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.valor").value(5000));

            verify(receitaService).buscarReceitaPorId(receita.getUuid());
        }

        @Test
        void deveRetornarForbiddenQuandoUsuarioNaoEDono() throws Exception {
            UserEntity outroUser = new UserEntity();
            outroUser.setUuid(UUID.randomUUID().toString());
            
            ReceitaEntity outraReceita = new ReceitaEntity();
            outraReceita.setUuid(UUID.randomUUID().toString());
            outraReceita.setUser(outroUser);
        
            when(receitaService.buscarReceitaPorId(anyString())).thenReturn(outraReceita);
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
        
            mockMvc.perform(get("/receitas/" + outraReceita.getUuid())
                    .header("Authorization", "Bearer token_exemplo"))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    class AtualizarReceitaTest {
        @Test
        void deveAtualizarReceita() throws Exception {
            ReceitaUpdateDTO requestDTO = new ReceitaUpdateDTO();
            requestDTO.setData(LocalDate.now());
            requestDTO.setCategoria(ReceitasCategorias.BOLSA_DE_ESTUDOS.name());
            requestDTO.setValor(new BigDecimal("200.00"));
            requestDTO.setObservacoes("Guardar");
            requestDTO.setOrigemDoPagamento("Governo");
        
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(receitaService.buscarReceitaPorId(receita.getUuid())).thenReturn(receita);
            when(receitaService.atualizarReceita(eq(receita.getUuid()), any(ReceitaUpdateDTO.class)))
                .thenReturn(receita);
            when(receitaMapper.mapTo(any(ReceitaEntity.class))).thenReturn(receitaDTO);
        
            mockMvc.perform(put("/receitas/" + receita.getUuid())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO))
                    .header("Authorization", "Bearer token_valido"))
                .andExpect(status().isOk());
            
            verify(receitaService).buscarReceitaPorId(receita.getUuid());
            verify(receitaService).atualizarReceita(eq(receita.getUuid()), any(ReceitaUpdateDTO.class));
        }
    
        @Test
        void deveRetornarForbiddenQuandoUsuarioNaoEDono() throws Exception {
            UserEntity outroUser = new UserEntity();
            outroUser.setUuid(UUID.randomUUID().toString());
            
            ReceitaEntity outraReceita = new ReceitaEntity();
            outraReceita.setUuid(UUID.randomUUID().toString());
            outraReceita.setUser(outroUser);
        
            ReceitaUpdateDTO dtoCompleto = new ReceitaUpdateDTO();
            dtoCompleto.setData(LocalDate.now());
            dtoCompleto.setCategoria("SALARIO");
            dtoCompleto.setValor(new BigDecimal("1000.00"));
            dtoCompleto.setObservacoes("Observação obrigatória");
            dtoCompleto.setOrigemDoPagamento("Empresa");
        
            when(receitaService.buscarReceitaPorId(anyString())).thenReturn(outraReceita);
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
        
            mockMvc.perform(put("/receitas/" + outraReceita.getUuid())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dtoCompleto))
                    .header("Authorization", "Bearer token_exemplo"))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    class ExcluirReceitaTest {
        @Test
        void deveExcluirReceita() throws Exception {
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(receitaService.buscarReceitaPorId(anyString())).thenReturn(receita);
            doNothing().when(receitaService).excluirReceita(anyString());
            
            mockMvc.perform(delete("/receitas/" + receita.getUuid())
                    .header("Authorization", "Bearer token_valido"))
                .andExpect(status().isNoContent());

            verify(receitaService).excluirReceita(receita.getUuid());
        }

        @Test
        void deveRetornarForbiddenQuandoUsuarioNaoEDono() throws Exception {
            UserEntity outroUser = new UserEntity();
            outroUser.setUuid("outro-user-id");
            
            ReceitaEntity outraReceita = new ReceitaEntity();
            outraReceita.setUuid("outra-receita-id");
            outraReceita.setUser(outroUser);
    
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(receitaService.buscarReceitaPorId("outra-receita-id")).thenReturn(outraReceita);
    
            mockMvc.perform(delete("/receitas/outra-receita-id")
                    .header("Authorization", "Bearer token_valido"))
                .andExpect(status().isForbidden());
        }
    }

    @Test
    void deveConverterDTOParaEntityCorretamente() throws Exception {
        ReceitaCreateDTO requestDTO = new ReceitaCreateDTO();
        requestDTO.setData(LocalDate.now());
        requestDTO.setCategoria(ReceitasCategorias.BONUS.name());
        requestDTO.setValor(new BigDecimal("500.00"));
        requestDTO.setObservacoes("Bônus trimestral");
        requestDTO.setOrigemDoPagamento("Empresa");
    
        when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
        when(receitaService.criarReceita(any(ReceitaCreateDTO.class), anyString())).thenReturn(receita);
        when(receitaMapper.mapTo(any(ReceitaEntity.class))).thenReturn(receitaDTO);
    
        mockMvc.perform(post("/receitas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO))
                .header("Authorization", "Bearer token"));
    
        ArgumentCaptor<ReceitaCreateDTO> dtoCaptor = ArgumentCaptor.forClass(ReceitaCreateDTO.class);
        verify(receitaService).criarReceita(dtoCaptor.capture(), anyString());
        
        ReceitaCreateDTO capturedDTO = dtoCaptor.getValue();
        assertEquals(requestDTO.getValor(), capturedDTO.getValor());
        assertEquals(requestDTO.getCategoria(), capturedDTO.getCategoria());
        assertEquals(requestDTO.getOrigemDoPagamento(), capturedDTO.getOrigemDoPagamento());
    }

    @Test
    void deveRetornarBadRequestParaDTOInvalido() throws Exception {
        ReceitaDTO invalidDTO = new ReceitaDTO();
        
        mockMvc.perform(post("/receitas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO))
                .header("Authorization", "Bearer token"))
            .andExpect(status().isBadRequest());
    }

    @Nested
    class ValidacaoDadosTest {
        @Test
        void criarReceita_DeveRetornarBadRequest_QuandoValorInvalido() throws Exception {
            ReceitaCreateDTO dtoInvalido = new ReceitaCreateDTO();
            dtoInvalido.setData(LocalDate.now());
            dtoInvalido.setCategoria("SALARIO");
            dtoInvalido.setValor(BigDecimal.ZERO);
            dtoInvalido.setObservacoes("Salário do mês");
            dtoInvalido.setOrigemDoPagamento("Empresa");
            
            mockMvc.perform(post("/receitas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dtoInvalido))
                    .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
        }

        @Test
        void atualizarReceita_DeveRetornarBadRequest_QuandoDataFutura() throws Exception {
            ReceitaUpdateDTO dtoInvalido = new ReceitaUpdateDTO();
            dtoInvalido.setData(LocalDate.now().plusDays(1));
            dtoInvalido.setCategoria("SALARIO");
            dtoInvalido.setValor(BigDecimal.ZERO);
            dtoInvalido.setObservacoes("Salário do mês");
            dtoInvalido.setOrigemDoPagamento("Empresa");
            
            mockMvc.perform(put("/receitas/" + receita.getUuid())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dtoInvalido))
                    .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
        }
    }
    
    @Test
    void acessarReceitaOutroUsuario_DeveRetornarForbidden() throws Exception {
        UserEntity outroUser = new UserEntity();
        outroUser.setUuid("outro_user_id");
        
        ReceitaEntity receitaOutroUsuario = new ReceitaEntity();
        receitaOutroUsuario.setUuid("123");
        receitaOutroUsuario.setUser(outroUser);

        when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
        when(receitaService.buscarReceitaPorId("123")).thenReturn(receitaOutroUsuario);
        
        mockMvc.perform(get("/receitas/123")
                .header("Authorization", "Bearer token_valido"))
            .andExpect(status().isForbidden());
    }

    @Nested
    class RelatoriosTest {
        @Test
        void gerarGraficoBarras_DeveRetornarOk() throws Exception {
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            
            when(receitaService.gerarGraficoBarras(anyString(), any(), any()))
                .thenReturn(new GraficoBarraDTO(Map.of("Janeiro", BigDecimal.valueOf(5000))));
            
            mockMvc.perform(get("/receitas/grafico-barras")
                    .param("inicio", "2023-01")
                    .param("fim", "2023-12")
                    .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dadosMensais.Janeiro").value(5000));
        }
    
        @Test
        void gerarGraficoBarras_DeveRetornarBadRequest_QuandoDatasInvalidas() throws Exception {
            mockMvc.perform(get("/receitas/grafico-barras")
                    .param("inicio", "invalido")
                    .param("fim", "2023-12")
                    .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
        }
    }
}