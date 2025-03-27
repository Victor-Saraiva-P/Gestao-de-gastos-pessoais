package br.com.gestorfinanceiro.services.DespesaServiceTest;

import br.com.gestorfinanceiro.dto.despesa.DespesaCreateDTO;
import br.com.gestorfinanceiro.dto.despesa.DespesaUpdateDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoPizzaDTO;
import br.com.gestorfinanceiro.exceptions.common.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.common.InvalidUuidException;
import br.com.gestorfinanceiro.exceptions.despesa.DespesaNotFoundException;
import br.com.gestorfinanceiro.exceptions.user.InvalidUserIdException;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.models.enums.Roles;
import br.com.gestorfinanceiro.repositories.CategoriaRepository;
import br.com.gestorfinanceiro.repositories.DespesaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.DespesaService;
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
class DespesaServiceIntegrationTest {

    private static final String CATEGORIA_PADRAO = "Alimentacao";
    private static final String DESTINO_PAGAMENTO_PADRAO = "Mercado";
    private static final String OBSERVACOES_PADRAO = "Compras do mês";
    private static final BigDecimal VALOR_PADRAO = BigDecimal.valueOf(100);
    private static final BigDecimal VALOR_ATUALIZADO = BigDecimal.valueOf(200);
    private static final BigDecimal VALOR_NEGATIVO = BigDecimal.valueOf(-100);

    @Autowired
    private DespesaService despesaService;

    @Autowired
    private DespesaRepository despesaRepository;

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

