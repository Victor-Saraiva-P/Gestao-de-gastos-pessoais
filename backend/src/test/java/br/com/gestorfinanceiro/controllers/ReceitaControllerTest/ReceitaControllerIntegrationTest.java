package br.com.gestorfinanceiro.controllers.ReceitaControllerTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.gestorfinanceiro.dto.receita.ReceitaCreateDTO;
import br.com.gestorfinanceiro.dto.receita.ReceitaUpdateDTO;
import br.com.gestorfinanceiro.dto.user.LoginDTO;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.models.enums.Roles;
import br.com.gestorfinanceiro.repositories.CategoriaRepository;
import br.com.gestorfinanceiro.repositories.ReceitaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import jakarta.transaction.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.hamcrest.Matchers.hasSize;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReceitaControllerIntegrationTest {

    private static final String CATEGORIA_PADRAO = "Salário";
    private static final String ORIGEM_RECEITA_PADRAO = "Empresa XYZ";
    private static final String OBSERVACOES_PADRAO = "Pagamento mensal";
    private static final BigDecimal VALOR_PADRAO = BigDecimal.valueOf(5000);
    private static final BigDecimal VALOR_ATUALIZADO = BigDecimal.valueOf(6000);
    private static final BigDecimal VALOR_NEGATIVO = BigDecimal.valueOf(-100);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReceitaRepository receitaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserEntity user;
    private String authHeader;

    @BeforeEach
    void setUp() throws Exception {
        limparBaseDeDados();
        user = criarUsuarioTest();
        criarCategoriaTest(CATEGORIA_PADRAO, user);
        
        authHeader = "Bearer " + obterTokenJwt();
    }

    private String obterTokenJwt() throws Exception {
        LoginDTO loginDTO = new LoginDTO(user.getEmail(), "123456");
        
        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();
        
        return objectMapper.readValue(result.getResponse().getContentAsString(), 
            new TypeReference<Map<String, String>>() {}).get("token");
    }

    private void limparBaseDeDados() {
        receitaRepository.deleteAllInBatch();
        categoriaRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void deveCarregarContextoAplicacao() {
        assertNotNull(mockMvc, "O MockMvc não deveria ser nulo!");
    }

    @Nested
    class CriarReceitaTest {
        @Test
        void deveCriarReceita() throws Exception {
            criarCategoriaTest(CATEGORIA_PADRAO, user);
            ReceitaCreateDTO dto = criarReceitaCreateDTO();
            
            mockMvc.perform(post("/receitas")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoria").value(CATEGORIA_PADRAO));
        }

        @Test
        void erroAoCriarReceitaComValorInvalido() throws Exception {
            ReceitaCreateDTO receitaDTO = criarReceitaCreateDTO(
                    BigDecimal.ZERO, LocalDate.now(), CATEGORIA_PADRAO
            );

            mockMvc.perform(post("/receitas")
                    .header("Authorization", authHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(receitaDTO)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class ListarReceitasTest {
        @Test
        void deveListarReceitasUsuario() throws Exception {
            criarReceitaNoBanco();

            mockMvc.perform(get("/receitas")
                    .header("Authorization", authHeader))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    class BuscarReceitaPorIdTest {
        @Test
        void deveBuscarReceitaPorId() throws Exception {
            ReceitaEntity receita = criarReceitaNoBanco();

            mockMvc.perform(get("/receitas/" + receita.getUuid())
                    .header("Authorization", authHeader))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.uuid").value(receita.getUuid()));
        }

        @Test
        void erroAoBuscarReceitaPorIdInexistente() throws Exception {
            String uuid = UUID.randomUUID().toString();

            mockMvc.perform(get("/receitas/" + uuid)
                    .header("Authorization", authHeader))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class AtualizarReceitaTest {
        @Test
        void deveAtualizarReceita() throws Exception {
            ReceitaEntity receita = criarReceitaNoBanco();
            ReceitaUpdateDTO receitaUpdateDTO = criarReceitaUpdateDTO(receita);

            mockMvc.perform(put("/receitas/" + receita.getUuid())
                    .header("Authorization", authHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(receitaUpdateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valor").value(VALOR_ATUALIZADO.doubleValue()));
        }

        @Test
        void erroAoAtualizarReceitaInexistente() throws Exception {
            ReceitaUpdateDTO receita = criarReceitaUpdateDTO();
            String uuidInexistente = UUID.randomUUID().toString();

            mockMvc.perform(put("/receitas/" + uuidInexistente)
                    .header("Authorization", authHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(receita)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class ExcluirReceitaTest {
        @Test
        void deveExcluirReceita() throws Exception {
            ReceitaEntity receita = criarReceitaNoBanco();

            mockMvc.perform(delete("/receitas/" + receita.getUuid())
                    .header("Authorization", authHeader))
                    .andExpect(status().isNoContent());

            assertFalse(receitaRepository.findById(receita.getUuid()).isPresent());
        }

        @Test
        void erroAoExcluirReceitaInexistente() throws Exception {
            String uuidInexistente = UUID.randomUUID().toString();

            mockMvc.perform(delete("/receitas/" + uuidInexistente)
                    .header("Authorization", authHeader))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class GerarGraficosTest {
        @Test
        void deveGerarGraficoBarras() throws Exception {
            YearMonth mesAtual = YearMonth.now();
            criarReceitaNoBanco(VALOR_PADRAO, mesAtual.atDay(1));
            
            mockMvc.perform(get("/receitas/grafico-barras")
                    .header("Authorization", authHeader)
                    .param("inicio", mesAtual.minusMonths(1).toString())
                    .param("fim", mesAtual.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.dadosMensais").exists());
        }

        @Test
        void deveGerarGraficoPizza() throws Exception {
            receitaRepository.deleteAll();
            categoriaRepository.deleteAll();
            
            CategoriaEntity categoria = criarCategoriaTest(CATEGORIA_PADRAO, user);
            criarReceitaNoBanco(VALOR_PADRAO, LocalDate.now(), categoria.getNome());
            
            mockMvc.perform(get("/receitas/grafico-pizza")
                .header("Authorization", authHeader)
                .param("inicio", LocalDate.now().minusMonths(1).toString())
                .param("fim", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categorias." + CATEGORIA_PADRAO).exists());
        }
    }

    @Nested
    class BuscaAvancadaTest {
        @Test
        void deveBuscarReceitasPorIntervaloDeDatas() throws Exception {
            LocalDate hoje = LocalDate.now();
            criarReceitaNoBanco(VALOR_PADRAO, hoje);
            
            mockMvc.perform(get("/receitas/por-intervalo-de-datas")
                .header("Authorization", authHeader)
                .param("inicio", hoje.minusDays(1).toString())
                .param("fim", hoje.plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void deveBuscarReceitasPorIntervaloDeValores() throws Exception {
            criarReceitaNoBanco(BigDecimal.valueOf(1000), LocalDate.now());
            criarReceitaNoBanco(BigDecimal.valueOf(2000), LocalDate.now());
            
            mockMvc.perform(get("/receitas/por-intervalo-de-valores")
                    .header("Authorization", authHeader)
                    .param("min", "800")
                    .param("max", "1500"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].valor").value(1000.0));
        }
    }

    @Test
    void erroAoCriarReceitaSemCategoria() throws Exception {
        ReceitaCreateDTO dto = criarReceitaCreateDTO(VALOR_PADRAO, LocalDate.now(), null);
        mockMvc.perform(post("/receitas")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void erroAoCriarReceitaComValorNegativo() throws Exception {
        ReceitaCreateDTO dto = criarReceitaCreateDTO(VALOR_NEGATIVO, LocalDate.now(), CATEGORIA_PADRAO);
        mockMvc.perform(post("/receitas")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarVariasReceitas() throws Exception {
        criarReceitaNoBanco();
        criarReceitaNoBanco(VALOR_ATUALIZADO, LocalDate.now().minusDays(1));
        
        mockMvc.perform(get("/receitas")
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void erroAoAtualizarReceitaComDadosInvalidos() throws Exception {
        ReceitaEntity receita = criarReceitaNoBanco();
        ReceitaUpdateDTO receitaUpdateDTO = criarReceitaUpdateDTO();
        receitaUpdateDTO.setValor(null);

        mockMvc.perform(put("/receitas/" + receita.getUuid())
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(receitaUpdateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void erroBuscaAvancadaComDatasInvalidas() throws Exception {
        mockMvc.perform(get("/receitas/por-intervalo-de-datas")
                .header("Authorization", authHeader)
                .param("inicio", "2025-13-01")
                .param("fim", "2025-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void erroAoGerarGraficoComIntervaloInvalido() throws Exception {
        mockMvc.perform(get("/receitas/grafico-barras")
                .header("Authorization", authHeader)
                .param("inicio", "2025-01-01")
                .param("fim", "2024-12-31"))
                .andExpect(status().isBadRequest());
    }

    @Nested
    class AutorizacaoTest {
        private UserEntity outroUsuario;
        private String authHeaderOutroUsuario;

        @BeforeEach
        void setUpOutroUsuario() throws Exception {
            outroUsuario = new UserEntity();
            outroUsuario.setUsername("OutroUsuario");
            outroUsuario.setEmail("outro@email.com");
            outroUsuario.setPassword(passwordEncoder.encode("654321"));
            outroUsuario.setRole(Roles.USER);
            userRepository.saveAndFlush(outroUsuario);

            authHeaderOutroUsuario = "Bearer " + obterTokenJwt(outroUsuario.getEmail(), "654321");
        }

        private String obterTokenJwt(String email, String senha) throws Exception {
            LoginDTO loginDTO = new LoginDTO(email, senha);
            
            MvcResult result = mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isOk())
                    .andReturn();
            
            return objectMapper.readValue(result.getResponse().getContentAsString(), 
                new TypeReference<Map<String, String>>() {}).get("token");
        }

        @Test
        void erroQuandoUsuarioTentaAcessarReceitaDeOutroUsuario() throws Exception {
            ReceitaEntity receita = criarReceitaNoBanco();

            mockMvc.perform(get("/receitas/" + receita.getUuid())
                    .header("Authorization", authHeaderOutroUsuario))
                    .andExpect(status().isForbidden());
        }

        @Test
        void erroQuandoUsuarioTentaAtualizarReceitaDeOutroUsuario() throws Exception {
            ReceitaEntity receita = criarReceitaNoBanco();
            ReceitaUpdateDTO receitaUpdateDTO = criarReceitaUpdateDTO(receita);

            mockMvc.perform(put("/receitas/" + receita.getUuid())
                    .header("Authorization", authHeaderOutroUsuario)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(receitaUpdateDTO)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void erroQuandoUsuarioTentaExcluirReceitaDeOutroUsuario() throws Exception {
            ReceitaEntity receita = criarReceitaNoBanco();

            mockMvc.perform(delete("/receitas/" + receita.getUuid())
                    .header("Authorization", authHeaderOutroUsuario))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class EdgeCasesTest {
        @Test
        void deveLidarComValoresMuitoAltos() throws Exception {
            BigDecimal valorMuitoAlto = new BigDecimal("999999999999999.99");
            ReceitaCreateDTO dto = criarReceitaCreateDTO(valorMuitoAlto, LocalDate.now(), CATEGORIA_PADRAO);
            
            mockMvc.perform(post("/receitas")
                    .header("Authorization", authHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.valor").value(valorMuitoAlto.doubleValue()));
        }

        @Test
        void deveLidarComDatasExtremas() throws Exception {
            LocalDate dataFuturaExtrema = LocalDate.now().plusYears(10);
            LocalDate dataPassadoExtremo = LocalDate.of(1900, 1, 1);

            // Teste com data futura extrema
            ReceitaCreateDTO dtoFuturo = criarReceitaCreateDTO(VALOR_PADRAO, dataFuturaExtrema, CATEGORIA_PADRAO);
            mockMvc.perform(post("/receitas")
                    .header("Authorization", authHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dtoFuturo)))
                    .andExpect(status().isCreated());

            // Teste com data passado extrema
            ReceitaCreateDTO dtoPassado = criarReceitaCreateDTO(VALOR_PADRAO, dataPassadoExtremo, CATEGORIA_PADRAO);
            mockMvc.perform(post("/receitas")
                    .header("Authorization", authHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dtoPassado)))
                    .andExpect(status().isCreated());
        }

        @Test
        void deveLidarComCategoriasEspeciais() throws Exception {
            String categoriaEspecial = "Receita @#$% 123";
            criarCategoriaTest(categoriaEspecial, user);
            
            ReceitaCreateDTO dto = criarReceitaCreateDTO(VALOR_PADRAO, LocalDate.now(), categoriaEspecial);
            
            mockMvc.perform(post("/receitas")
                    .header("Authorization", authHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.categoria").value(categoriaEspecial));
        }

        @Test
        void deveLidarComObservacoesLongas() throws Exception {
            String observacoesLongas = "a".repeat(1000);
            ReceitaCreateDTO dto = criarReceitaCreateDTO();
            dto.setObservacoes(observacoesLongas);
            
            mockMvc.perform(post("/receitas")
                    .header("Authorization", authHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.observacoes").value(observacoesLongas));
        }

        @Test
        void deveLidarComOrigensDePagamentoEspeciais() throws Exception {
            String origemEspecial = "Origem @#$% 123";
            ReceitaCreateDTO dto = criarReceitaCreateDTO();
            dto.setOrigemDoPagamento(origemEspecial);
            
            mockMvc.perform(post("/receitas")
                    .header("Authorization", authHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.origemDoPagamento").value(origemEspecial));
        }
    }

    private UserEntity criarUsuarioTest() {
        UserEntity newUser = new UserEntity();
        newUser.setUsername("Lana");
        newUser.setEmail("lana@gmail.com");
        newUser.setPassword(passwordEncoder.encode("123456"));
        newUser.setRole(Roles.USER);
        
        return userRepository.saveAndFlush(newUser);
    }

    private CategoriaEntity criarCategoriaTest(String nome, UserEntity user) {
        return categoriaRepository.findByNomeAndTipoAndUserUuid(nome, CategoriaType.RECEITAS, user.getUuid())
            .orElseGet(() -> {
                CategoriaEntity categoria = new CategoriaEntity();
                categoria.setNome(nome);
                categoria.setTipo(CategoriaType.RECEITAS);
                categoria.setUser(user);
                return categoriaRepository.save(categoria);
            });
    }

    private ReceitaEntity criarReceitaNoBanco() {
        return criarReceitaNoBanco(VALOR_PADRAO, LocalDate.now(), CATEGORIA_PADRAO);
    }
    
    private ReceitaEntity criarReceitaNoBanco(BigDecimal valor, LocalDate data) {
        return criarReceitaNoBanco(valor, data, CATEGORIA_PADRAO);
    }
    
    private ReceitaEntity criarReceitaNoBanco(BigDecimal valor, LocalDate data, String categoriaNome) {
        CategoriaEntity categoria = categoriaRepository.findByNomeAndTipoAndUserUuid(
                categoriaNome, CategoriaType.RECEITAS, user.getUuid())
            .orElseGet(() -> {
                CategoriaEntity novaCategoria = new CategoriaEntity();
                novaCategoria.setNome(categoriaNome);
                novaCategoria.setTipo(CategoriaType.RECEITAS);
                novaCategoria.setUser(user);
                return categoriaRepository.save(novaCategoria);
            });
    
        ReceitaEntity receita = new ReceitaEntity();
        receita.setValor(valor);
        receita.setData(data);
        receita.setCategoria(categoria);
        receita.setOrigemDoPagamento(ORIGEM_RECEITA_PADRAO);
        receita.setObservacoes(OBSERVACOES_PADRAO);
        receita.setUser(user);
        
        return receitaRepository.save(receita);
    }

    private ReceitaCreateDTO criarReceitaCreateDTO(BigDecimal valor, LocalDate data, String categoria) {
        ReceitaCreateDTO dto = new ReceitaCreateDTO();
        dto.setValor(valor);
        dto.setData(data);
        dto.setCategoria(categoria);
        dto.setOrigemDoPagamento(ORIGEM_RECEITA_PADRAO);
        dto.setObservacoes(OBSERVACOES_PADRAO);
        return dto;
    }

    private ReceitaCreateDTO criarReceitaCreateDTO() {
        return criarReceitaCreateDTO(VALOR_PADRAO, LocalDate.now(), CATEGORIA_PADRAO);
    }

    private ReceitaUpdateDTO criarReceitaUpdateDTO(ReceitaEntity receita) {
        ReceitaUpdateDTO dto = new ReceitaUpdateDTO();
        dto.setValor(VALOR_ATUALIZADO);
        dto.setData(receita.getData());
        dto.setCategoria(receita.getCategoria().getNome());
        dto.setOrigemDoPagamento(receita.getOrigemDoPagamento());
        dto.setObservacoes(receita.getObservacoes());
        return dto;
    }

    private ReceitaUpdateDTO criarReceitaUpdateDTO() {
        ReceitaUpdateDTO dto = new ReceitaUpdateDTO();
        dto.setValor(VALOR_ATUALIZADO);
        dto.setData(LocalDate.now());
        dto.setCategoria(CATEGORIA_PADRAO);
        dto.setOrigemDoPagamento(ORIGEM_RECEITA_PADRAO);
        dto.setObservacoes(OBSERVACOES_PADRAO);
        return dto;
    }
}