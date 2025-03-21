package br.com.gestorfinanceiro.services.DespesaServiceTest;

import br.com.gestorfinanceiro.dto.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.GraficoPizzaDTO;
import br.com.gestorfinanceiro.exceptions.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.InvalidUserIdException;
import br.com.gestorfinanceiro.exceptions.InvalidUuidException;
import br.com.gestorfinanceiro.exceptions.despesa.DespesaNotFoundException;
import br.com.gestorfinanceiro.exceptions.despesa.DespesaOperationException;
import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.DespesasCategorias;
import br.com.gestorfinanceiro.models.enums.Roles;
import br.com.gestorfinanceiro.repositories.DespesaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.impl.DespesaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DespesaServiceTest {

    @Mock
    private DespesaRepository despesaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DespesaServiceImpl despesaService;

    private UserEntity user;
    private DespesaEntity despesa;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setUuid(UUID.randomUUID().toString());
        user.setUsername("Jorge");
        user.setEmail("jorge@gmail.com");
        user.setPassword("123456");
        user.setRole(Roles.USER);

        despesa = new DespesaEntity();
        despesa.setUuid(UUID.randomUUID().toString());
        despesa.setData(LocalDate.now());
        despesa.setValor(BigDecimal.valueOf(100));
        despesa.setCategoria(DespesasCategorias.ALIMENTACAO);
        despesa.setDestinoPagamento("Mercado");
        despesa.setObservacoes("Compras do mês");
        despesa.setUser(user);
    }

    @Test
    void deveCarregarDespesaService() {
        assertNotNull(despesaService, "O DespesaService não deveria ser nulo!");
    }

    @Nested
    class CriarDespesaTest {

        @Test
        void deveCriarDespesa() {
            when(userRepository.findById(user.getUuid())).thenReturn(Optional.of(user));
            when(despesaRepository.save(any(DespesaEntity.class))).thenReturn(despesa);

            DespesaEntity despesaSalva = despesaService.criarDespesa(despesa, user.getUuid());

            assertNotNull(despesaSalva);
            assertEquals(despesa.getValor(), despesaSalva.getValor());
            assertEquals(despesa.getCategoria(), despesaSalva.getCategoria());
        }

        @Test
        void erroAoCriarDespesaNula() {
            assertThrows(InvalidDataException.class, () -> despesaService.criarDespesa(null, user.getUuid()));
        }

        @Test
        void erroAoCriarDespesaComValorInvalido() {
            despesa.setValor(BigDecimal.ZERO);
            assertThrows(InvalidDataException.class, () -> despesaService.criarDespesa(despesa, user.getUuid()));
        }

        @Test
        void erroAoCriarDespesaComValorNulo() {
            despesa.setValor(null);
            assertThrows(InvalidDataException.class, () -> despesaService.criarDespesa(despesa, user.getUuid()));
        }

        @Test
        void erroAoCriarDespesaComValorNegativo() {
            despesa.setValor(BigDecimal.valueOf(-100));
            assertThrows(InvalidDataException.class, () -> despesaService.criarDespesa(despesa, user.getUuid()));
        }

        @Test
        void erroAoCriarDespesaUsuarioNaoEncontrado() {
            when(userRepository.findById(anyString())).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> despesaService.criarDespesa(despesa, UUID.randomUUID().toString()));
        }

        @Test
        void criarDespesa_DeveLancarDespesaOperationException_QuandoRepositorioFalhar() {
            // Arrange
            DespesaEntity despesa = new DespesaEntity();
            despesa.setValor(BigDecimal.valueOf(100));
            despesa.setData(LocalDate.now());
            despesa.setCategoria(DespesasCategorias.ALIMENTACAO);

            UserEntity user = new UserEntity();
            user.setUuid(UUID.randomUUID().toString());

            when(userRepository.findById(user.getUuid())).thenReturn(Optional.of(user));
            when(despesaRepository.save(any(DespesaEntity.class))).thenThrow(new RuntimeException("Erro no repositório"));

            // Act & Assert
            assertThrows(DespesaOperationException.class, () -> despesaService.criarDespesa(despesa, user.getUuid()));
        }
    }

    @Nested
    class ListarDespesasTest {

        @Test
        void deveListarDespesasUsuario() {
            when(despesaRepository.findAllByUserUuid(user.getUuid())).thenReturn(List.of(despesa));

            List<DespesaEntity> despesas = despesaService.listarDespesasUsuario(user.getUuid());
            assertFalse(despesas.isEmpty());
        }

        @Test
        void erroAoListarDespesasUsuarioComIdNuloOuVazio() {
            assertThrows(InvalidUserIdException.class, () -> despesaService.listarDespesasUsuario(null));
            assertThrows(InvalidUserIdException.class, () -> despesaService.listarDespesasUsuario(""));
        }

        @Test
        void erroAoListarDespesasVazia() {
            when(despesaRepository.findAllByUserUuid(anyString())).thenReturn(List.of());

            assertThrows(DespesaNotFoundException.class, () -> despesaService.listarDespesasUsuario(UUID.randomUUID().toString()));
        }
    }

    @Nested
    class BuscarDespesaPorIdTest {

        @Test
        void deveBuscarDespesaPorId() {
            when(despesaRepository.findById(despesa.getUuid())).thenReturn(Optional.of(despesa));

            DespesaEntity despesaEncontrada = despesaService.buscarDespesaPorId(despesa.getUuid());
            assertEquals(despesa.getUuid(), despesaEncontrada.getUuid());
        }

        @Test
        void erroAoBuscarDespesaPorIdInexistente() {
            when(despesaRepository.findById(anyString())).thenReturn(Optional.empty());

            assertThrows(DespesaNotFoundException.class, () -> despesaService.buscarDespesaPorId(UUID.randomUUID().toString()));
        }

        @Test
        void erroAoBuscarDespesaPorIdNuloOuVazio() {
            assertThrows(InvalidUuidException.class, () -> despesaService.buscarDespesaPorId(null));
            assertThrows(InvalidUuidException.class, () -> despesaService.buscarDespesaPorId(""));
        }
    }

    @Nested
    class AtualizarDespesaTest {

        @Test
        void deveAtualizarDespesa() {
            when(despesaRepository.findById(despesa.getUuid())).thenReturn(Optional.of(despesa));
            when(despesaRepository.save(any(DespesaEntity.class))).thenReturn(despesa);

            despesa.setValor(BigDecimal.valueOf(200));
            DespesaEntity despesaAtualizada = despesaService.atualizarDespesa(despesa.getUuid(), despesa);

            assertEquals(BigDecimal.valueOf(200), despesaAtualizada.getValor());
        }

        @Test
        void erroAoAtualizarDespesaInexistente() {
            when(despesaRepository.findById(anyString())).thenReturn(Optional.empty());

            assertThrows(DespesaNotFoundException.class, () -> despesaService.atualizarDespesa(UUID.randomUUID().toString(), despesa));
        }

        @Test
        void erroAoAtualizarDespesaNula() {
            assertThrows(InvalidDataException.class, () -> despesaService.atualizarDespesa(UUID.randomUUID().toString(), null));
        }

        @Test
        void erroAoAtualizarUuidNuloOuVazio() {
            assertThrows(InvalidUuidException.class, () -> despesaService.atualizarDespesa(null, despesa));
            assertThrows(InvalidUuidException.class, () -> despesaService.atualizarDespesa("", despesa));
        }

        @Test
        void atualizarDespesa_DeveLancarDespesaOperationException_QuandoRepositorioFalhar() {
            // Arrange
            DespesaEntity despesa = new DespesaEntity();
            despesa.setUuid(UUID.randomUUID().toString());
            despesa.setValor(BigDecimal.valueOf(100));
            despesa.setData(LocalDate.now());
            despesa.setCategoria(DespesasCategorias.ALIMENTACAO);

            DespesaEntity despesaAtualizada = new DespesaEntity();
            despesaAtualizada.setValor(BigDecimal.valueOf(200));

            when(despesaRepository.findById(despesa.getUuid())).thenReturn(Optional.of(despesa));
            when(despesaRepository.save(any(DespesaEntity.class))).thenThrow(new RuntimeException("Erro no repositório"));

            // Act & Assert
            assertThrows(DespesaOperationException.class, () -> despesaService.atualizarDespesa(despesa.getUuid(), despesaAtualizada));
        }
    }

    @Nested
    class ExcluirDespesaTest {

        @Test
        void deveExcluirDespesa() {
            when(despesaRepository.findById(despesa.getUuid())).thenReturn(Optional.of(despesa));
            doNothing().when(despesaRepository).delete(despesa); // Verifica o método delete

            despesaService.excluirDespesa(despesa.getUuid());
            verify(despesaRepository, times(1)).delete(despesa); // Verifica o método delete
        }

        @Test
        void erroAoExcluirDespesaInexistente() {
            when(despesaRepository.findById(anyString())).thenReturn(Optional.empty());

            assertThrows(DespesaNotFoundException.class, () -> despesaService.excluirDespesa(UUID.randomUUID().toString()));
        }

        @Test
        void erroAoExcluirDespesaComUuidNuloOuVazio() {
            assertThrows(InvalidUuidException.class, () -> despesaService.excluirDespesa(null));
            assertThrows(InvalidUuidException.class, () -> despesaService.excluirDespesa(""));
        }

        @Test
        void excluirDespesa_DeveLancarDespesaOperationException_QuandoRepositorioFalhar() {
            // Arrange
            DespesaEntity despesa = new DespesaEntity();
            despesa.setUuid(UUID.randomUUID().toString());

            when(despesaRepository.findById(despesa.getUuid())).thenReturn(Optional.of(despesa));
            doThrow(new RuntimeException("Erro no repositório")).when(despesaRepository).delete(despesa);

            // Act & Assert
            assertThrows(DespesaOperationException.class, () -> despesaService.excluirDespesa(despesa.getUuid()));
        }
    }

    @Nested
    class GerarGraficosTest {

        @Test
        void deveGerarGraficoBarras() {
            when(despesaRepository.findByUserAndYearMonthRange(anyString(), any(YearMonth.class), any(YearMonth.class)))
                    .thenReturn(List.of(despesa));

            YearMonth inicio = YearMonth.of(2025, 1);
            YearMonth fim = YearMonth.of(2025, 3);

            GraficoBarraDTO grafico = despesaService.gerarGraficoBarras(user.getUuid(), inicio, fim);

            assertNotNull(grafico);
            assertEquals(1, grafico.dadosMensais().size());
            assertEquals(BigDecimal.valueOf(100), grafico.dadosMensais().get("março 2025"));
        }

        @Test
        void deveGerarGraficoPizza() {
            when(despesaRepository.findByUserAndDateRange(anyString(), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of(despesa));

            LocalDate inicio = LocalDate.of(2024, 1, 1);
            LocalDate fim = LocalDate.of(2024, 12, 31);

            GraficoPizzaDTO grafico = despesaService.gerarGraficoPizza(user.getUuid(), inicio, fim);

            assertNotNull(grafico);
            assertEquals(1, grafico.categorias().size());
            assertEquals(BigDecimal.valueOf(100).stripTrailingZeros(), grafico.categorias().get("ALIMENTACAO").stripTrailingZeros());
        }
    }

    @Nested
    class BuscaAvancadaTest {
        @Test
        void deveBuscarDespesasPorIntervaloDeDatas() {
            when(despesaRepository.findByUserAndDateRange(anyString(), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of(despesa));

            LocalDate inicio = LocalDate.of(2024, 1, 1);
            LocalDate fim = LocalDate.of(2024, 2, 18);

            List<DespesaEntity> despesas = despesaService.buscarDespesasPorIntervaloDeDatas(user.getUuid(), inicio, fim);

            assertNotNull(despesas);
            assertEquals(1, despesas.size());
            assertEquals(BigDecimal.valueOf(100).stripTrailingZeros(), despesas.get(0).getValor().stripTrailingZeros());
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
            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeDatas(user.getUuid(), null, LocalDate.of(2024, 2, 18)));
            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeDatas(user.getUuid(), LocalDate.of(2024, 1, 1), null));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeDatasComInicioDepoisDoFim() {
            LocalDate inicio = LocalDate.of(2024, 1, 10);
            LocalDate fim = LocalDate.of(2024, 1, 5);

            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeDatas(user.getUuid(), inicio, fim));
        }

        @Test
        void buscarDespesasPorIntervaloDeDatas_DeveLancarDespesaOperationException_QuandoRepositorioFalhar() {
            // Arrange
            String userId = UUID.randomUUID().toString();
            LocalDate inicio = LocalDate.of(2024, 1, 1);
            LocalDate fim = LocalDate.of(2024, 12, 31);

            when(despesaRepository.findByUserAndDateRange(userId, inicio, fim)).thenThrow(new RuntimeException("Erro no repositório"));

            // Act & Assert
            assertThrows(DespesaOperationException.class, () -> despesaService.buscarDespesasPorIntervaloDeDatas(userId, inicio, fim));
        }

        @Test
        void deveBuscarDespesasPorIntervaloDeValores() {
            when(despesaRepository.findByUserAndValueBetween(anyString(), any(BigDecimal.class), any(BigDecimal.class)))
                    .thenReturn(List.of(despesa));

            BigDecimal min = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.valueOf(150);

            List<DespesaEntity> despesas = despesaService.buscarDespesasPorIntervaloDeValores(user.getUuid(), min, max);

            assertNotNull(despesas);
            assertEquals(1, despesas.size());
            assertEquals(BigDecimal.valueOf(100).stripTrailingZeros(), despesas.get(0).getValor().stripTrailingZeros());
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
            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(user.getUuid(), null, BigDecimal.valueOf(150)));
            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(user.getUuid(), BigDecimal.valueOf(100), null));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeValoresComValoresMenoresOuIguaisAZero() {
            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(user.getUuid(), BigDecimal.ZERO, BigDecimal.valueOf(150)));
            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(user.getUuid(), BigDecimal.valueOf(100), BigDecimal.ZERO));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeValoresComMinMaiorQueMax() {
            BigDecimal min = BigDecimal.valueOf(200);
            BigDecimal max = BigDecimal.valueOf(100);

            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(user.getUuid(), min, max));
        }

        @Test
        void buscarDespesasPorIntervaloDeValores_DeveLancarDespesaOperationException_QuandoRepositorioFalhar() {
            // Arrange
            String userId = UUID.randomUUID().toString();
            BigDecimal min = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.valueOf(200);

            when(despesaRepository.findByUserAndValueBetween(userId, min, max)).thenThrow(new RuntimeException("Erro no repositório"));

            // Act & Assert
            assertThrows(DespesaOperationException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(userId, min, max));
        }
    }
}