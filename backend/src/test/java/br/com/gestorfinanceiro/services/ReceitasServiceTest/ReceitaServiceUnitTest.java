package br.com.gestorfinanceiro.services.ReceitasServiceTest;

import br.com.gestorfinanceiro.dto.grafico.GraficoBarraDTO;
import br.com.gestorfinanceiro.dto.grafico.GraficoPizzaDTO;
import br.com.gestorfinanceiro.dto.receita.ReceitaCreateDTO;
import br.com.gestorfinanceiro.dto.receita.ReceitaUpdateDTO;
import br.com.gestorfinanceiro.exceptions.common.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.common.InvalidUuidException;
import br.com.gestorfinanceiro.exceptions.receita.ReceitaNotFoundException;
import br.com.gestorfinanceiro.exceptions.receita.ReceitaOperationException;
import br.com.gestorfinanceiro.exceptions.user.InvalidUserIdException;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.models.enums.Roles;
import br.com.gestorfinanceiro.repositories.CategoriaRepository;
import br.com.gestorfinanceiro.repositories.ReceitaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.impl.ReceitaServiceImpl;
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
class ReceitaServiceUnitTest {

    private static final String CATEGORIA_PADRAO = "Salario";
    private static final String ORIGEM_PAGAMENTO_PADRAO = "Empresa X";
    private static final String OBSERVACOES_PADRAO = "Remuneracao";
    private static final BigDecimal VALOR_PADRAO = BigDecimal.valueOf(10000);
    private static final BigDecimal VALOR_ATUALIZADO = BigDecimal.valueOf(20000);
    private static final BigDecimal VALOR_NEGATIVO = BigDecimal.valueOf(-100);

    @Mock
    private ReceitaRepository receitaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ReceitaServiceImpl receitaService;

    private UserEntity user;
    private ReceitaEntity receita;
    private CategoriaEntity categoria;
    private ReceitaCreateDTO receitaCreateDTO;
    private ReceitaUpdateDTO receitaUpdateDTO;

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
        categoria.setTipo(CategoriaType.RECEITAS);
        categoria.setUser(user);

        receita = new ReceitaEntity();
        receita.setUuid(UUID.randomUUID().toString());
        receita.setData(LocalDate.now());
        receita.setValor(VALOR_PADRAO);
        receita.setCategoria(categoria);
        receita.setOrigemDoPagamento(ORIGEM_PAGAMENTO_PADRAO);
        receita.setObservacoes(OBSERVACOES_PADRAO);
        receita.setUser(user);

        receitaCreateDTO = new ReceitaCreateDTO();
        receitaCreateDTO.setData(LocalDate.now());
        receitaCreateDTO.setCategoria(CATEGORIA_PADRAO);
        receitaCreateDTO.setValor(VALOR_PADRAO);
        receitaCreateDTO.setOrigemDoPagamento(ORIGEM_PAGAMENTO_PADRAO);
        receitaCreateDTO.setObservacoes(OBSERVACOES_PADRAO);