    private void limparBaseDeDados() {
        despesaRepository.deleteAll();
        categoriaRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void deveCarregarDespesaService() {
        assertNotNull(despesaService, "O DespesaService não deveria ser nulo!");
    }

    @Nested
    class CriarDespesaTest {
        @Test
        void deveCriarDespesa() {
            DespesaCreateDTO despesaDTO = criarDespesaCreateDTO();

            DespesaEntity despesaSalva = despesaService.criarDespesa(despesaDTO, user.getUuid());

            assertNotNull(despesaSalva);
            assertEquals(despesaDTO.getValor(), despesaSalva.getValor());
            assertEquals(despesaDTO.getCategoria(), despesaSalva.getCategoria().getNome());
        }

        @Test
        void erroAoCriarDespesaComValorInvalido() {
            DespesaCreateDTO despesaDTO = criarDespesaCreateDTO(
                    BigDecimal.ZERO, LocalDate.now(), CATEGORIA_PADRAO
            );

            assertThrows(InvalidDataException.class,
                    () -> despesaService.criarDespesa(despesaDTO, user.getUuid()));
        }

        @Test
        void erroAoCriarDespesaComValorNulo() {
            DespesaCreateDTO despesaDTO = criarDespesaCreateDTO(
                    null, LocalDate.now(), CATEGORIA_PADRAO
            );

            assertThrows(InvalidDataException.class,
                    () -> despesaService.criarDespesa(despesaDTO, user.getUuid()));
        }

        @Test
        void erroAoCriarDespesaComValorNegativo() {
            DespesaCreateDTO despesaDTO = criarDespesaCreateDTO(
                    VALOR_NEGATIVO, LocalDate.now(), CATEGORIA_PADRAO
            );

            assertThrows(InvalidDataException.class,
                    () -> despesaService.criarDespesa(despesaDTO, user.getUuid()));
        }

        @Test
        void erroAoCriarDespesaUsuarioNaoEncontrado() {
            DespesaCreateDTO despesaDTO = criarDespesaCreateDTO();
            String usuarioInexistente = UUID.randomUUID().toString();

            assertThrows(RuntimeException.class,
                    () -> despesaService.criarDespesa(despesaDTO, usuarioInexistente));
        }
    }

    @Nested
    class ListarDespesasTest {
        @Test
        void deveListarDespesasUsuario() {
            despesaService.criarDespesa(criarDespesaCreateDTO(), user.getUuid());

            List<DespesaEntity> despesas = despesaService.listarDespesasUsuario(user.getUuid());

            assertFalse(despesas.isEmpty());
        }

        @Test
        void erroAoListarDespesasUsuarioComIdNuloOuVazio() {
            assertThrows(InvalidUserIdException.class,
                    () -> despesaService.listarDespesasUsuario(null));
            assertThrows(InvalidUserIdException.class,
                    () -> despesaService.listarDespesasUsuario(""));
        }

        @Test
        void erroAoListarDespesasVazia() {
            assertThrows(DespesaNotFoundException.class,
                    () -> despesaService.listarDespesasUsuario(user.getUuid()));
        }
    }

    @Nested
    class BuscarDespesaPorIdTest {
        @Test
        void deveBuscarDespesaPorId() {
            DespesaEntity despesa = despesaService.criarDespesa(
                    criarDespesaCreateDTO(), user.getUuid());

            DespesaEntity despesaEncontrada = despesaService.buscarDespesaPorId(despesa.getUuid());

            assertEquals(despesa.getUuid(), despesaEncontrada.getUuid());
        }

        @Test
        void erroAoBuscarDespesaPorIdInexistente() {
            String uuid = UUID.randomUUID().toString();

            assertThrows(DespesaNotFoundException.class,
                    () -> despesaService.buscarDespesaPorId(uuid));
        }

        @Test
        void erroAoBuscarDespesaPorIdNuloOuVazio() {
            assertThrows(InvalidUuidException.class,
                    () -> despesaService.buscarDespesaPorId(null));
            assertThrows(InvalidUuidException.class,
                    () -> despesaService.buscarDespesaPorId(""));
        }
    }

    @Nested
    class AtualizarDespesaTest {
        @Test
        void deveAtualizarDespesa() {
            DespesaEntity despesa = despesaService.criarDespesa(
                    criarDespesaCreateDTO(), user.getUuid());

            DespesaUpdateDTO despesaUpdateDTO = criarDespesaUpdateDTO(despesa);

            DespesaEntity despesaAtualizada = despesaService.atualizarDespesa(
                    despesa.getUuid(), despesaUpdateDTO);

            assertEquals(VALOR_ATUALIZADO, despesaAtualizada.getValor());
        }

        @Test
        void erroAoAtualizarDespesaInexistente() {
            DespesaUpdateDTO despesa = criarDespesaUpdateDTO();
            String uuidInexistente = UUID.randomUUID().toString();

            assertThrows(DespesaNotFoundException.class,
                    () -> despesaService.atualizarDespesa(uuidInexistente, despesa));
        }

        @Test
        void erroAoAtualizarDespesaNula() {
            String uuid = UUID.randomUUID().toString();

            assertThrows(InvalidDataException.class,
                    () -> despesaService.atualizarDespesa(uuid, null));
        }

        @Test
        void erroAoAtualizarUuidNuloOuVazio() {
            DespesaUpdateDTO despesa = criarDespesaUpdateDTO();

            assertThrows(InvalidUuidException.class,
                    () -> despesaService.atualizarDespesa(null, despesa));
            assertThrows(InvalidUuidException.class,
                    () -> despesaService.atualizarDespesa("", despesa));
        }
    }

    @Nested
    class ExcluirDespesaTest {
        @Test
        void deveExcluirDespesa() {
            DespesaEntity despesa = despesaService.criarDespesa(
                    criarDespesaCreateDTO(), user.getUuid());

            despesaService.excluirDespesa(despesa.getUuid());

            assertFalse(despesaRepository.findById(despesa.getUuid()).isPresent());
        }

        @Test
        void erroAoExcluirDespesaInexistente() {
            String uuidInexistente = UUID.randomUUID().toString();

            assertThrows(DespesaNotFoundException.class,
                    () -> despesaService.excluirDespesa(uuidInexistente));
        }

        @Test
        void erroAoExcluirDespesaComUuidNuloOuVazio() {
            assertThrows(InvalidUuidException.class,
                    () -> despesaService.excluirDespesa(null));
            assertThrows(InvalidUuidException.class,
                    () -> despesaService.excluirDespesa(""));
        }
    }

    @Nested
    class GerarGraficosTest {
        @Test
        void deveGerarGraficoBarras() {
            criarDespesaComValorEData(user, BigDecimal.valueOf(100), LocalDate.of(2024, 1, 10));
            criarDespesaComValorEData(user, BigDecimal.valueOf(200), LocalDate.of(2024, 2, 15));
            criarDespesaComValorEData(user, BigDecimal.valueOf(150), LocalDate.of(2024, 2, 20));

            YearMonth inicio = YearMonth.of(2024, 1);
            YearMonth fim = YearMonth.of(2024, 2);

            GraficoBarraDTO grafico = despesaService.gerarGraficoBarras(user.getUuid(), inicio, fim);

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
            CategoriaEntity transporte = criarCategoriaTest("Transporte", user);
            CategoriaEntity lazer = criarCategoriaTest("Lazer", user);

            criarDespesaComCategoriaEValor(user, BigDecimal.valueOf(300), CATEGORIA_PADRAO);
            criarDespesaComCategoriaEValor(user, BigDecimal.valueOf(200), transporte.getNome());
            criarDespesaComCategoriaEValor(user, BigDecimal.valueOf(100), lazer.getNome());
            criarDespesaComCategoriaEValor(user, BigDecimal.valueOf(100), lazer.getNome());

            LocalDate inicio = LocalDate.of(2025, 1, 1);
            LocalDate fim = LocalDate.of(2025, 12, 31);

            GraficoPizzaDTO grafico = despesaService.gerarGraficoPizza(user.getUuid(), inicio, fim);

            assertNotNull(grafico);
            assertEquals(3, grafico.categorias().size());
            assertEquals(
                    BigDecimal.valueOf(300).stripTrailingZeros(),
                    grafico.categorias().get(CATEGORIA_PADRAO).stripTrailingZeros());
            assertEquals(
                    BigDecimal.valueOf(200).stripTrailingZeros(),
                    grafico.categorias().get("Transporte").stripTrailingZeros());
            assertEquals(
                    BigDecimal.valueOf(200).stripTrailingZeros(),
                    grafico.categorias().get("Lazer").stripTrailingZeros());
        }
    }

    @Nested
    class BuscaAvancadaTest {
        private static final LocalDate DATA_INICIO_2024 = LocalDate.of(2024, 1, 1);
        private static final LocalDate DATA_FIM_2024 = LocalDate.of(2024, 2, 18);
        private static final LocalDate DATA_10_JAN_2024 = LocalDate.of(2024, 1, 10);
        private static final LocalDate DATA_15_FEV_2024 = LocalDate.of(2024, 2, 15);
        private static final LocalDate DATA_20_FEV_2024 = LocalDate.of(2024, 2, 20);
        private static final LocalDate DATA_5_JAN_2024 = LocalDate.of(2024, 1, 5);

        @Test
        void deveBuscarDespesasPorIntervaloDeDatas() {
            criarDespesaComValorEData(user, BigDecimal.valueOf(100), DATA_10_JAN_2024);
            criarDespesaComValorEData(user, BigDecimal.valueOf(200), DATA_15_FEV_2024);
            criarDespesaComValorEData(user, BigDecimal.valueOf(150), DATA_20_FEV_2024);

            List<DespesaEntity> despesas = despesaService.buscarDespesasPorIntervaloDeDatas(
                    user.getUuid(), DATA_INICIO_2024, DATA_FIM_2024);

            assertNotNull(despesas);
            assertEquals(2, despesas.size());
            assertEquals(
                    BigDecimal.valueOf(100).stripTrailingZeros(),
                    despesas.get(0).getValor().stripTrailingZeros());
            assertEquals(
                    BigDecimal.valueOf(200).stripTrailingZeros(),
                    despesas.get(1).getValor().stripTrailingZeros());
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeDatasComUserIdNuloOuVazio() {
            assertThrows(InvalidUserIdException.class,
                    () -> despesaService.buscarDespesasPorIntervaloDeDatas(null, DATA_INICIO_2024, DATA_FIM_2024));
            assertThrows(InvalidUserIdException.class,
                    () -> despesaService.buscarDespesasPorIntervaloDeDatas("", DATA_INICIO_2024, DATA_FIM_2024));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeDatasComDatasNulas() {
            assertThrows(InvalidDataException.class,
                    () -> despesaService.buscarDespesasPorIntervaloDeDatas(user.getUuid(), null, DATA_FIM_2024));
            assertThrows(InvalidDataException.class,
                    () -> despesaService.buscarDespesasPorIntervaloDeDatas(user.getUuid(), DATA_INICIO_2024, null));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeDatasComInicioDepoisDoFim() {
            assertThrows(InvalidDataException.class,
                    () -> despesaService.buscarDespesasPorIntervaloDeDatas(
                            user.getUuid(), DATA_10_JAN_2024, DATA_5_JAN_2024));
        }

        @Test
        void deveBuscarDespesasPorIntervaloDeValores() {
            criarDespesaComValorEData(user, BigDecimal.valueOf(100), DATA_10_JAN_2024);
            criarDespesaComValorEData(user, BigDecimal.valueOf(200), DATA_15_FEV_2024);
            criarDespesaComValorEData(user, BigDecimal.valueOf(150), DATA_20_FEV_2024);

            List<DespesaEntity> despesas = despesaService.buscarDespesasPorIntervaloDeValores(
                    user.getUuid(), BigDecimal.valueOf(100), BigDecimal.valueOf(150));

            assertNotNull(despesas);
            assertEquals(2, despesas.size());
            assertEquals(
                    BigDecimal.valueOf(100).stripTrailingZeros(),
                    despesas.get(0).getValor().stripTrailingZeros());
            assertEquals(
                    BigDecimal.valueOf(150).stripTrailingZeros(),
                    despesas.get(1).getValor().stripTrailingZeros());
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeValoresComUserIdNuloOuVazio() {
            assertThrows(InvalidUserIdException.class,
                    () -> despesaService.buscarDespesasPorIntervaloDeValores(null, VALOR_PADRAO, VALOR_ATUALIZADO));
            assertThrows(InvalidUserIdException.class,
                    () -> despesaService.buscarDespesasPorIntervaloDeValores("", VALOR_PADRAO, VALOR_ATUALIZADO));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeValoresComValoresNulos() {
            assertThrows(InvalidDataException.class,
                    () -> despesaService.buscarDespesasPorIntervaloDeValores(user.getUuid(), null, VALOR_ATUALIZADO));
            assertThrows(InvalidDataException.class,
                    () -> despesaService.buscarDespesasPorIntervaloDeValores(user.getUuid(), VALOR_PADRAO, null));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeValoresComValoresMenoresOuIguaisAZero() {
            assertThrows(InvalidDataException.class,
                    () -> despesaService.buscarDespesasPorIntervaloDeValores(user.getUuid(), BigDecimal.ZERO, VALOR_ATUALIZADO));
            assertThrows(InvalidDataException.class,
                    () -> despesaService.buscarDespesasPorIntervaloDeValores(user.getUuid(), VALOR_PADRAO, BigDecimal.ZERO));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeValoresComMinMaiorQueMax() {
            assertThrows(InvalidDataException.class,
                    () -> despesaService.buscarDespesasPorIntervaloDeValores(user.getUuid(), VALOR_ATUALIZADO, VALOR_PADRAO));
        }
    }

    //----------------- Métodos Auxiliares Melhorados -----------------//

    private UserEntity criarUsuarioTest() {
        UserEntity user = new UserEntity();
        user.setUsername("Jorge");
        user.setEmail("jorge@gmail.com");
        user.setPassword("123456");
        user.setRole(Roles.USER);
        return userRepository.save(user);
    }

    private CategoriaEntity criarCategoriaTest(String nome, UserEntity user) {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNome(nome);
        categoria.setTipo(CategoriaType.DESPESAS);
        categoria.setUser(user);
        return categoriaRepository.save(categoria);
    }

    private DespesaCreateDTO criarDespesaCreateDTO(BigDecimal valor, LocalDate data, String categoria) {
        DespesaCreateDTO dto = new DespesaCreateDTO();
        dto.setValor(valor);
        dto.setData(data);
        dto.setCategoria(categoria);
        dto.setDestinoPagamento(DespesaServiceIntegrationTest.DESTINO_PAGAMENTO_PADRAO);
        dto.setObservacoes(DespesaServiceIntegrationTest.OBSERVACOES_PADRAO);
        return dto;
    }

    private DespesaCreateDTO criarDespesaCreateDTO() {
        return criarDespesaCreateDTO(
                VALOR_PADRAO,
                LocalDate.now(),
                CATEGORIA_PADRAO
        );
    }

    private DespesaUpdateDTO criarDespesaUpdateDTO() {
        DespesaUpdateDTO dto = new DespesaUpdateDTO();
        dto.setValor(VALOR_ATUALIZADO);
        dto.setData(LocalDate.now());
        dto.setCategoria(CATEGORIA_PADRAO);
        dto.setDestinoPagamento(DESTINO_PAGAMENTO_PADRAO);
        dto.setObservacoes(OBSERVACOES_PADRAO);
        return dto;
    }

    private DespesaUpdateDTO criarDespesaUpdateDTO(DespesaEntity despesa) {
        DespesaUpdateDTO dto = new DespesaUpdateDTO();
        dto.setValor(DespesaServiceIntegrationTest.VALOR_ATUALIZADO);
        dto.setData(despesa.getData());
        dto.setCategoria(despesa.getCategoria().getNome());
        dto.setDestinoPagamento(despesa.getDestinoPagamento());
        dto.setObservacoes(despesa.getObservacoes());
        return dto;
    }

    private void criarDespesaComValorEData(UserEntity user, BigDecimal valor, LocalDate data) {
        despesaService.criarDespesa(
                criarDespesaCreateDTO(valor, data, CATEGORIA_PADRAO),
                user.getUuid()
        );
    }

    private void criarDespesaComCategoriaEValor(UserEntity user, BigDecimal valor, String categoria) {
        despesaService.criarDespesa(
                criarDespesaCreateDTO(valor, LocalDate.of(2025, 3, 12), categoria),
                user.getUuid()
        );
    }
}