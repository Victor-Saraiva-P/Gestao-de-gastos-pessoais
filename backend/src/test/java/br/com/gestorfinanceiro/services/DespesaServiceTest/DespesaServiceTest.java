package br.com.gestorfinanceiro.services.DespesaServiceTest;

import br.com.gestorfinanceiro.dto.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.GraficoPizzaDTO;
import br.com.gestorfinanceiro.exceptions.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.InvalidUserIdException;
import br.com.gestorfinanceiro.exceptions.InvalidUuidException;
import br.com.gestorfinanceiro.exceptions.despesa.DespesaNotFoundException;
import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.DespesasCategorias;
import br.com.gestorfinanceiro.models.enums.Roles;
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
class DespesaServiceTest {

    @Autowired
    private DespesaService despesaService;

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        despesaRepository.deleteAll();
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
            UserEntity user = criarUsuarioTest();
            DespesaEntity despesa = criarDespesaTest();

            DespesaEntity despesaSalva = despesaService.criarDespesa(despesa, user.getUuid());

            assertNotNull(despesaSalva);
            assertEquals(despesa.getValor(), despesaSalva.getValor());
            assertEquals(despesa.getCategoria(), despesaSalva.getCategoria());
        }

        @Test
        void erroAoCriarDespesaNula() {
            UserEntity user = criarUsuarioTest();
            String userId = user.getUuid();
            assertThrows(InvalidDataException.class, () -> despesaService.criarDespesa(null, userId));
        }


        @Test
        void erroAoCriarDespesaComValorInvalido() {
            UserEntity user = criarUsuarioTest();
            DespesaEntity despesa = new DespesaEntity();
            despesa.setValor(BigDecimal.ZERO);
            String userId = user.getUuid();

            assertThrows(InvalidDataException.class, () -> despesaService.criarDespesa(despesa, userId));
        }

        @Test
        void erroAoCriarDespesaComValorNegativo() {
            UserEntity user = criarUsuarioTest();
            DespesaEntity despesa = new DespesaEntity();
            despesa.setValor(BigDecimal.valueOf(-100));
            String userId = user.getUuid();

            assertThrows(InvalidDataException.class, () -> despesaService.criarDespesa(despesa, userId));
        }

        @Test
        void erroAoCriarDespesaUsuarioNaoEncontrado() {
            DespesaEntity despesa = criarDespesaTest();
            String usuarioInexistente = UUID.randomUUID().toString();

            assertThrows(RuntimeException.class, () -> despesaService.criarDespesa(despesa, usuarioInexistente));
        }
    }

    @Nested
    class ListarDespesasTest {

        @Test
        void deveListarDespesasUsuario() {
            UserEntity user = criarUsuarioTest();
            despesaService.criarDespesa(criarDespesaTest(), user.getUuid());

            List<DespesaEntity> despesas = despesaService.listarDespesasUsuario(user.getUuid());
            assertFalse(despesas.isEmpty());
        }

        @Test
        void erroAoListarDespesasUsuarioComIdNuloOuVazio() {
            String uuid = null;
            assertThrows(InvalidUserIdException.class, () -> despesaService.listarDespesasUsuario(uuid));
            assertThrows(InvalidUserIdException.class, () -> despesaService.listarDespesasUsuario(""));
        }
    }

    @Nested
    class BuscarDespesaPorIdTest {

        @Test
        void deveBuscarDespesaPorId() {
            UserEntity user = criarUsuarioTest();
            DespesaEntity despesa = despesaService.criarDespesa(criarDespesaTest(), user.getUuid());

            DespesaEntity despesaEncontrada = despesaService.buscarDespesaPorId(despesa.getUuid());
            assertEquals(despesa.getUuid(), despesaEncontrada.getUuid());
        }

        @Test
        void erroAoBuscarDespesaPorIdInexistente() {
            String uuid = UUID.randomUUID().toString();
            assertThrows(DespesaNotFoundException.class, () -> despesaService.buscarDespesaPorId(uuid));
        }

        @Test
        void erroAoBuscarDespesaPorIdNuloOuVazio() {
            String uuid = null;

            assertThrows(InvalidUuidException.class, () -> despesaService.buscarDespesaPorId(uuid));
            assertThrows(InvalidUuidException.class, () -> despesaService.buscarDespesaPorId(""));
        }
    }

    @Nested
    class AtualizarDespesaTest {

        @Test
        void deveAtualizarDespesa() {
            UserEntity user = criarUsuarioTest();
            DespesaEntity despesa = despesaService.criarDespesa(criarDespesaTest(), user.getUuid());

            despesa.setValor(BigDecimal.valueOf(200));
            DespesaEntity despesaAtualizada = despesaService.atualizarDespesa(despesa.getUuid(), despesa);

            assertEquals(BigDecimal.valueOf(200), despesaAtualizada.getValor());
        }

        @Test
        void erroAoAtualizarDespesaInexistente() {
            DespesaEntity despesa = criarDespesaTest();
            String uuidInexistente = UUID.randomUUID().toString();

            assertThrows(DespesaNotFoundException.class, () -> despesaService.atualizarDespesa(uuidInexistente, despesa));
        }

        @Test
        void erroAoAtualizarDespesaNula() {
            DespesaEntity despesa = null;
            String uuid = UUID.randomUUID().toString();

            assertThrows(InvalidDataException.class, () -> despesaService.atualizarDespesa(uuid, despesa));
        }

        @Test
        void erroAoAtualizarUuidNuloOuVazio() {
            DespesaEntity despesa = criarDespesaTest();
            String uuid = null;

            assertThrows(InvalidUuidException.class, () -> despesaService.atualizarDespesa(uuid, despesa));
            assertThrows(InvalidUuidException.class, () -> despesaService.atualizarDespesa("", despesa));
        }
    }

    @Nested
    class ExcluirDespesaTest {

        @Test
        void deveExcluirDespesa() {
            UserEntity user = criarUsuarioTest();
            DespesaEntity despesa = despesaService.criarDespesa(criarDespesaTest(), user.getUuid());

            despesaService.excluirDespesa(despesa.getUuid());
            assertFalse(despesaRepository.findById(despesa.getUuid()).isPresent());
        }

        @Test
        void erroAoExcluirDespesaInexistente() {
            String uuidInexistente = UUID.randomUUID().toString();

            assertThrows(DespesaNotFoundException.class, () -> despesaService.excluirDespesa(uuidInexistente));
        }

        @Test
        void erroAoExcluirDespesaComUuidNuloOuVazio() {
            String uuidNull = null;

            assertThrows(InvalidUuidException.class, () -> despesaService.excluirDespesa(uuidNull));
            assertThrows(InvalidUuidException.class, () -> despesaService.excluirDespesa(""));
        }
    }

    @Nested
    class GerarGraficosTest {

        @Test
        void deveGerarGraficoBarras() {
            UserEntity user = criarUsuarioTest();

            // Criando despesas em meses diferentes
            criarDespesaComValorEData(user, BigDecimal.valueOf(100), LocalDate.of(2024, 1, 10));
            criarDespesaComValorEData(user, BigDecimal.valueOf(200), LocalDate.of(2024, 2, 15));
            criarDespesaComValorEData(user, BigDecimal.valueOf(150), LocalDate.of(2024, 2, 20));

            YearMonth inicio = YearMonth.of(2024, 1);
            YearMonth fim = YearMonth.of(2024, 2);

            GraficoBarraDTO grafico = despesaService.gerarGraficoBarras(user.getUuid(), inicio, fim);

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
            UserEntity user = criarUsuarioTest();

            // Criando despesas de diferentes categorias
            criarDespesaComCategoriaEValor(user, BigDecimal.valueOf(300), DespesasCategorias.ALIMENTACAO);
            criarDespesaComCategoriaEValor(user, BigDecimal.valueOf(200), DespesasCategorias.TRANSPORTE);
            criarDespesaComCategoriaEValor(user, BigDecimal.valueOf(100), DespesasCategorias.LAZER);

            LocalDate inicio = LocalDate.of(2024, 1, 1);
            LocalDate fim = LocalDate.of(2024, 12, 31);

            GraficoPizzaDTO grafico = despesaService.gerarGraficoPizza(user.getUuid(), inicio, fim);
            System.out.println(grafico);
            // Verifica se os valores por categoria estão corretos
            assertNotNull(grafico);
            assertEquals(3, grafico.categorias().size());
            assertEquals(BigDecimal.valueOf(300).stripTrailingZeros(), grafico.categorias().get("ALIMENTACAO").stripTrailingZeros());
            assertEquals(BigDecimal.valueOf(200).stripTrailingZeros(), grafico.categorias().get("TRANSPORTE").stripTrailingZeros());
            assertEquals(BigDecimal.valueOf(100).stripTrailingZeros(), grafico.categorias().get("LAZER").stripTrailingZeros());


        }
    }

    @Nested
    class BuscaAvancadaTest {
        @Test
        void deveBuscarDespesasPorIntervaloDeDatas() {
            UserEntity user = criarUsuarioTest();
            String userId = user.getUuid();

            // Criando despesas em datas diferentes
            criarDespesaComValorEData(user, BigDecimal.valueOf(100), LocalDate.of(2024, 1, 10));
            criarDespesaComValorEData(user, BigDecimal.valueOf(200), LocalDate.of(2024, 2, 15));
            criarDespesaComValorEData(user, BigDecimal.valueOf(150), LocalDate.of(2024, 2, 20));

            LocalDate inicio = LocalDate.of(2024, 1, 1);
            LocalDate fim = LocalDate.of(2024, 2, 18);

            List<DespesaEntity> despesas = despesaService.buscarDespesasPorIntervaloDeDatas(userId, inicio, fim);

            // Verifica se as despesas estão corretas
            assertNotNull(despesas);
            assertEquals(2, despesas.size());
            assertEquals(BigDecimal.valueOf(100).stripTrailingZeros(), despesas.get(0).getValor().stripTrailingZeros());
            assertEquals(BigDecimal.valueOf(200).stripTrailingZeros(), despesas.get(1).getValor().stripTrailingZeros());
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeDatasComUserIdNuloOuVazio() {
            LocalDate inicio = LocalDate.of(2024, 1, 1);
            LocalDate fim = LocalDate.of(2024, 2, 18);

            assertThrows(InvalidUserIdException.class, () -> despesaService.buscarDespesasPorIntervaloDeDatas(null, inicio, fim));
            assertThrows(InvalidUserIdException.class, () -> despesaService.buscarDespesasPorIntervaloDeDatas("", inicio, fim));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeDatasComDatasNulas() {
            UserEntity user = criarUsuarioTest();
            String userId = user.getUuid();
            LocalDate inicio = null;
            LocalDate inicio2 = LocalDate.of(2024, 1, 1);
            LocalDate fim = null;
            LocalDate fim2 = LocalDate.of(2024, 2, 18);

            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeDatas(userId, inicio, fim2));
            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeDatas(userId, inicio2, fim));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeDatasComInicioDepoisDoFim() {
            UserEntity user = criarUsuarioTest();
            String userId = user.getUuid();
            LocalDate inicio = LocalDate.of(2024, 1, 10);
            LocalDate fim = LocalDate.of(2024, 1, 5);

            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeDatas(userId, inicio, fim));
        }

        @Test
        void deveBuscarDespesasPorIntervaloDeValores() {
            UserEntity user = criarUsuarioTest();
            String userId = user.getUuid();

            // Criando despesas com valores diferentes
            criarDespesaComValorEData(user, BigDecimal.valueOf(100), LocalDate.of(2024, 1, 10));
            criarDespesaComValorEData(user, BigDecimal.valueOf(200), LocalDate.of(2024, 2, 15));
            criarDespesaComValorEData(user, BigDecimal.valueOf(150), LocalDate.of(2024, 2, 20));

            BigDecimal min = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.valueOf(150);

            List<DespesaEntity> despesas = despesaService.buscarDespesasPorIntervaloDeValores(userId, min, max);

            // Verifica se as despesas estão corretas
            assertNotNull(despesas);
            assertEquals(2, despesas.size());
            assertEquals(BigDecimal.valueOf(100).stripTrailingZeros(), despesas.get(0).getValor().stripTrailingZeros());
            assertEquals(BigDecimal.valueOf(150).stripTrailingZeros(), despesas.get(1).getValor().stripTrailingZeros());
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeValoresComUserIdNuloOuVazio() {
            BigDecimal min = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.valueOf(150);

            assertThrows(InvalidUserIdException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(null, min, max));
            assertThrows(InvalidUserIdException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores("", min, max));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeValoresComValoresNulos() {
            UserEntity user = criarUsuarioTest();
            String userId = user.getUuid();
            BigDecimal min = null;
            BigDecimal min2 = BigDecimal.valueOf(100);
            BigDecimal max = null;
            BigDecimal max2 = BigDecimal.valueOf(150);

            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(userId, min, max2));
            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(userId, min2, max));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeValoresComValoresMenoresOuIguaisAZero() {
            UserEntity user = criarUsuarioTest();
            String userId = user.getUuid();
            BigDecimal min = BigDecimal.ZERO;
            BigDecimal min2 = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.ZERO;
            BigDecimal max2 = BigDecimal.valueOf(150);

            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(userId, min, max2));
            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(userId, min2, max));        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeValoresComMinMaiorQueMax() {
            UserEntity user = criarUsuarioTest();
            String userId = user.getUuid();
            BigDecimal min = BigDecimal.valueOf(200);
            BigDecimal max = BigDecimal.valueOf(100);

            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(userId, min, max));
        }
    }



    //----------------- Métodos Auxiliares -----------------//

    private UserEntity criarUsuarioTest() {
        UserEntity user = new UserEntity();
        user.setUsername("Jorge");
        user.setEmail("jorge@gmail.com");
        user.setPassword("123456");
        user.setRole(Roles.USER);
        return userRepository.save(user);
    }

    private DespesaEntity criarDespesaTest() {
        DespesaEntity despesa = new DespesaEntity();
        despesa.setData(LocalDate.now());
        despesa.setValor(BigDecimal.valueOf(100));
        despesa.setCategoria(DespesasCategorias.ALIMENTACAO);
        despesa.setDestinoPagamento("Mercado");
        despesa.setObservacoes("Compras do mês");
        return despesa;
    }

    private void criarDespesaComValorEData(UserEntity user, BigDecimal valor, LocalDate data) {
        DespesaEntity despesa = new DespesaEntity();
        despesa.setData(data);
        despesa.setValor(valor);
        despesa.setCategoria(DespesasCategorias.ALIMENTACAO);
        despesa.setDestinoPagamento("Loja X");
        despesa.setObservacoes("Compra teste");
        despesa.setUser(user);
        despesaService.criarDespesa(despesa, user.getUuid());
    }

    private void criarDespesaComCategoriaEValor(UserEntity user, BigDecimal valor, DespesasCategorias categoria) {
        DespesaEntity despesa = new DespesaEntity();
        despesa.setData(LocalDate.of(2024, 1, 15));
        despesa.setValor(valor);
        despesa.setCategoria(categoria);
        despesa.setDestinoPagamento("Teste");
        despesa.setObservacoes("Compra teste");
        despesa.setUser(user);
        despesaService.criarDespesa(despesa, user.getUuid());
    }

}
