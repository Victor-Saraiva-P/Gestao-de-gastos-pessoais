package br.com.gestorfinanceiro.services.DespesaServiceTest;

import br.com.gestorfinanceiro.exceptions.InvalidDataException;
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
        userRepository.deleteAll();
        despesaRepository.deleteAll();
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
        }

        @Test
        void erroAoCriarDespesaNula() {
            UserEntity user = criarUsuarioTest();
            assertThrows(InvalidDataException.class, () -> despesaService.criarDespesa(null, user.getUuid()));
        }

        @Test
        void erroAoCriarDespesaComValorInvalido() {
            UserEntity user = criarUsuarioTest();
            DespesaEntity despesa = new DespesaEntity();
            despesa.setValor(BigDecimal.ZERO);

            assertThrows(InvalidDataException.class, () -> despesaService.criarDespesa(despesa, user.getUuid()));
        }

        @Test
        void erroAoCriarDespesaComValorNegativo() {
            UserEntity user = criarUsuarioTest();
            DespesaEntity despesa = new DespesaEntity();
            despesa.setValor(BigDecimal.valueOf(-100));

            assertThrows(InvalidDataException.class, () -> despesaService.criarDespesa(despesa, user.getUuid()));
        }

        @Test
        void erroAoCriarDespesaUsuarioNaoEncontrado() {
            DespesaEntity despesa = new DespesaEntity();
            despesa.setValor(BigDecimal.valueOf(50));

            assertThrows(RuntimeException.class, () -> despesaService.criarDespesa(despesa, UUID.randomUUID().toString()));
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

//        @Test
//        void erroAoListarDespesasUsuarioComIdNulo() {
//            assertThrows(InvalidDataException.class, () -> despesaService.listarDespesasUsuario(null));
//        }
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
            assertThrows(DespesaNotFoundException.class, () -> despesaService.buscarDespesaPorId(UUID.randomUUID().toString()));
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

//        @Test
//        void erroAoAtualizarDespesaInexistente() {
//            DespesaEntity despesa = criarDespesaTest();
//            assertThrows(DespesaNotFoundException.class, () -> despesaService.atualizarDespesa(UUID.randomUUID().toString(), despesa));
//        }
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

//        @Test
//        void erroAoExcluirDespesaInexistente() {
//            assertThrows(DespesaNotFoundException.class, () -> despesaService.excluirDespesa(UUID.randomUUID().toString()));
//        }
    }

    @Nested
    class GerarGraficosTest {

        @Test
        void deveGerarGraficoBarras() {
            UserEntity user = criarUsuarioTest();
            despesaService.criarDespesa(criarDespesaTest(), user.getUuid());

            YearMonth mesAnterior = YearMonth.from(LocalDate.now().minusMonths(1));
            YearMonth mesAtual = YearMonth.from(LocalDate.now());

            assertNotNull(despesaService.gerarGraficoBarras(user.getUuid(), mesAnterior, mesAtual));
        }

        @Test
        void deveGerarGraficoPizza() {
            UserEntity user = criarUsuarioTest();
            despesaService.criarDespesa(criarDespesaTest(), user.getUuid());

            assertNotNull(despesaService.gerarGraficoPizza(user.getUuid(),
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()));
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
}