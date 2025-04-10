package br.com.gestorfinanceiro.controllers.DespesaControllerTest;

import br.com.gestorfinanceiro.dto.despesa.DespesaCreateDTO;
import br.com.gestorfinanceiro.dto.despesa.DespesaDTO;
import br.com.gestorfinanceiro.dto.despesa.DespesaUpdateDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoBarraDTO;
import br.com.gestorfinanceiro.controller.DespesaController;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.models.enums.DespesasCategorias;
import br.com.gestorfinanceiro.services.impl.DespesaServiceImpl;
import br.com.gestorfinanceiro.config.security.JwtUtil;
import br.com.gestorfinanceiro.mappers.Mapper;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;
import java.util.HashMap;
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
class DespesaControllerUnitTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private DespesaServiceImpl despesaService;

    @Mock
    private Mapper<DespesaEntity, DespesaDTO> despesaMapper;

    @Mock
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;

    @InjectMocks
    private DespesaController despesaController;

    private UserEntity user;
    private DespesaEntity despesa;
    private DespesaDTO despesaDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    
        mockMvc = MockMvcBuilders.standaloneSetup(despesaController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    
        user = new UserEntity();
        user.setUuid(UUID.randomUUID().toString());
        user.setUsername("Vini");
        user.setEmail("vini@gmail.com");
        user.setPassword("123456");
    
        CategoriaEntity categoria = new CategoriaEntity(
            DespesasCategorias.ALIMENTACAO.name(),
            CategoriaType.DESPESAS,
            user
        );
        categoria.setUuid(UUID.randomUUID().toString());
    
        despesa = new DespesaEntity();
        despesa.setUuid(UUID.randomUUID().toString());
        despesa.setData(LocalDate.now());
        despesa.setValor(BigDecimal.valueOf(100));
        despesa.setCategoria(categoria);
        despesa.setDestinoPagamento("Mercado");
        despesa.setObservacoes("Compras do mês");
        despesa.setUser(user);
    
        despesaDTO = new DespesaDTO();
        despesaDTO.setData(LocalDate.now());
        despesaDTO.setCategoria(DespesasCategorias.ALIMENTACAO.name());
        despesaDTO.setValor(BigDecimal.valueOf(100));
        despesaDTO.setObservacoes("Compras mensais");
        despesaDTO.setDestinoPagamento("Mercado");
        despesaDTO.setUuid(UUID.randomUUID().toString());
    }

    @Test
    void deveCarregarDespesaController() {
        assertNotNull(despesaController, "O DespesaController não deveria ser nulo!");
    }

    @Nested
    class CriarDespesaTest {
        @Test
        void deveCriarDespesa() throws Exception {
            DespesaCreateDTO requestDTO = new DespesaCreateDTO();
            requestDTO.setData(LocalDate.now());
            requestDTO.setCategoria(DespesasCategorias.TRANSPORTE.name());
            requestDTO.setValor(new BigDecimal("150.50"));
            requestDTO.setObservacoes("Transporte mensal");
            requestDTO.setDestinoPagamento("Uber");
        
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(despesaService.criarDespesa(any(DespesaCreateDTO.class), anyString())).thenReturn(despesa);
            when(despesaMapper.mapTo(any(DespesaEntity.class))).thenReturn(despesaDTO);
        
            mockMvc.perform(post("/despesas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO))
                    .header("Authorization", "Bearer token_valido"))
                .andExpect(status().isCreated());
        }
        
        @Test
        void deveRetornarBadRequestParaCriacaoInvalida() throws Exception {
            DespesaCreateDTO requestDTO = new DespesaCreateDTO();
            
            mockMvc.perform(post("/despesas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO))
                    .header("Authorization", "Bearer token_valido"))
                .andExpect(status().isBadRequest());
        }        

        @Test
        void deveExtrairTokenCorretamente() throws Exception {
            DespesaCreateDTO requestDTO = new DespesaCreateDTO();
            requestDTO.setData(LocalDate.now());
            requestDTO.setCategoria(DespesasCategorias.LAZER.name());
            requestDTO.setValor(new BigDecimal("75.00"));
            requestDTO.setObservacoes("Cinema");
            requestDTO.setDestinoPagamento("Shopping");

            String expectedToken = "token_esperado";
            when(jwtUtil.extractUserId(expectedToken)).thenReturn(user.getUuid());
            when(despesaService.criarDespesa(any(DespesaCreateDTO.class), anyString())).thenReturn(despesa);
            when(despesaMapper.mapTo(any(DespesaEntity.class))).thenReturn(despesaDTO);

            mockMvc.perform(post("/despesas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO))
                    .header("Authorization", "Bearer " + expectedToken));

            verify(jwtUtil).extractUserId(expectedToken);
        }
    }

    @Nested
    class ListarDespesasTest {
        @Test
        void deveListarDespesas() throws Exception {
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(despesaService.listarDespesasUsuario(anyString())).thenReturn(List.of(despesa));
            when(despesaMapper.mapTo(any(DespesaEntity.class))).thenReturn(despesaDTO);

            mockMvc.perform(MockMvcRequestBuilders.get("/despesas")
                    .header("Authorization", "Bearer token_exemplo"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].valor").value(100));
        }
    }

    @Nested
    class BuscarDespesaPorIdTest {
        @Test
        void deveBuscarDespesaPorId() throws Exception {
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(despesaService.buscarDespesaPorId(anyString())).thenReturn(despesa);
            when(despesaMapper.mapTo(any(DespesaEntity.class))).thenReturn(despesaDTO);

            mockMvc.perform(MockMvcRequestBuilders.get("/despesas/" + despesa.getUuid())
                    .header("Authorization", "Bearer token_exemplo"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.valor").value(100));

            verify(despesaService).buscarDespesaPorId(despesa.getUuid());
        }

        @Test
        void deveRetornarForbiddenQuandoUsuarioNaoEDono() throws Exception {
            UserEntity outroUser = new UserEntity();
            outroUser.setUuid(UUID.randomUUID().toString());
            
            DespesaEntity outraDespesa = new DespesaEntity();
            outraDespesa.setUuid(UUID.randomUUID().toString());
            outraDespesa.setUser(outroUser);
        
            when(despesaService.buscarDespesaPorId(anyString())).thenReturn(outraDespesa);
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
        
            mockMvc.perform(get("/despesas/" + outraDespesa.getUuid())
                    .header("Authorization", "Bearer token_exemplo"))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    class AtualizarDespesaTest {
        @Test
        void deveAtualizarDespesa() throws Exception {
            // 1. Cria o DTO de atualização
            DespesaUpdateDTO requestDTO = new DespesaUpdateDTO();
            requestDTO.setData(LocalDate.now());
            requestDTO.setCategoria(DespesasCategorias.MORADIA.name());
            requestDTO.setValor(new BigDecimal("1250.00"));
            requestDTO.setObservacoes("Aluguel atualizado");
            requestDTO.setDestinoPagamento("Imobiliária XYZ");
        
            // 2. Configura os mocks
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(despesaService.buscarDespesaPorId(despesa.getUuid())).thenReturn(despesa);
            when(despesaService.atualizarDespesa(eq(despesa.getUuid()), any(DespesaUpdateDTO.class)))
                .thenReturn(despesa);
            when(despesaMapper.mapTo(any(DespesaEntity.class))).thenReturn(despesaDTO);
        
            // 3. Executa a requisição
            mockMvc.perform(put("/despesas/" + despesa.getUuid())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO))
                    .header("Authorization", "Bearer token_valido"))
                .andExpect(status().isOk());
            
            // 4. Verifica as interações
            verify(despesaService).buscarDespesaPorId(despesa.getUuid());
            verify(despesaService).atualizarDespesa(eq(despesa.getUuid()), any(DespesaUpdateDTO.class));
        }
    
        @Test
        void deveRetornarForbiddenQuandoUsuarioNaoEDono() throws Exception {
            UserEntity outroUser = new UserEntity();
            outroUser.setUuid(UUID.randomUUID().toString());
            
            DespesaEntity outraDespesa = new DespesaEntity();
            outraDespesa.setUuid(UUID.randomUUID().toString());
            outraDespesa.setUser(outroUser);
        
            DespesaUpdateDTO dtoCompleto = new DespesaUpdateDTO();
            dtoCompleto.setData(LocalDate.now());
            dtoCompleto.setCategoria("ALIMENTACAO");
            dtoCompleto.setValor(new BigDecimal("100.00"));
            dtoCompleto.setObservacoes("Observação obrigatória");
            dtoCompleto.setDestinoPagamento("Mercado");
        
            when(despesaService.buscarDespesaPorId(anyString())).thenReturn(outraDespesa);
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
        
            mockMvc.perform(put("/despesas/" + outraDespesa.getUuid())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dtoCompleto))
                    .header("Authorization", "Bearer token_exemplo"))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    class ExcluirDespesaTest {
        @Test
        void deveExcluirDespesa() throws Exception {
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(despesaService.buscarDespesaPorId(anyString())).thenReturn(despesa);
            doNothing().when(despesaService).excluirDespesa(anyString());
            
            mockMvc.perform(delete("/despesas/" + despesa.getUuid())
                    .header("Authorization", "Bearer token_valido"))
                .andExpect(status().isNoContent());

            verify(despesaService).excluirDespesa(despesa.getUuid());
        }

        @Test
        void deveRetornarForbiddenQuandoUsuarioNaoEDono() throws Exception {
            UserEntity outroUser = new UserEntity();
            outroUser.setUuid("outro-user-id");
            
            DespesaEntity outraDespesa = new DespesaEntity();
            outraDespesa.setUuid("outra-despesa-id");
            outraDespesa.setUser(outroUser);
    
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(despesaService.buscarDespesaPorId("outra-despesa-id")).thenReturn(outraDespesa);
    
            mockMvc.perform(delete("/despesas/outra-despesa-id")
                    .header("Authorization", "Bearer token_valido"))
                .andExpect(status().isForbidden());
        }
    }

    @Test
    void deveConverterDTOParaEntityCorretamente() throws Exception {
        DespesaCreateDTO requestDTO = new DespesaCreateDTO();
        requestDTO.setData(LocalDate.now());
        requestDTO.setCategoria(DespesasCategorias.TRANSPORTE.name());
        requestDTO.setValor(new BigDecimal("150.00"));
        requestDTO.setObservacoes("Transporte mensal");
        requestDTO.setDestinoPagamento("Uber");
    
        when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
        when(despesaService.criarDespesa(any(DespesaCreateDTO.class), anyString())).thenReturn(despesa);
        when(despesaMapper.mapTo(any(DespesaEntity.class))).thenReturn(despesaDTO);
    
        mockMvc.perform(post("/despesas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO))
                .header("Authorization", "Bearer token"));
    
        ArgumentCaptor<DespesaCreateDTO> dtoCaptor = ArgumentCaptor.forClass(DespesaCreateDTO.class);
        verify(despesaService).criarDespesa(dtoCaptor.capture(), anyString());
        
        DespesaCreateDTO capturedDTO = dtoCaptor.getValue();
        assertEquals(requestDTO.getValor(), capturedDTO.getValor());
        assertEquals(requestDTO.getCategoria(), capturedDTO.getCategoria());
        assertEquals(requestDTO.getDestinoPagamento(), capturedDTO.getDestinoPagamento());
    }

    @Test
    void deveRetornarBadRequestParaDTOInvalido() throws Exception {
        DespesaDTO invalidDTO = new DespesaDTO();
        
        mockMvc.perform(post("/despesas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO))
                .header("Authorization", "Bearer token"))
            .andExpect(status().isBadRequest());
    }

    @Nested
    class ValidacaoDadosTest {
        @Test
        void criarDespesa_DeveRetornarBadRequest_QuandoValorInvalido() throws Exception {
            DespesaCreateDTO dtoInvalido = new DespesaCreateDTO();
            dtoInvalido.setData(LocalDate.now());
            dtoInvalido.setCategoria("ALIMENTACAO");
            dtoInvalido.setValor(BigDecimal.ZERO);
            dtoInvalido.setObservacoes("Compras do mês");
            dtoInvalido.setDestinoPagamento("Mercado");
            
            mockMvc.perform(post("/despesas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dtoInvalido))
                    .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
        }

        @Test
        void atualizarDespesa_DeveRetornarBadRequest_QuandoDataFutura() throws Exception {
            DespesaUpdateDTO dtoInvalido = new DespesaUpdateDTO();
            dtoInvalido.setData(LocalDate.now().plusDays(1));
            dtoInvalido.setCategoria("ALIMENTACAO");
            dtoInvalido.setValor(BigDecimal.ZERO);
            dtoInvalido.setObservacoes("Compras do mês");
            dtoInvalido.setDestinoPagamento("Mercado");
            
            mockMvc.perform(put("/despesas/" + despesa.getUuid())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dtoInvalido))
                    .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
        }
    }
    
    @Test
    void acessarDespesaOutroUsuario_DeveRetornarForbidden() throws Exception {
        // Configurar um usuário diferente
        UserEntity outroUser = new UserEntity();
        outroUser.setUuid("outro_user_id");
        
        DespesaEntity despesaOutroUsuario = new DespesaEntity();
        despesaOutroUsuario.setUuid("123");
        despesaOutroUsuario.setUser(outroUser);

        when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid()); // Usuário logado
        when(despesaService.buscarDespesaPorId("123")).thenReturn(despesaOutroUsuario);
        
        mockMvc.perform(get("/despesas/123")
                .header("Authorization", "Bearer token_valido"))
            .andExpect(status().isForbidden());
    }

    @Nested
    class RelatoriosTest {
        @Test
        void gerarGraficoBarras_DeveRetornarOk() throws Exception {
            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            
            when(despesaService.gerarGraficoBarras(anyString(), any(), any()))
                .thenReturn(new GraficoBarraDTO(Map.of("Janeiro", BigDecimal.TEN)));
            
            mockMvc.perform(get("/despesas/grafico-barras")
                    .param("inicio", "2023-01")
                    .param("fim", "2023-12")
                    .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dadosMensais.Janeiro").value(10));
        }
    
        @Test
        void gerarGraficoBarras_DeveRetornarBadRequest_QuandoDatasInvalidas() throws Exception {
            mockMvc.perform(get("/despesas/grafico-barras")
                    .param("inicio", "invalido")
                    .param("fim", "2023-12")
                    .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GraficoBarrasTest {
        @Test
        void gerarGraficoBarras_DeveRetornarOk() throws Exception {
            YearMonth inicio = YearMonth.of(2023, 1);
            YearMonth fim = YearMonth.of(2023, 12);
            GraficoBarraDTO graficoMock = new GraficoBarraDTO(Map.of("Janeiro", BigDecimal.valueOf(1500)));

            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(despesaService.gerarGraficoBarras(anyString(), any(YearMonth.class), any(YearMonth.class)))
                .thenReturn(graficoMock);

            mockMvc.perform(get("/despesas/grafico-barras")
                    .param("inicio", inicio.toString())
                    .param("fim", fim.toString())
                    .header("Authorization", "Bearer token_valido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dadosMensais.Janeiro").value(1500));
        }

        @Test
        void gerarGraficoBarras_SemToken_DeveRetornarUnauthorized() throws Exception {
            YearMonth inicio = YearMonth.of(2023, 1);
            YearMonth fim = YearMonth.of(2023, 12);

            mockMvc.perform(get("/despesas/grafico-barras")
                    .param("inicio", inicio.toString())
                    .param("fim", fim.toString()))
                .andExpect(status().isUnauthorized());
        }

        @Test
        void gerarGraficoBarras_TokenInvalido_DeveRetornarUnauthorized() throws Exception {
            YearMonth inicio = YearMonth.of(2023, 1);
            YearMonth fim = YearMonth.of(2023, 12);

            mockMvc.perform(get("/despesas/grafico-barras")
                    .param("inicio", inicio.toString())
                    .param("fim", fim.toString())
                    .header("Authorization", "TokenInvalido"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        void gerarGraficoBarras_FormatoDataInvalido_DeveRetornarBadRequest() throws Exception {
            mockMvc.perform(get("/despesas/grafico-barras")
                    .param("inicio", "2023/01")
                    .param("fim", "2023-12")
                    .header("Authorization", "Bearer token_valido"))
                .andExpect(status().isBadRequest());
        }

        @Test
        void gerarGraficoBarras_MesmaDataInicioEFim_DeveRetornarOk() throws Exception {
            YearMonth data = YearMonth.of(2023, 6);
            GraficoBarraDTO graficoMock = new GraficoBarraDTO(Map.of("Junho", BigDecimal.valueOf(1200)));

            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(despesaService.gerarGraficoBarras(anyString(), any(YearMonth.class), any(YearMonth.class)))
                .thenReturn(graficoMock);

            mockMvc.perform(get("/despesas/grafico-barras")
                    .param("inicio", data.toString())
                    .param("fim", data.toString())
                    .header("Authorization", "Bearer token_valido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dadosMensais.Junho").value(1200));
        }

        @Test
        void gerarGraficoBarras_DeveChamarServiceComParametrosCorretos() throws Exception {
            YearMonth inicio = YearMonth.of(2023, 1);
            YearMonth fim = YearMonth.of(2023, 3);
            GraficoBarraDTO graficoMock = new GraficoBarraDTO(new HashMap<>());

            when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
            when(despesaService.gerarGraficoBarras(anyString(), any(YearMonth.class), any(YearMonth.class)))
                .thenReturn(graficoMock);

            mockMvc.perform(get("/despesas/grafico-barras")
                    .param("inicio", inicio.toString())
                    .param("fim", fim.toString())
                    .header("Authorization", "Bearer token_valido"));

            ArgumentCaptor<YearMonth> inicioCaptor = ArgumentCaptor.forClass(YearMonth.class);
            ArgumentCaptor<YearMonth> fimCaptor = ArgumentCaptor.forClass(YearMonth.class);

            verify(despesaService).gerarGraficoBarras(eq(user.getUuid()), inicioCaptor.capture(), fimCaptor.capture());
            
            assertEquals(inicio, inicioCaptor.getValue());
            assertEquals(fim, fimCaptor.getValue());
        }
    }

    @Test
    void gerarGraficoBarras_DataInicialPosteriorAFinal_DeveLancarExcecaoComMensagemCorreta() throws Exception {
        YearMonth inicio = YearMonth.of(2023, 12);
        YearMonth fim = YearMonth.of(2023, 1);
        
        when(jwtUtil.extractUserId(anyString())).thenReturn(user.getUuid());
    
        mockMvc = MockMvcBuilders.standaloneSetup(despesaController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    
        mockMvc.perform(get("/despesas/grafico-barras")
                .param("inicio", inicio.toString())
                .param("fim", fim.toString())
                .header("Authorization", "Bearer token_valido"))
            .andExpect(status().isBadRequest())
            .andExpect(result -> {
                assertTrue(result.getResolvedException() instanceof ResponseStatusException);
                ResponseStatusException ex = (ResponseStatusException) result.getResolvedException();
                assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
                assertEquals("Data inicial não pode ser posterior à data final", ex.getReason());
            });
    }
}