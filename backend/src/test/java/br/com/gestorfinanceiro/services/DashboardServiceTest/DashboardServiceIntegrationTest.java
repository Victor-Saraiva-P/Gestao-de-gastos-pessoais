package br.com.gestorfinanceiro.services.DashboardServiceTest;

import br.com.gestorfinanceiro.exceptions.user.InvalidUserIdException;
import br.com.gestorfinanceiro.exceptions.user.UserNotFoundException;
import br.com.gestorfinanceiro.models.*;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.models.enums.Roles;
import br.com.gestorfinanceiro.repositories.CategoriaRepository;
import br.com.gestorfinanceiro.repositories.DespesaRepository;
import br.com.gestorfinanceiro.repositories.ReceitaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.DashboardService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DashboardServiceIntegrationTest {

    private static final YearMonth PERIODO_PADRAO = YearMonth.of(2023, 1);
    private static final BigDecimal VALOR_PADRAO = BigDecimal.valueOf(1000);
    private static final BigDecimal VALOR_ALTO = BigDecimal.valueOf(2000);
    private static final String CATEGORIA_DESPESA_PADRAO = "Alimentação";
    private static final String CATEGORIA_RECEITA_PADRAO = "Salario";
    private static final String DESTINO_DESPESA_PADRAO = "Mercado";
    private static final String ORIGEM_RECEITA_PADRAO = "Empresa X";
    private static final String OBSERVACAO_DESPESA_PADRAO = "Compras do mês";
    private static final String OBSERVACAO_RECEITA_PADRAO = "Salário do mês";


    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private ReceitaRepository receitaRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    private String userId;

    @BeforeEach
    void setUp() {
        limparBancoDeDados();
        UserEntity user = criarUsuarioTest();
        userId = user.getUuid();
        criarDadosTeste(user);
    }

    @AfterEach
    void tearDown() {
        despesaRepository.deleteAllInBatch();
        receitaRepository.deleteAllInBatch();
        categoriaRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    private void limparBancoDeDados() {
        despesaRepository.deleteAll();
        receitaRepository.deleteAll();
        categoriaRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    class Validacoes {
        @Test
        void deveLancarExcecaoQuandoUserIdInvalido() {
            assertThrows(InvalidUserIdException.class, () -> {
                dashboardService.getSaldoTotal(null, PERIODO_PADRAO);
            });

            assertThrows(InvalidUserIdException.class, () -> {
                dashboardService.getSaldoTotal("", PERIODO_PADRAO);
            });
        }

        @Test
        void deveLancarExcecaoQuandoUsuarioNaoExiste() {
            assertThrows(UserNotFoundException.class, () -> {
                dashboardService.getSaldoTotal("uuid-inexistente", PERIODO_PADRAO);
            });
        }

        @Test
        void deveLancarExcecaoQuandoYearMonthNulo() {
            assertThrows(IllegalArgumentException.class, () -> {
                dashboardService.getSaldoTotal(userId, null);
            });
        }
    }

    @Nested
    class SaldoTotal {
        @Test
        void deveCalcularSaldoTotalCorretamente() {
            // Act
            BigDecimal saldo = dashboardService.getSaldoTotal(userId, PERIODO_PADRAO);

            // Assert
            BigDecimal expected = VALOR_PADRAO.add(VALOR_ALTO).subtract(VALOR_PADRAO).subtract(VALOR_ALTO);
            assertEquals(0, saldo.compareTo(BigDecimal.ZERO), "Saldo deveria ser zero");
        }

        @Test
        void deveRetornarSaldoPositivoQuandoReceitasMaiores() {
            // Arrange - Adiciona mais uma receita
            ReceitaEntity receitaExtra = new ReceitaEntity();
            receitaExtra.setUser(userRepository.findById(userId).get());
            receitaExtra.setValor(BigDecimal.valueOf(500));
            receitaExtra.setData(PERIODO_PADRAO.atDay(10));
            receitaExtra.setOrigemDoPagamento(ORIGEM_RECEITA_PADRAO);
            receitaExtra.setObservacoes("Bônus do mês");
            receitaExtra.setCategoria(categoriaRepository.findByNome(CATEGORIA_RECEITA_PADRAO).get());
            receitaRepository.save(receitaExtra);

            // Act
            BigDecimal saldo = dashboardService.getSaldoTotal(userId, PERIODO_PADRAO);

            // Assert
            assertEquals(0, saldo.compareTo(BigDecimal.valueOf(500)));
        }
    }

    @Nested
    class MaiorDespesa {
        @Test
        void deveRetornarMaiorDespesa() {
            // Act
            DespesaEntity maiorDespesa = dashboardService.getMaiorDespesa(userId, PERIODO_PADRAO);

            // Assert
            assertNotNull(maiorDespesa);
            assertEquals(0, VALOR_ALTO.compareTo(maiorDespesa.getValor()));
        }

        @Test
        void deveRetornarNuloQuandoNaoHaDespesas() {
            // Arrange
            despesaRepository.deleteAll();

            // Act
            DespesaEntity maiorDespesa = dashboardService.getMaiorDespesa(userId, PERIODO_PADRAO);

            // Assert
            assertNull(maiorDespesa);
        }
    }

    @Nested
    class MaiorReceita {
        @Test
        void deveRetornarMaiorReceita() {
            // Act
            ReceitaEntity maiorReceita = dashboardService.getMaiorReceita(userId, PERIODO_PADRAO);

            // Assert
            assertNotNull(maiorReceita);
            assertEquals(0, VALOR_ALTO.compareTo(maiorReceita.getValor()));
        }

        @Test
        void deveRetornarNuloQuandoNaoHaReceitas() {
            // Arrange
            receitaRepository.deleteAll();

            // Act
            ReceitaEntity maiorReceita = dashboardService.getMaiorReceita(userId, PERIODO_PADRAO);

            // Assert
            assertNull(maiorReceita);
        }
    }

    @Nested
    class CategoriaMaiorDespesa {
        @Test
        void deveRetornarCategoriaComMaiorDespesa() {
            // Act
            Map<String, BigDecimal> resultado = dashboardService.getCategoriaComMaiorDespesa(userId, PERIODO_PADRAO);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.containsKey(CATEGORIA_DESPESA_PADRAO));
            BigDecimal totalEsperado = VALOR_PADRAO.add(VALOR_ALTO);
            assertEquals(0, totalEsperado.compareTo(resultado.get(CATEGORIA_DESPESA_PADRAO)));
        }
    }

    @Nested
    class CategoriaMaiorReceita {
        @Test
        void deveRetornarCategoriaComMaiorReceita() {
            // Act
            Map<String, BigDecimal> resultado = dashboardService.getCategoriaComMaiorReceita(userId, PERIODO_PADRAO);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.containsKey(CATEGORIA_RECEITA_PADRAO));
            BigDecimal totalEsperado = VALOR_PADRAO.add(VALOR_ALTO);
            assertEquals(0, totalEsperado.compareTo(resultado.get(CATEGORIA_RECEITA_PADRAO)));
        }
    }

    @Nested
    class TotalDespesasNoMes {
        @Test
        void deveCalcularTotalDespesasCorretamente() {
            // Act
            BigDecimal total = dashboardService.calcularTotalDespesasNoMes(userId, PERIODO_PADRAO);

            // Assert
            BigDecimal expected = VALOR_PADRAO.add(VALOR_ALTO);
            assertEquals(0, expected.compareTo(total));
        }

        @Test
        void deveRetornarZeroQuandoNaoHaDespesas() {
            // Arrange
            despesaRepository.deleteAll();

            // Act
            BigDecimal total = dashboardService.calcularTotalDespesasNoMes(userId, PERIODO_PADRAO);

            // Assert
            assertEquals(0, BigDecimal.ZERO.compareTo(total));
        }
    }

    @Nested
    class TotalReceitasNoMes {
        @Test
        void deveCalcularTotalReceitasCorretamente() {
            // Act
            BigDecimal total = dashboardService.calcularTotalReceitasNoMes(userId, PERIODO_PADRAO);

            // Assert
            BigDecimal expected = VALOR_PADRAO.add(VALOR_ALTO);
            assertEquals(0, expected.compareTo(total));
        }

        @Test
        void deveRetornarZeroQuandoNaoHaReceitas() {
            // Arrange
            receitaRepository.deleteAll();

            // Act
            BigDecimal total = dashboardService.calcularTotalReceitasNoMes(userId, PERIODO_PADRAO);

            // Assert
            assertEquals(0, BigDecimal.ZERO.compareTo(total));
        }
    }

    // Métodos auxiliares

    private UserEntity criarUsuarioTest() {
        UserEntity user = new UserEntity();
        user.setUsername("Usuário Teste");
        user.setEmail("teste@example.com");
        user.setPassword("senha123");
        user.setRole(Roles.ADMIN);
        return userRepository.save(user);
    }

    private void criarDadosTeste(UserEntity user) {
        CategoriaEntity categoria1 = new CategoriaEntity();
        categoria1.setNome(CATEGORIA_DESPESA_PADRAO);
        categoria1.setTipo(CategoriaType.DESPESAS);
        categoria1.setUser(user);
        categoriaRepository.save(categoria1);

        CategoriaEntity categoria2 = new CategoriaEntity();
        categoria2.setNome(CATEGORIA_RECEITA_PADRAO);
        categoria2.setTipo(CategoriaType.RECEITAS);
        categoria2.setUser(user);
        categoriaRepository.save(categoria2);

        // Criar despesas
        DespesaEntity despesa1 = new DespesaEntity();
        despesa1.setUser(user);
        despesa1.setValor(VALOR_PADRAO);
        despesa1.setData(PERIODO_PADRAO.atDay(1));
        despesa1.setDestinoPagamento(DESTINO_DESPESA_PADRAO);
        despesa1.setObservacoes(OBSERVACAO_DESPESA_PADRAO);
        despesa1.setCategoria(categoria1);

        DespesaEntity despesa2 = new DespesaEntity();
        despesa2.setUser(user);
        despesa2.setValor(VALOR_ALTO);
        despesa2.setData(PERIODO_PADRAO.atDay(15));
        despesa2.setDestinoPagamento(DESTINO_DESPESA_PADRAO);
        despesa2.setObservacoes(OBSERVACAO_DESPESA_PADRAO);
        despesa2.setCategoria(categoria1);

        despesaRepository.save(despesa1);
        despesaRepository.save(despesa2);

        // Criar receitas
        ReceitaEntity receita1 = new ReceitaEntity();
        receita1.setUser(user);
        receita1.setValor(VALOR_PADRAO);
        receita1.setData(PERIODO_PADRAO.atDay(5));
        receita1.setOrigemDoPagamento(ORIGEM_RECEITA_PADRAO);
        receita1.setObservacoes(OBSERVACAO_RECEITA_PADRAO);
        receita1.setCategoria(categoria2);

        ReceitaEntity receita2 = new ReceitaEntity();
        receita2.setUser(user);
        receita2.setValor(VALOR_ALTO);
        receita2.setData(PERIODO_PADRAO.atDay(20));
        receita2.setOrigemDoPagamento(ORIGEM_RECEITA_PADRAO);
        receita2.setObservacoes(OBSERVACAO_RECEITA_PADRAO);
        receita2.setCategoria(categoria2);

        receitaRepository.save(receita1);
        receitaRepository.save(receita2);
    }
}