        receitaUpdateDTO = new ReceitaUpdateDTO();
        receitaUpdateDTO.setData(LocalDate.now());
        receitaUpdateDTO.setCategoria(CATEGORIA_PADRAO);
        receitaUpdateDTO.setValor(VALOR_ATUALIZADO);
        receitaUpdateDTO.setOrigemDoPagamento(ORIGEM_PAGAMENTO_PADRAO);
        receitaUpdateDTO.setObservacoes(OBSERVACOES_PADRAO);

    }

    @Test
    void deveCarregarReceitaService() {
        assertNotNull(receitaService, "O ReceitaService não deveria ser nulo!");
    }

    @Nested
    class CriarReceitaTest {

        @Test
        void erroAoCriarReceitaComValorInvalido() {
            receitaCreateDTO.setValor(BigDecimal.ZERO);
            String userId = user.getUuid();
            assertThrows(InvalidDataException.class, () -> receitaService.criarReceita(receitaCreateDTO, userId));
        }

        @Test
        void erroAoCriarReceitaComValorNulo() {
            receitaCreateDTO.setValor(null);
            String userId = user.getUuid();
            assertThrows(InvalidDataException.class, () -> receitaService.criarReceita(receitaCreateDTO, userId));
        }

        @Test
        void erroAoCriarReceitaComValorNegativo() {
            receitaCreateDTO.setValor(VALOR_NEGATIVO);
            String userId = user.getUuid();
            assertThrows(InvalidDataException.class, () -> receitaService.criarReceita(receitaCreateDTO, userId));
        }

        @Test
        void erroAoCriarReceitaUsuarioNaoEncontrado() {
            when(userRepository.findById(anyString())).thenReturn(Optional.empty());
            String userId = UUID.randomUUID().toString();

            assertThrows(RuntimeException.class, () -> receitaService.criarReceita(receitaCreateDTO, userId));
        }
    }

    @Nested
    class ListarReceitasTest {

        @Test
        void deveListarReceitasUsuario() {
            String userId = user.getUuid();
            when(receitaRepository.findAllByUserUuid(userId)).thenReturn(List.of(receita));

            List<ReceitaEntity> receitas = receitaService.listarReceitasUsuario(userId);
            assertFalse(receitas.isEmpty());
        }

        @Test
        void erroAoListarReceitasUsuarioComIdNuloOuVazio() {
            assertThrows(InvalidUserIdException.class, () -> receitaService.listarReceitasUsuario(null));
            assertThrows(InvalidUserIdException.class, () -> receitaService.listarReceitasUsuario(""));
        }

        @Test
        void erroAoListarReceitasVazia() {
            String userId = UUID.randomUUID().toString();
            when(receitaRepository.findAllByUserUuid(anyString())).thenReturn(List.of());

            assertThrows(ReceitaNotFoundException.class, () -> receitaService.listarReceitasUsuario(userId));
        }
    }

    @Nested
    class BuscarReceitaPorIdTest {

        @Test
        void deveBuscarReceitaPorId() {
            String receitaId = receita.getUuid();
            when(receitaRepository.findById(receitaId)).thenReturn(Optional.of(receita));

            ReceitaEntity receitaBuscada = receitaService.buscarReceitaPorId(receitaId);
            assertNotNull(receitaBuscada);
        }

        @Test
        void erroAoBuscarReceitaPorIdInexistente() {
            String receitaId = UUID.randomUUID().toString();
            when(receitaRepository.findById(receitaId)).thenReturn(Optional.empty());

            assertThrows(ReceitaNotFoundException.class, () -> receitaService.buscarReceitaPorId(receitaId));
        }

        @Test
        void erroAoBuscarReceitaPorIdNuloOuVazio() {
            assertThrows(InvalidUuidException.class, () -> receitaService.buscarReceitaPorId(null));
            assertThrows(InvalidUuidException.class, () -> receitaService.buscarReceitaPorId(""));
        }
    }

    @Nested
    class AtualizarReceitaTest {

        @Test
        void deveAtualizarReceita() {
            when(receitaRepository.findById(receita.getUuid())).thenReturn(Optional.of(receita));
            when(categoriaRepository.findByNomeAndTipoAndUserUuid(CATEGORIA_PADRAO, CategoriaType.RECEITAS, user.getUuid())).thenReturn(Optional.of(categoria));
            when(receitaRepository.save(any(ReceitaEntity.class))).thenReturn(receita);

            receita.setValor(BigDecimal.valueOf(20000));
            ReceitaEntity receitaAtualizada = receitaService.atualizarReceita(receita.getUuid(), receitaUpdateDTO);

            assertEquals(BigDecimal.valueOf(20000), receitaAtualizada.getValor());
        }

        @Test
        void erroAoAtualizarReceitaInexistente() {
            String receitaId = UUID.randomUUID().toString();
            when(receitaRepository.findById(receitaId)).thenReturn(Optional.empty());

            assertThrows(ReceitaNotFoundException.class, () -> receitaService.atualizarReceita(receitaId, receitaUpdateDTO));
        }

        @Test
        void erroAoAtualizarUuidNullOuVazio() {
            assertThrows(InvalidUuidException.class, () -> receitaService.atualizarReceita(null, receitaUpdateDTO));
            assertThrows(InvalidUuidException.class, () -> receitaService.atualizarReceita("", receitaUpdateDTO));
        }

        @Test
        void erroAoAtualizarReceitaNula() {
            String receitaId = UUID.randomUUID().toString();

            assertThrows(InvalidDataException.class, () -> receitaService.atualizarReceita(receitaId, null));
        }

        @Test
        void atualizarReceita_DeveLancarReceitaOperationException_QuandoRepositorioFalhar() {
            when(categoriaRepository.findByNomeAndTipoAndUserUuid(CATEGORIA_PADRAO, CategoriaType.RECEITAS, user.getUuid())).thenReturn(Optional.of(categoria));

            // Arrange
            ReceitaUpdateDTO receitaUpdateDto = new ReceitaUpdateDTO();
            receitaUpdateDto.setValor(VALOR_ATUALIZADO);
            receitaUpdateDto.setData(LocalDate.now());
            receitaUpdateDto.setCategoria(CATEGORIA_PADRAO);

            String receitaId = receita.getUuid();

            when(receitaRepository.findById(receitaId)).thenReturn(Optional.of(receita));
            when(receitaRepository.save(any(ReceitaEntity.class))).thenThrow(new RuntimeException("Erro no repositório"));

            // Act & Assert
            assertThrows(ReceitaOperationException.class,
                    () -> receitaService.atualizarReceita(receitaId, receitaUpdateDto));
        }
    }

    @Nested
    class excluirReceitaTest {

        @Test
        void deveExcluirReceita() {
            String receitaId = receita.getUuid();
            when(receitaRepository.findById(receitaId)).thenReturn(Optional.of(receita));
            doNothing().when(receitaRepository).delete(receita);

            receitaService.excluirReceita(receitaId);
            verify(receitaRepository, times(1)).delete(receita);
        }

        @Test
        void erroAoExcluirReceitaInexistente() {
            String receitaId = UUID.randomUUID().toString();
            when(receitaRepository.findById(receitaId)).thenReturn(Optional.empty());

            assertThrows(ReceitaNotFoundException.class, () -> receitaService.excluirReceita(receitaId));
        }

        @Test
        void erroAoExcluirReceitaComIdNuloOuVazio() {
            assertThrows(InvalidUuidException.class, () -> receitaService.excluirReceita(null));
            assertThrows(InvalidUuidException.class, () -> receitaService.excluirReceita(""));
        }

        @Test
        void excluirReceita_DeveLancarReceitaOperationException_QuandoRepositorioFalhar() {
            // Arrange
            ReceitaEntity receitaTest = new ReceitaEntity();
            receitaTest.setUuid(UUID.randomUUID().toString());
            receitaTest.setValor(BigDecimal.valueOf(100));
            receitaTest.setData(LocalDate.now());
            receitaTest.setCategoria(categoria);

            String receitaId = receitaTest.getUuid();

            when(receitaRepository.findById(receitaId)).thenReturn(Optional.of(receitaTest));
            doThrow(new RuntimeException("Erro no repositório")).when(receitaRepository).delete(receitaTest);

            // Act & Assert
            assertThrows(ReceitaOperationException.class, () -> receitaService.excluirReceita(receitaId));
        }
    }

    @Nested
    class gerarGraficoTest {

        @Test
        void deveGerarGraficoBarras() {
            ReceitaEntity receita = mock(ReceitaEntity.class);
            when(receita.getValor()).thenReturn(BigDecimal.valueOf(10000));
            when(receita.getData()).thenReturn(LocalDate.of(2025, 3, 1));
            
            when(receitaRepository.findByUserAndYearMonthRange(anyString(), any(), any()))
                .thenReturn(List.of(receita));
            
            GraficoBarraDTO resultado = receitaService.gerarGraficoBarras("user123", 
                YearMonth.of(2025, 1), 
                YearMonth.of(2025, 3));
            
            assertNotNull(resultado);
            assertEquals(3, resultado.dadosMensais().size());
            assertEquals(BigDecimal.valueOf(10000), resultado.dadosMensais().get("março 2025"));
            assertEquals(BigDecimal.ZERO, resultado.dadosMensais().get("janeiro 2025"));
            assertEquals(BigDecimal.ZERO, resultado.dadosMensais().get("fevereiro 2025"));
        }

        @Test
        void deveGerarGraficoPizza() {
            when(receitaRepository.findByUserAndDateRange(anyString(), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of(receita));

            LocalDate inicio = LocalDate.of(2024, 1, 1);
            LocalDate fim = LocalDate.of(2024, 12, 31);

            GraficoPizzaDTO grafico = receitaService.gerarGraficoPizza(user.getUuid(), inicio, fim);

            assertNotNull(grafico);
            assertEquals(1, grafico.categorias().size());
            assertEquals(BigDecimal.valueOf(10000).stripTrailingZeros(), grafico.categorias().get("Salario").stripTrailingZeros());
        }
    }

    @Nested
    class buscaAvancadaTest {

        @Test
        void deveBuscarReceitasPorIntervaloDeDatas() {
            when(receitaRepository.findByUserAndDateRange(anyString(), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of(receita));

            LocalDate inicio = LocalDate.of(2024, 1, 1);
            LocalDate fim = LocalDate.of(2024, 2, 18);

            List<ReceitaEntity> receitas = receitaService.buscarReceitasPorIntervaloDeDatas(user.getUuid(), inicio, fim);

            assertNotNull(receitas);
            assertEquals(1, receitas.size());
            assertEquals(BigDecimal.valueOf(10000).stripTrailingZeros(), receitas.get(0).getValor().stripTrailingZeros());
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
            LocalDate inicio = LocalDate.of(2024, 1, 1);
            LocalDate fim = LocalDate.of(2024, 2, 18);
            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeDatas(userId, null, fim));
            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeDatas(userId, inicio, null));
        }

        @Test
        void erroAoBuscarReceitassPorIntervaloDeDatasComInicioDepoisDoFim() {
            String userId = user.getUuid();
            LocalDate inicio = LocalDate.of(2024, 1, 10);
            LocalDate fim = LocalDate.of(2024, 1, 5);

            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeDatas(userId, inicio, fim));
        }

        @Test
        void buscarReceitasPorIntervaloDeDatas_DeveLancarReceitaOperationException_QuandoRepositorioFalhar() {
            // Arrange
            String userId = UUID.randomUUID().toString();
            LocalDate inicio = LocalDate.of(2024, 1, 1);
            LocalDate fim = LocalDate.of(2024, 12, 31);

            when(receitaRepository.findByUserAndDateRange(userId, inicio, fim)).thenThrow(new RuntimeException("Erro no repositório"));

            // Act & Assert
            assertThrows(ReceitaOperationException.class, () -> receitaService.buscarReceitasPorIntervaloDeDatas(userId, inicio, fim));
        }

        @Test
        void deveBuscarReceitasPorIntervaloDeValores() {
            when(receitaRepository.findByUserAndValueBetween(anyString(), any(BigDecimal.class), any(BigDecimal.class)))
                    .thenReturn(List.of(receita));

            BigDecimal min = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.valueOf(150);

            List<ReceitaEntity> receitas = receitaService.buscarReceitasPorIntervaloDeValores(user.getUuid(), min, max);

            assertNotNull(receitas);
            assertEquals(1, receitas.size());
            assertEquals(BigDecimal.valueOf(10000).stripTrailingZeros(), receitas.get(0).getValor().stripTrailingZeros());
        }

        @Test
        void erroAoBuscarReceitasPorIntervaloDeValoresComUserIdNuloOuVazio() {
            BigDecimal min = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.valueOf(150);

            assertThrows(InvalidUserIdException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores(null, min, max));
            assertThrows(InvalidUserIdException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores("", min, max));
        }

        @Test
        void erroAoBuscarReceitassPorIntervaloDeValoresComValoresNulos() {
            String userId = user.getUuid();
            BigDecimal min = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.valueOf(150);

            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores(userId, null, max));
            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores(userId, min, null));
        }

        @Test
        void erroAoBuscarReceitasPorIntervaloDeValoresComValoresMenoresOuIguaisAZero() {
            String userId = user.getUuid();
            BigDecimal min = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.valueOf(150);

            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores(userId, BigDecimal.ZERO, max));
            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores(userId, min, BigDecimal.ZERO));
        }

        @Test
        void erroAoBuscarReceitassPorIntervaloDeValoresComMinMaiorQueMax() {
            String userId = user.getUuid();
            BigDecimal min = BigDecimal.valueOf(200);
            BigDecimal max = BigDecimal.valueOf(100);

            assertThrows(InvalidDataException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores(userId, min, max));
        }

        @Test
        void buscarReceitasPorIntervaloDeValores_DeveLancarReceitaOperationException_QuandoRepositorioFalhar() {
            // Arrange
            String userId = UUID.randomUUID().toString();
            BigDecimal min = BigDecimal.valueOf(100);
            BigDecimal max = BigDecimal.valueOf(200);

            when(receitaRepository.findByUserAndValueBetween(userId, min, max)).thenThrow(new RuntimeException("Erro no repositório"));

            // Act & Assert
            assertThrows(ReceitaOperationException.class, () -> receitaService.buscarReceitasPorIntervaloDeValores(userId, min, max));
        }
    }
}