package br.com.gestorfinanceiro.services.ReceitasServiceTest;

import br.com.gestorfinanceiro.dto.grafico.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoPizzaDTO;
import br.com.gestorfinanceiro.dto.receita.ReceitaCreateDTO;
import br.com.gestorfinanceiro.dto.receita.ReceitaUpdateDTO;
import br.com.gestorfinanceiro.exceptions.common.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.common.InvalidUuidException;
import br.com.gestorfinanceiro.exceptions.receita.ReceitaNotFoundException;
import br.com.gestorfinanceiro.exceptions.user.InvalidUserIdException;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.models.enums.Roles;
import br.com.gestorfinanceiro.repositories.CategoriaRepository;
import br.com.gestorfinanceiro.repositories.ReceitaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.ReceitaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ReceitaServiceIntegrationTest {

    private static final String CATEGORIA_PADRAO = "Salario";
    private static final String ORIGEM_PAGAMENTO_PADRAO = "Empresa X";
    private static final String OBSERVACOES_PADRAO = "Remuneracao";
    private static final BigDecimal VALOR_PADRAO = BigDecimal.valueOf(10000);
    private static final BigDecimal VALOR_ATUALIZADO = BigDecimal.valueOf(20000);
    private static final BigDecimal VALOR_NEGATIVO = BigDecimal.valueOf(-100);

    @Autowired
    private ReceitaService receitaService;

    @Autowired
    private ReceitaRepository receitaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        limparBaseDeDados();
        user = criarUsuarioTest();
        criarCategoriaTest(CATEGORIA_PADRAO, user);
    }

    @AfterEach
    void tearDown() {
        receitaRepository.deleteAllInBatch();
        categoriaRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    private void limparBaseDeDados() {
        receitaRepository.deleteAll();
        categoriaRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void deveCarregarReceitaService() {
        assertNotNull(receitaService, "O ReceitaService não deveria ser nulo!");
    }

    @Nested
    class CriarReceitaTest {

        @Test
        void deveCriarReceita() {
            ReceitaCreateDTO receitaDTO = criarReceitaCreateDTO();

            ReceitaEntity receitaSalva = receitaService.criarReceita(receitaDTO, user.getUuid());

            assertNotNull(receitaSalva);
            assertEquals(receitaDTO.getValor(), receitaSalva.getValor());
            assertEquals(receitaDTO.getCategoria(), receitaSalva.getCategoria().getNome());
        }

        @Test
        void erroAoCriarReceitaComValorInvalido() {
            ReceitaCreateDTO receitaDTO = criarReceitaCreateDTO();
            receitaDTO.setValor(BigDecimal.ZERO);
            String userId = user.getUuid();

            assertThrows(InvalidDataException.class, () -> receitaService.criarReceita(receitaDTO, userId));
        }

        @Test
        void erroAoCriarReceitaComValorNulo() {
            ReceitaCreateDTO receitaDTO = criarReceitaCreateDTO();
            receitaDTO.setValor(null);
            String userId = user.getUuid();

            assertThrows(InvalidDataException.class, () -> receitaService.criarReceita(receitaDTO, userId));
        }

        @Test
        void erroAoCriarReceitaComValorNegativo() {
            ReceitaCreateDTO receitaDTO = criarReceitaCreateDTO();
            receitaDTO.setValor(VALOR_NEGATIVO);
            String userId = user.getUuid();

            assertThrows(InvalidDataException.class, () -> receitaService.criarReceita(receitaDTO, userId));
        }

        @Test
        void erroAoCriarReceitaUsuarioNaoEncontrado() {
            ReceitaCreateDTO receitaDTO = criarReceitaCreateDTO();
            String usuarioInexistente = UUID.randomUUID().toString();

            assertThrows(RuntimeException.class, () -> receitaService.criarReceita(receitaDTO, usuarioInexistente));
        }
    }

    @Nested
    class ListarReceitasTest {

        @Test
        void deveListarReceitasUsuario() {
            receitaService.criarReceita(criarReceitaCreateDTO(), user.getUuid());

            List<ReceitaEntity> receitas = receitaService.listarReceitasUsuario(user.getUuid());
            assertFalse(receitas.isEmpty());
        }

        @Test
        void erroAoListarReceitasUsuarioComIdNuloOuVazio() {
            String uuid = null;
            assertThrows(InvalidUserIdException.class, () -> receitaService.listarReceitasUsuario(uuid));
            assertThrows(InvalidUserIdException.class, () -> receitaService.listarReceitasUsuario(""));
        }

        @Test
        void erroAoListarReceitasVazia() {
            String userId = user.getUuid();

            assertThrows(ReceitaNotFoundException.class, () -> receitaService.listarReceitasUsuario(userId));
        }
    }

    @Nested
    class BuscarReceitaPorIdTest {

        @Test
        void deveBuscarReceitaPorId() {
            ReceitaEntity receita = receitaService.criarReceita(criarReceitaCreateDTO(), user.getUuid());

            ReceitaEntity receitaEncontrada = receitaService.buscarReceitaPorId(receita.getUuid());
            assertEquals(receita.getUuid(), receitaEncontrada.getUuid());
        }

        @Test
        void erroAoBuscarReceitaPorIdInexistente() {
            String uuid = UUID.randomUUID().toString();
            assertThrows(ReceitaNotFoundException.class, () -> receitaService.buscarReceitaPorId(uuid));
        }

        @Test
        void erroAoBuscarReceitaPorIdNuloOuVazio() {
            String uuid = null;

            assertThrows(InvalidUuidException.class, () -> receitaService.buscarReceitaPorId(uuid));
            assertThrows(InvalidUuidException.class, () -> receitaService.buscarReceitaPorId(""));
        }
    }

    @Nested
    class AtualizarReceitaTest {

        @Test
        void deveAtualizarReceita() {
            ReceitaEntity receita = receitaService.criarReceita(criarReceitaCreateDTO(), user.getUuid());

            receita.setValor(VALOR_ATUALIZADO);

            ReceitaUpdateDTO receitaAtualizada = criarReceitaUpdateDTO(receita);
            receita = receitaService.atualizarReceita(receita.getUuid(), receitaAtualizada);

            assertEquals(BigDecimal.valueOf(20000), receita.getValor());
        }

        @Test
        void erroAoAtualizarReceitaInexistente() {
            ReceitaUpdateDTO receita = criarReceitaUpdateDTO();
            String uuidInexistente = UUID.randomUUID().toString();

            assertThrows(ReceitaNotFoundException.class, () -> receitaService.atualizarReceita(uuidInexistente, receita));
        }

        @Test
        void erroAoAtualizarReceitaNula() {
            ReceitaUpdateDTO receita = null;
            String uuid = UUID.randomUUID().toString();

            assertThrows(InvalidDataException.class, () -> receitaService.atualizarReceita(uuid, receita));
        }

        @Test
        void erroAoAtualizarReceitaUuidNuloOuVazio() {
            ReceitaUpdateDTO receita = criarReceitaUpdateDTO();
            String uuid = null;

            assertThrows(InvalidUuidException.class, () -> receitaService.atualizarReceita(uuid, receita));
            assertThrows(InvalidUuidException.class, () -> receitaService.atualizarReceita("", receita));
        }
    }

    @Nested
    class ExcluirReceitaTest {

        @Test
        void deveExcluirReceita() {
            ReceitaEntity receita = receitaService.criarReceita(criarReceitaCreateDTO(), user.getUuid());

            receitaService.excluirReceita(receita.getUuid());
            assertFalse(receitaRepository.findById(receita.getUuid()).isPresent());
        }

        @Test
        void erroAoExcluirReceitaInexistente() {
            String uuidInexistente = UUID.randomUUID().toString();

            assertThrows(ReceitaNotFoundException.class, () -> receitaService.excluirReceita(uuidInexistente));
        }

        @Test
        void erroAoExcluirReceitaComUuidNuloOuVazio() {
            String uuidNull = null;

            assertThrows(InvalidUuidException.class, () -> receitaService.excluirReceita(uuidNull));
            assertThrows(InvalidUuidException.class, () -> receitaService.excluirReceita(""));
        }
    }

    @Nested
    class GerarGraficoTest {

        @Test
        void deveGerarGraficoBarras() {
            criarReceitaComValorEData(user, BigDecimal.valueOf(100), LocalDate.of(2024, 1, 10));
            criarReceitaComValorEData(user, BigDecimal.valueOf(200), LocalDate.of(2024, 2, 15));
            criarReceitaComValorEData(user, BigDecimal.valueOf(150), LocalDate.of(2024, 2, 20));

            YearMonth inicio = YearMonth.of(2024, 1);
            YearMonth fim = YearMonth.of(2024, 2);

            GraficoBarraDTO grafico = receitaService.gerarGraficoBarras(user.getUuid(), inicio, fim);

            // Verifica se os valores por mês estão corretos
            assertNotNull(grafico);
            assertEquals(2, grafico.dadosMensais().size());
            assertEquals(
                    BigDecimal.valueOf(100).stripTrailingZeros(),
                    grafico.dadosMensais().get("janeiro 2024").stripTrailingZeros()
            );

            assertEquals(
                    BigDecimal.valueOf(350).stripTrailingZeros(),
                    grafico.dadosMensais().get("fevereiro 2024").stripTrailingZeros()
            );

        }

        @Test
        void deveGerarGraficoPizza() {
            CategoriaEntity bonus = criarCategoriaTest("Bonus", user);
            CategoriaEntity extra = criarCategoriaTest("Extra", user);

            criarReceitaComCategoriaEValor(user, BigDecimal.valueOf(300), CATEGORIA_PADRAO);
            criarReceitaComCategoriaEValor(user, BigDecimal.valueOf(200), bonus.getNome());
            criarReceitaComCategoriaEValor(user, BigDecimal.valueOf(100), extra.getNome());
            criarReceitaComCategoriaEValor(user, BigDecimal.valueOf(100), extra.getNome());

            LocalDate inicio = LocalDate.of(2025, 1, 1);
            LocalDate fim = LocalDate.of(2025, 12, 31);

            GraficoPizzaDTO grafico = receitaService.gerarGraficoPizza(user.getUuid(), inicio, fim);

            // Verifica se os valores por categoria estão corretos
            assertNotNull(grafico);
            assertEquals(3, grafico.categorias().size());
            assertEquals(BigDecimal.valueOf(300).stripTrailingZeros(), grafico.categorias().get("Salario").stripTrailingZeros());
            assertEquals(BigDecimal.valueOf(200).stripTrailingZeros(), grafico.categorias().get("Bonus").stripTrailingZeros());
            assertEquals(BigDecimal.valueOf(200).stripTrailingZeros(), grafico.categorias().get("Extra").stripTrailingZeros());


        }
    }

    @Nested
    class BuscaAvancadaTest {

        @Test
        void deveBuscarReceitasPorIntervaloDeDatas() {
            String userId = user.getUuid();

            // Criando receitas em datas diferentes
            criarReceitaComValorEData(user, BigDecimal.valueOf(100), LocalDate.of(2024, 1, 10));
            criarReceitaComValorEData(user, BigDecimal.valueOf(200), LocalDate.of(2024, 2, 15));
            criarReceitaComValorEData(user, BigDecimal.valueOf(150), LocalDate.of(2024, 2, 20));

            LocalDate inicio = LocalDate.of(2024, 1, 1);
            LocalDate fim = LocalDate.of(2024, 2, 18);

            List<ReceitaEntity> receitas = receitaService.buscarReceitasPorIntervaloDeDatas(userId, inicio, fim);

            // Verifica se as receitas estão corretas
            assertNotNull(receitas);
            assertEquals(2, receitas.size());
            assertEquals(BigDecimal.valueOf(100).stripTrailingZeros(), receitas.get(0).getValor().stripTrailingZeros());
            assertEquals(BigDecimal.valueOf(200).stripTrailingZeros(), receitas.get(1).getValor().stripTrailingZeros());
        }

        @Test
        void erroAoBuscarReceitasPorIntervaloDeDatasComUserIdNuloOuVazio() {
            LocalDate inicio = LocalDate.of(2024, 1, 1);
            LocalDate fim = LocalDate.of(2024, 2, 18);

            assertThrows(InvalidUserIdException.class, () -> receitaService.buscarReceitasPorIntervaloDeDatas(null, inicio, fim));
            assertThrows(InvalidUserIdException.class, () -> receitaService.buscarReceitasPorIntervaloDeDatas("", inicio, fim));
        }

        @Test
        void erroAoBuscarReceitasPorIntervaloDeDatasComDatasNulas() {
            String userId = user.getUuid();
            LocalDate inicio = null;
            LocalDate inicio2 = LocalDate.of(2024, 1, 1);
            LocalDate fim = null;
            LocalDate fim2 = LocalDate.of(2024, 2, 18);

            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeDatas(userId, inicio, fim2));
            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeDatas(userId, inicio2, fim));
        }

        @Test
        void erroAoBuscarReceitasPorIntervaloDeDatasComInicioDepoisDoFim() {
            String userId = user.getUuid();
            LocalDate inicio = LocalDate.of(2024, 1, 10);
            LocalDate fim = LocalDate.of(2024, 1, 5);

            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeDatas(userId, inicio, fim));
        }

        @Test
        void deveBuscarReceitasPorIntervaloDeValores() {
            String userId = user.getUuid();

            // Criando receitas com valores diferentes
            criarReceitaComValorEData(user, BigDecimal.valueOf(100), LocalDate.of(2024, 1, 10));
            criarReceitaComValorEData(user, BigDecimal.valueOf(200), LocalDate.of(2024, 2, 15));
            criarReceitaComValorEData(user, BigDecimal.valueOf(150), LocalDate.of(2024, 2, 20));

            BigDecimal min = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.valueOf(150);

            List<ReceitaEntity> receitas = receitaService.buscarReceitasPorIntervaloDeValores(userId, min, max);

            // Verifica se as receitas estão corretas
            assertNotNull(receitas);
            assertEquals(2, receitas.size());
            assertEquals(BigDecimal.valueOf(100).stripTrailingZeros(), receitas.get(0).getValor().stripTrailingZeros());
            assertEquals(BigDecimal.valueOf(150).stripTrailingZeros(), receitas.get(1).getValor().stripTrailingZeros());
        }

        @Test
        void erroAoBuscarReceitasPorIntervaloDeValoresComUserIdNuloOuVazio() {
            BigDecimal min = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.valueOf(150);

            assertThrows(InvalidUserIdException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores(null, min, max));
            assertThrows(InvalidUserIdException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores("", min, max));
        }

        @Test
        void erroAoBuscarReceitasPorIntervaloDeValoresComValoresNulos() {
            String userId = user.getUuid();
            BigDecimal min = null;
            BigDecimal min2 = BigDecimal.valueOf(100);
            BigDecimal max = null;
            BigDecimal max2 = BigDecimal.valueOf(150);

            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores(userId, min, max2));
            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores(userId, min2, max));
        }

        @Test
        void erroAoBuscarReceitasPorIntervaloDeValoresComValoresMenoresOuIguaisAZero() {
            String userId = user.getUuid();
            BigDecimal min = BigDecimal.ZERO;
            BigDecimal min2 = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.ZERO;
            BigDecimal max2 = BigDecimal.valueOf(150);

            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores(userId, min, max2));
            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores(userId, min2, max));
        }

        @Test
        void erroAoBuscarReceitasPorIntervaloDeValoresComMinMaiorQueMax() {
            String userId = user.getUuid();
            BigDecimal min = BigDecimal.valueOf(200);
            BigDecimal max = BigDecimal.valueOf(100);

            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores(userId, min, max));
        }
    }

    //----------------- Métodos Auxiliares -----------------//

    private UserEntity criarUsuarioTest() {
        UserEntity userTest = new UserEntity();
        userTest.setUsername("Jorge");
        userTest.setEmail("jorge@gmail.com");
        userTest.setPassword("123456");
        userTest.setRole(Roles.USER);
        return userRepository.save(userTest);
    }

    private CategoriaEntity criarCategoriaTest(String nome, UserEntity user) {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNome(nome);
        categoria.setTipo(CategoriaType.RECEITAS);
        categoria.setUser(user);
        return categoriaRepository.save(categoria);
    }

    private ReceitaCreateDTO criarReceitaCreateDTO(BigDecimal valor, LocalDate data, String categoria) {
        ReceitaCreateDTO receita = new ReceitaCreateDTO();
        receita.setData(data);
        receita.setCategoria(categoria);
        receita.setOrigemDoPagamento(ORIGEM_PAGAMENTO_PADRAO);
        receita.setObservacoes(OBSERVACOES_PADRAO);
        receita.setValor(valor);
        return receita;
    }

    private ReceitaCreateDTO criarReceitaCreateDTO() {
        return criarReceitaCreateDTO(
                VALOR_PADRAO,
                LocalDate.now(),
                CATEGORIA_PADRAO
        );
    }

    private ReceitaUpdateDTO criarReceitaUpdateDTO(ReceitaEntity oldReceita) {
        ReceitaUpdateDTO receita = new ReceitaUpdateDTO();
        receita.setData(oldReceita.getData());
        receita.setCategoria(oldReceita.getCategoria().getNome());
        receita.setOrigemDoPagamento(ORIGEM_PAGAMENTO_PADRAO);
        receita.setObservacoes(OBSERVACOES_PADRAO);
        receita.setValor(oldReceita.getValor());
        return receita;
    }

    private ReceitaUpdateDTO criarReceitaUpdateDTO() {
        ReceitaUpdateDTO receita = new ReceitaUpdateDTO();
        receita.setData(LocalDate.now());
        receita.setCategoria(CATEGORIA_PADRAO);
        receita.setOrigemDoPagamento(ORIGEM_PAGAMENTO_PADRAO);
        receita.setObservacoes(OBSERVACOES_PADRAO);
        receita.setValor(VALOR_PADRAO);
        return receita;
    }

    private void criarReceitaComValorEData(UserEntity user, BigDecimal valor, LocalDate data) {
        receitaService.criarReceita(
                criarReceitaCreateDTO(valor, data, CATEGORIA_PADRAO),
                user.getUuid()
        );
    }

    private void criarReceitaComCategoriaEValor(UserEntity user, BigDecimal valor, String categoria) {
        receitaService.criarReceita(
                criarReceitaCreateDTO(valor, LocalDate.of(2025, 3, 12), categoria),
                user.getUuid()
        );
    }

}
