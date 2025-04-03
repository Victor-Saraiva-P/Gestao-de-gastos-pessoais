package br.com.gestorfinanceiro.services.DespesaServiceTest;

import br.com.gestorfinanceiro.dto.despesa.DespesaCreateDTO;
import br.com.gestorfinanceiro.dto.despesa.DespesaUpdateDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoPizzaDTO;
import br.com.gestorfinanceiro.exceptions.common.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.common.InvalidUuidException;
import br.com.gestorfinanceiro.exceptions.despesa.DespesaNotFoundException;
import br.com.gestorfinanceiro.exceptions.despesa.DespesaOperationException;
import br.com.gestorfinanceiro.exceptions.user.InvalidUserIdException;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.models.enums.Roles;
import br.com.gestorfinanceiro.repositories.CategoriaRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DespesaServiceUnitTest {

    private static final String CATEGORIA_PADRAO = "Alimentacao";
    private static final String DESTINO_PAGAMENTO_PADRAO = "Mercado";
    private static final String OBSERVACOES_PADRAO = "Compras do mês";
    private static final BigDecimal VALOR_PADRAO = BigDecimal.valueOf(100);
    private static final BigDecimal VALOR_ATUALIZADO = BigDecimal.valueOf(200);
    private static final BigDecimal VALOR_NEGATIVO = BigDecimal.valueOf(-100);

    @Mock
    private DespesaRepository despesaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private DespesaServiceImpl despesaService;

    private UserEntity user;
    private DespesaEntity despesa;
    private CategoriaEntity categoria;
    private DespesaCreateDTO despesaCreateDTO;
    private DespesaUpdateDTO despesaUpdateDTO;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setUuid(UUID.randomUUID().toString());
        user.setUsername("Jorge");
        user.setEmail("jorge@gmail.com");
        user.setPassword("123456");
        user.setRole(Roles.USER);

        categoria = new CategoriaEntity();
        categoria.setUuid(UUID.randomUUID().toString());
        categoria.setNome(CATEGORIA_PADRAO);
        categoria.setTipo(CategoriaType.DESPESAS);
        categoria.setUser(user);

        despesa = new DespesaEntity();
        despesa.setUuid(UUID.randomUUID().toString());
        despesa.setData(LocalDate.now());
        despesa.setValor(VALOR_PADRAO);
        despesa.setCategoria(categoria);
        despesa.setDestinoPagamento(DESTINO_PAGAMENTO_PADRAO);
        despesa.setObservacoes(OBSERVACOES_PADRAO);
        despesa.setUser(user);

        despesaCreateDTO = new DespesaCreateDTO();
        despesaCreateDTO.setData(LocalDate.now());
        despesaCreateDTO.setCategoria(CATEGORIA_PADRAO);
        despesaCreateDTO.setValor(VALOR_PADRAO);
        despesaCreateDTO.setDestinoPagamento(DESTINO_PAGAMENTO_PADRAO);
        despesaCreateDTO.setObservacoes(OBSERVACOES_PADRAO);

        despesaUpdateDTO = new DespesaUpdateDTO();
        despesaUpdateDTO.setData(LocalDate.now());
        despesaUpdateDTO.setCategoria(CATEGORIA_PADRAO);
        despesaUpdateDTO.setValor(VALOR_ATUALIZADO);
        despesaUpdateDTO.setDestinoPagamento(DESTINO_PAGAMENTO_PADRAO);
        despesaUpdateDTO.setObservacoes(OBSERVACOES_PADRAO);
    }


    @Test
    void deveCarregarDespesaService() {
        assertNotNull(despesaService, "O DespesaService não deveria ser nulo!");
    }

    @Nested
    class CriarDespesaTest {

        @Test
        void erroAoCriarDespesaComValorInvalido() {
            despesaCreateDTO.setValor(BigDecimal.ZERO);
            String userId = user.getUuid();
            assertThrows(InvalidDataException.class, () -> despesaService.criarDespesa(despesaCreateDTO, userId));
        }

        @Test
        void erroAoCriarDespesaComValorNulo() {
            despesaCreateDTO.setValor(null);
            String userId = user.getUuid();
            assertThrows(InvalidDataException.class, () -> despesaService.criarDespesa(despesaCreateDTO, userId));
        }

        @Test
        void erroAoCriarDespesaComValorNegativo() {
            despesaCreateDTO.setValor(VALOR_NEGATIVO);
            String userId = user.getUuid();
            assertThrows(InvalidDataException.class, () -> despesaService.criarDespesa(despesaCreateDTO, userId));
        }

        @Test
        void erroAoCriarDespesaUsuarioNaoEncontrado() {
            when(userRepository.findById(anyString())).thenReturn(Optional.empty());
            String userId = UUID.randomUUID().toString();

            assertThrows(RuntimeException.class, () -> despesaService.criarDespesa(despesaCreateDTO, userId));
        }
    }

    @Nested
    class ListarDespesasTest {

        @Test
        void deveListarDespesasUsuario() {
            String userId = user.getUuid();
            when(despesaRepository.findAllByUserUuid(userId)).thenReturn(List.of(despesa));

            List<DespesaEntity> despesas = despesaService.listarDespesasUsuario(userId);
            assertFalse(despesas.isEmpty());
        }

        @Test
        void erroAoListarDespesasUsuarioComIdNuloOuVazio() {
            assertThrows(InvalidUserIdException.class, () -> despesaService.listarDespesasUsuario(null));
            assertThrows(InvalidUserIdException.class, () -> despesaService.listarDespesasUsuario(""));
        }

        @Test
        void erroAoListarDespesasVazia() {
            String userId = UUID.randomUUID().toString();
            when(despesaRepository.findAllByUserUuid(anyString())).thenReturn(List.of());

            assertThrows(DespesaNotFoundException.class, () -> despesaService.listarDespesasUsuario(userId));
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
            String userId = UUID.randomUUID().toString();
            when(despesaRepository.findById(anyString())).thenReturn(Optional.empty());

            assertThrows(DespesaNotFoundException.class, () -> despesaService.buscarDespesaPorId(userId));
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
            when(categoriaRepository.findByNomeAndTipoAndUserUuid(CATEGORIA_PADRAO, CategoriaType.DESPESAS, user.getUuid())).thenReturn(Optional.of(categoria));
            when(despesaRepository.save(any(DespesaEntity.class))).thenReturn(despesa);

            despesa.setValor(BigDecimal.valueOf(200));
            DespesaEntity despesaAtualizada = despesaService.atualizarDespesa(despesa.getUuid(), despesaUpdateDTO);

            assertEquals(BigDecimal.valueOf(200), despesaAtualizada.getValor());
        }

        @Test
        void erroAoAtualizarDespesaInexistente() {
            String userId = UUID.randomUUID().toString();
            when(despesaRepository.findById(anyString())).thenReturn(Optional.empty());

            assertThrows(DespesaNotFoundException.class, () -> despesaService.atualizarDespesa(userId, despesaUpdateDTO));
        }

        @Test
        void erroAoAtualizarUuidNuloOuVazio() {
            assertThrows(InvalidUuidException.class, () -> despesaService.atualizarDespesa(null, despesaUpdateDTO));
            assertThrows(InvalidUuidException.class, () -> despesaService.atualizarDespesa("", despesaUpdateDTO));
        }

        @Test
        void erroAoAtualizarDespesaNula() {
            String despesaId = UUID.randomUUID().toString();

            assertThrows(InvalidDataException.class, () -> despesaService.atualizarDespesa(despesaId, null));
        }


        @Test
        void atualizarDespesa_DeveLancarDespesaOperationException_QuandoRepositorioFalhar() {
            when(categoriaRepository.findByNomeAndTipoAndUserUuid(CATEGORIA_PADRAO, CategoriaType.DESPESAS, user.getUuid())).thenReturn(Optional.of(categoria));
            // Arrange
            DespesaUpdateDTO despesaTest = new DespesaUpdateDTO();
            despesaTest.setValor(VALOR_ATUALIZADO);
            despesaTest.setData(LocalDate.now());
            despesaTest.setCategoria(CATEGORIA_PADRAO);

            String despesaId = despesa.getUuid();

            when(despesaRepository.findById(despesaId)).thenReturn(Optional.of(despesa));
            when(despesaRepository.save(any(DespesaEntity.class))).thenThrow(new RuntimeException("Erro no repositório"));

            // Act & Assert
            assertThrows(DespesaOperationException.class, () -> despesaService.atualizarDespesa(despesaId, despesaTest));
        }
    }

    @Nested
    class ExcluirDespesaTest {

        @Test
        void deveExcluirDespesa() {
            String despesaId = despesa.getUuid();
            when(despesaRepository.findById(despesaId)).thenReturn(Optional.of(despesa));
            doNothing().when(despesaRepository).delete(despesa); // Verifica o método delete

            despesaService.excluirDespesa(despesaId);
            verify(despesaRepository, times(1)).delete(despesa); // Verifica o método delete
        }

        @Test
        void erroAoExcluirDespesaInexistente() {
            String despesaId = UUID.randomUUID().toString();
            when(despesaRepository.findById(anyString())).thenReturn(Optional.empty());

            assertThrows(DespesaNotFoundException.class, () -> despesaService.excluirDespesa(despesaId));
        }

        @Test
        void erroAoExcluirDespesaComUuidNuloOuVazio() {
            assertThrows(InvalidUuidException.class, () -> despesaService.excluirDespesa(null));
            assertThrows(InvalidUuidException.class, () -> despesaService.excluirDespesa(""));
        }

        @Test
        void excluirDespesa_DeveLancarDespesaOperationException_QuandoRepositorioFalhar() {
            // Arrange
            DespesaEntity despesaTest = new DespesaEntity();
            despesaTest.setUuid(UUID.randomUUID().toString());

            String despesaId = despesaTest.getUuid();

            when(despesaRepository.findById(despesaId)).thenReturn(Optional.of(despesaTest));
            doThrow(new RuntimeException("Erro no repositório")).when(despesaRepository).delete(despesaTest);

            // Act & Assert
            assertThrows(DespesaOperationException.class, () -> despesaService.excluirDespesa(despesaId));
        }
    }

    @Nested
    class GerarGraficosTest {

        @Test
        void deveGerarGraficoBarras() {
            DespesaEntity despesaMock = mock(DespesaEntity.class);
            when(despesaMock.getValor()).thenReturn(BigDecimal.valueOf(100));
            when(despesaMock.getData()).thenReturn(LocalDate.of(2025, 3, 1));
            
            when(despesaRepository.findByUserAndYearMonthRange(anyString(), any(), any()))
                .thenReturn(List.of(despesaMock));
            
            GraficoBarraDTO resultado = despesaService.gerarGraficoBarras("user123", 
                YearMonth.of(2025, 1), 
                YearMonth.of(2025, 3));
            
            assertNotNull(resultado);
            assertEquals(3, resultado.dadosMensais().size());
            assertEquals(BigDecimal.valueOf(100), resultado.dadosMensais().get("março 2025"));
            assertEquals(BigDecimal.ZERO, resultado.dadosMensais().get("janeiro 2025"));
            assertEquals(BigDecimal.ZERO, resultado.dadosMensais().get("fevereiro 2025"));
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
            assertEquals(BigDecimal.valueOf(100).stripTrailingZeros(), grafico.categorias().get("Alimentacao").stripTrailingZeros());
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
            String userId = user.getUuid();
            LocalDate inicio = LocalDate.of(2024, 1, 1);
            LocalDate fim = LocalDate.of(2024, 2, 18);
            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeDatas(userId, null, fim));
            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeDatas(userId, inicio, null));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeDatasComInicioDepoisDoFim() {
            String userId = user.getUuid();
            LocalDate inicio = LocalDate.of(2024, 1, 10);
            LocalDate fim = LocalDate.of(2024, 1, 5);

            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeDatas(userId, inicio, fim));
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
            String userId = user.getUuid();
            BigDecimal min = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.valueOf(150);

            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(userId, null, max));
            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(userId, min, null));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeValoresComValoresMenoresOuIguaisAZero() {
            String userId = user.getUuid();
            BigDecimal min = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.valueOf(150);

            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(userId, BigDecimal.ZERO, max));
            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(userId, min, BigDecimal.ZERO));
        }

        @Test
        void erroAoBuscarDespesasPorIntervaloDeValoresComMinMaiorQueMax() {
            String userId = user.getUuid();
            BigDecimal min = BigDecimal.valueOf(200);
            BigDecimal max = BigDecimal.valueOf(100);

            assertThrows(InvalidDataException.class, () -> despesaService.buscarDespesasPorIntervaloDeValores(userId, min, max));
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