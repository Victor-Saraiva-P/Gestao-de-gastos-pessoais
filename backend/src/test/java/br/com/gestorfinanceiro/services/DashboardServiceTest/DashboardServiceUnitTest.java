package br.com.gestorfinanceiro.services.DashboardServiceTest;

import br.com.gestorfinanceiro.exceptions.dashboard.DashboardOperationException;
import br.com.gestorfinanceiro.exceptions.user.InvalidUserIdException;
import br.com.gestorfinanceiro.exceptions.user.UserNotFoundException;
import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.repositories.DespesaRepository;
import br.com.gestorfinanceiro.repositories.ReceitaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceUnitTest {

    private static final String USER_ID = UUID.randomUUID().toString();
    private static final YearMonth PERIODO = YearMonth.of(2023, 1);
    private static final BigDecimal VALOR = BigDecimal.valueOf(1000);

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DespesaRepository despesaRepository;

    @Mock
    private ReceitaRepository receitaRepository;

    @Nested
    class ValidacoesBasicas {
        @Test
        void deveLancarInvalidUserIdExceptionQuandoUserIdNulo() {
            assertThrows(InvalidUserIdException.class, () ->
                    dashboardService.getSaldoTotal(null, PERIODO));
        }

        @Test
        void deveLancarInvalidUserIdExceptionQuandoUserIdVazio() {
            assertThrows(InvalidUserIdException.class, () ->
                    dashboardService.getSaldoTotal("", PERIODO));
        }

        @Test
        void deveLancarUserNotFoundExceptionQuandoUsuarioNaoExiste() {
            when(userRepository.existsById(USER_ID)).thenReturn(false);

            assertThrows(UserNotFoundException.class, () ->
                    dashboardService.getSaldoTotal(USER_ID, PERIODO));
        }

        @Test
        void deveLancarIllegalArgumentExceptionQuandoYearMonthNulo() {
            when(userRepository.existsById(USER_ID)).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () ->
                    dashboardService.getSaldoTotal(USER_ID, null));
        }
    }

    @Nested
    class TestesComRepositorioFalhando {
        @BeforeEach
        void setUp() {
            when(userRepository.existsById(USER_ID)).thenReturn(true);
        }

        @Test
        void deveLancarDashboardOperationExceptionQuandoFalhaAoCalcularSaldoTotal() {
            when(receitaRepository.sumReceitasByUserIdAndYearMonth(anyString(), anyInt(), anyInt()))
                    .thenThrow(new RuntimeException("Erro de conexão com o banco"));

            Exception exception = assertThrows(DashboardOperationException.class, () ->
                    dashboardService.getSaldoTotal(USER_ID, PERIODO));

            assertEquals("Erro ao calcular saldo total. Por favor, tente novamente.", exception.getMessage());
            assertNotNull(exception.getCause());
        }

        @Test
        void deveLancarDashboardOperationExceptionQuandoFalhaAoBuscarMaiorDespesa() {
            when(despesaRepository.findTopByUserIdAndYearMonthOrderByValorDesc(anyString(), anyInt(), anyInt()))
                    .thenThrow(new RuntimeException("Timeout na consulta"));

            Exception exception = assertThrows(DashboardOperationException.class, () ->
                    dashboardService.getMaiorDespesa(USER_ID, PERIODO));

            assertEquals("Erro ao buscar maior despesa. Por favor, tente novamente.", exception.getMessage());
        }

        @Test
        void deveLancarDashboardOperationExceptionQuandoFalhaAoBuscarMaiorReceita() {
            when(receitaRepository.findTopByUserIdAndYearMonthOrderByValorDesc(anyString(), anyInt(), anyInt()))
                    .thenThrow(new RuntimeException("Erro no banco de dados"));

            Exception exception = assertThrows(DashboardOperationException.class, () ->
                    dashboardService.getMaiorReceita(USER_ID, PERIODO));

            assertEquals("Erro ao buscar maior receita. Por favor, tente novamente.", exception.getMessage());
        }

        @Test
        void deveLancarDashboardOperationExceptionQuandoFalhaAoBuscarCategoriaMaiorDespesa() {
            when(despesaRepository.findCategoriaWithHighestDespesaByUserIdAndYearMonth(anyString(), anyInt(), anyInt()))
                    .thenThrow(new RuntimeException("Falha na consulta"));

            Exception exception = assertThrows(DashboardOperationException.class, () ->
                    dashboardService.getCategoriaComMaiorDespesa(USER_ID, PERIODO));

            assertEquals("Erro ao buscar categoria com maior despesa. Por favor, tente novamente.", exception.getMessage());
        }

        @Test
        void deveLancarDashboardOperationExceptionQuandoFalhaAoCalcularTotalDespesas() {
            when(despesaRepository.sumDespesasByUserIdAndYearMonth(anyString(), any(YearMonth.class)))
                    .thenThrow(new RuntimeException("Erro no repositório"));

            Exception exception = assertThrows(DashboardOperationException.class, () ->
                    dashboardService.calcularTotalDespesasNoMes(USER_ID, PERIODO));

            assertEquals("Erro ao calcular total de despesas do mês. Por favor, tente novamente.", exception.getMessage());
        }

        @Test
        void deveLancarDashboardOperationExceptionQuandoFalhaAoCalcularTotalReceitas() {
            when(receitaRepository.sumReceitasByUserIdAndYearMonth(anyString(), any(YearMonth.class)))
                    .thenThrow(new RuntimeException("Erro de persistência"));

            Exception exception = assertThrows(DashboardOperationException.class, () ->
                    dashboardService.calcularTotalReceitasNoMes(USER_ID, PERIODO));

            assertEquals("Erro ao calcular total de receitas do mês. Por favor, tente novamente.", exception.getMessage());
        }
    }

    @Nested
    class TestesComRepositorioFuncionando {
        private DespesaEntity despesa;
        private ReceitaEntity receita;

        @BeforeEach
        void setUp() {
            when(userRepository.existsById(USER_ID)).thenReturn(true);

            despesa = new DespesaEntity();
            despesa.setValor(VALOR);

            receita = new ReceitaEntity();
            receita.setValor(VALOR);
        }

        @Test
        void deveRetornarSaldoTotalCorretamente() {
            when(receitaRepository.sumReceitasByUserIdAndYearMonth(anyString(), anyInt(), anyInt()))
                    .thenReturn(VALOR);
            when(despesaRepository.sumDespesasByUserIdAndYearMonth(anyString(), anyInt(), anyInt()))
                    .thenReturn(VALOR);

            BigDecimal resultado = dashboardService.getSaldoTotal(USER_ID, PERIODO);

            assertEquals(BigDecimal.ZERO, resultado);
        }

        @Test
        void deveRetornarMaiorDespesaCorretamente() {
            when(despesaRepository.findTopByUserIdAndYearMonthOrderByValorDesc(anyString(), anyInt(), anyInt()))
                    .thenReturn(despesa);

            DespesaEntity resultado = dashboardService.getMaiorDespesa(USER_ID, PERIODO);

            assertEquals(VALOR, resultado.getValor());
        }

        @Test
        void deveRetornarMaiorReceitaCorretamente() {
            when(receitaRepository.findTopByUserIdAndYearMonthOrderByValorDesc(anyString(), anyInt(), anyInt()))
                    .thenReturn(receita);

            ReceitaEntity resultado = dashboardService.getMaiorReceita(USER_ID, PERIODO);

            assertEquals(VALOR, resultado.getValor());
        }

        @Test
        void deveRetornarCategoriaComMaiorDespesaCorretamente() {
            when(despesaRepository.findCategoriaWithHighestDespesaByUserIdAndYearMonth(anyString(), anyInt(), anyInt()))
                    .thenReturn(Map.of("Alimentação", VALOR));

            Map<String, BigDecimal> resultado = dashboardService.getCategoriaComMaiorDespesa(USER_ID, PERIODO);

            assertEquals(1, resultado.size());
            assertTrue(resultado.containsKey("Alimentação"));
            assertEquals(VALOR, resultado.get("Alimentação"));
        }

        @Test
        void deveRetornarTotalDespesasCorretamente() {
            when(despesaRepository.sumDespesasByUserIdAndYearMonth(anyString(), any(YearMonth.class)))
                    .thenReturn(VALOR);

            BigDecimal resultado = dashboardService.calcularTotalDespesasNoMes(USER_ID, PERIODO);

            assertEquals(VALOR, resultado);
        }

        @Test
        void deveRetornarTotalReceitasCorretamente() {
            when(receitaRepository.sumReceitasByUserIdAndYearMonth(anyString(), any(YearMonth.class)))
                    .thenReturn(VALOR);

            BigDecimal resultado = dashboardService.calcularTotalReceitasNoMes(USER_ID, PERIODO);

            assertEquals(VALOR, resultado);
        }
    }

    @Nested
    class TestesCategoriaMaiorReceita {
        @BeforeEach
        void setUp() {
            when(userRepository.existsById(USER_ID)).thenReturn(true);
        }

        @Test
        void deveLancarDashboardOperationExceptionQuandoFalhaAoBuscarCategoriaMaiorReceita() {
            // Arrange
            when(receitaRepository.findCategoriaWithHighestReceitaByUserIdAndYearMonth(
                    anyString(), anyInt(), anyInt()))
                    .thenThrow(new RuntimeException("Erro na consulta SQL"));

            // Act & Assert
            Exception exception = assertThrows(DashboardOperationException.class, () ->
                    dashboardService.getCategoriaComMaiorReceita(USER_ID, PERIODO));

            // Verifica a mensagem da exceção
            assertEquals("Erro ao buscar categoria com maior receita. Por favor, tente novamente.",
                    exception.getMessage());

            // Verifica se a causa original foi preservada
            assertNotNull(exception.getCause());
            assertEquals("Erro na consulta SQL", exception.getCause().getMessage());
        }

        @Test
        void deveRetornarMapaComCategoriaMaiorReceita() {
            // Arrange
            Map<String, BigDecimal> resultadoEsperado = Map.of("Salário", BigDecimal.valueOf(5000));
            when(receitaRepository.findCategoriaWithHighestReceitaByUserIdAndYearMonth(
                    anyString(), anyInt(), anyInt()))
                    .thenReturn(resultadoEsperado);

            // Act
            Map<String, BigDecimal> resultado = dashboardService.getCategoriaComMaiorReceita(USER_ID, PERIODO);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertTrue(resultado.containsKey("Salário"));
            assertEquals(BigDecimal.valueOf(5000), resultado.get("Salário"));
        }
    }
}