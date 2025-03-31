package br.com.gestorfinanceiro.services.OrcamentoMensalServiceTest;

import br.com.gestorfinanceiro.exceptions.categoria.CategoriaNameNotFoundException;
import br.com.gestorfinanceiro.exceptions.orcamentomensal.OrcamentoMensalAlreadyExistsException;
import br.com.gestorfinanceiro.exceptions.orcamentomensal.OrcamentoMensalNotFoundException;
import br.com.gestorfinanceiro.exceptions.common.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.common.InvalidUuidException;
import br.com.gestorfinanceiro.exceptions.user.UserNotFoundException;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.OrcamentoMensalEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.models.enums.Roles;
import br.com.gestorfinanceiro.repositories.CategoriaRepository;
import br.com.gestorfinanceiro.repositories.OrcamentoMensalRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.impl.OrcamentoMensalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrcamentoMensalServiceUnitTest {

    private static final String USER_ID = UUID.randomUUID().toString();
    private static final String ORCAMENTO_ID = UUID.randomUUID().toString();
    private static final String CATEGORIA_PADRAO = "Alimentacao";
    private static final BigDecimal VALOR_PADRAO = BigDecimal.valueOf(100);
    private static final BigDecimal VALOR_ATUALIZADO = BigDecimal.valueOf(200);
    private static final BigDecimal VALOR_NEGATIVO = BigDecimal.valueOf(-100);
    private static final YearMonth PERIODO_PADRAO = YearMonth.of(2023, 1);
    private static final YearMonth PERIODO_DIFERENTE = YearMonth.of(2023, 2);

    @InjectMocks
    private OrcamentoMensalServiceImpl orcamentoMensalService;

    @Mock
    private OrcamentoMensalRepository orcamentoMensalRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UserRepository userRepository;

    private UserEntity user;
    private CategoriaEntity categoria;
    private OrcamentoMensalEntity orcamentoExistente;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setUuid(USER_ID);
        user.setUsername("Test User");
        user.setRole(Roles.USER);

        categoria = new CategoriaEntity();
        categoria.setNome(CATEGORIA_PADRAO);
        categoria.setTipo(CategoriaType.DESPESAS);
        categoria.setUser(user);

        orcamentoExistente = new OrcamentoMensalEntity();
        orcamentoExistente.setUuid(ORCAMENTO_ID);
        orcamentoExistente.setUser(user);
        orcamentoExistente.setCategoria(categoria);
        orcamentoExistente.setValorLimite(VALOR_PADRAO);
        orcamentoExistente.setPeriodo(PERIODO_PADRAO);
    }

    @Nested
    class CriarOrcamentoMensal {
        @Test
        void deveCriarOrcamentoComSucesso() {
            // Arrange
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
            when(categoriaRepository.findByNomeAndUserUuid(CATEGORIA_PADRAO, USER_ID))
                    .thenReturn(Optional.of(categoria));
            when(orcamentoMensalRepository.findByCategoriaAndPeriodoAndUserUuid(categoria, PERIODO_PADRAO, USER_ID))
                    .thenReturn(Optional.empty());
            when(orcamentoMensalRepository.save(any(OrcamentoMensalEntity.class)))
                    .thenReturn(orcamentoExistente);

            // Act
            OrcamentoMensalEntity result = orcamentoMensalService.criarOrcamentoMensal(
                    USER_ID, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);

            // Assert
            assertNotNull(result);
            assertEquals(ORCAMENTO_ID, result.getUuid());
            verify(orcamentoMensalRepository).save(any(OrcamentoMensalEntity.class));
        }

        @Test
        void deveLancarExcecaoQuandoUsuarioNaoExiste() {
            // Arrange
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> {
                orcamentoMensalService.criarOrcamentoMensal(
                        USER_ID, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);
            });
        }

        @Test
        void deveLancarExcecaoQuandoCategoriaNaoExiste() {
            // Arrange
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
            when(categoriaRepository.findByNomeAndUserUuid(CATEGORIA_PADRAO, USER_ID))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CategoriaNameNotFoundException.class, () -> {
                orcamentoMensalService.criarOrcamentoMensal(
                        USER_ID, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);
            });
        }

        @Test
        void deveLancarExcecaoQuandoOrcamentoJaExiste() {
            // Arrange
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
            when(categoriaRepository.findByNomeAndUserUuid(CATEGORIA_PADRAO, USER_ID))
                    .thenReturn(Optional.of(categoria));
            when(orcamentoMensalRepository.findByCategoriaAndPeriodoAndUserUuid(categoria, PERIODO_PADRAO, USER_ID))
                    .thenReturn(Optional.of(orcamentoExistente));

            // Act & Assert
            assertThrows(OrcamentoMensalAlreadyExistsException.class, () -> {
                orcamentoMensalService.criarOrcamentoMensal(
                        USER_ID, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);
            });
        }

        @Test
        void deveLancarExcecaoQuandoValorNegativo() {
            // Act & Assert
            assertThrows(InvalidDataException.class, () -> {
                orcamentoMensalService.criarOrcamentoMensal(
                        USER_ID, CATEGORIA_PADRAO, VALOR_NEGATIVO, PERIODO_PADRAO);
            });
        }

        @Test
        void deveLancarExcecaoQuandoUserIdInvalido() {
            // Act & Assert
            assertThrows(InvalidUuidException.class, () -> {
                orcamentoMensalService.criarOrcamentoMensal(
                        null, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);
            });
        }
    }

    @Nested
    class ListarOrcamentosMensais {
        @Test
        void deveListarTodosPorUsuarioComSucesso() {
            // Arrange
            when(orcamentoMensalRepository.findByUserId(USER_ID))
                    .thenReturn(List.of(orcamentoExistente));

            // Act
            List<OrcamentoMensalEntity> result = orcamentoMensalService.listarTodosPorUsuario(USER_ID);

            // Assert
            assertEquals(1, result.size());
            assertEquals(ORCAMENTO_ID, result.get(0).getUuid());
        }

        @Test
        void deveLancarExcecaoQuandoNenhumOrcamentoEncontrado() {
            // Arrange
            when(orcamentoMensalRepository.findByUserId(USER_ID))
                    .thenReturn(List.of());

            // Act & Assert
            assertThrows(OrcamentoMensalNotFoundException.class, () -> {
                orcamentoMensalService.listarTodosPorUsuario(USER_ID);
            });
        }

        @Test
        void deveListarPorPeriodoComSucesso() {
            // Arrange
            when(orcamentoMensalRepository.findByPeriodo(PERIODO_PADRAO))
                    .thenReturn(List.of(orcamentoExistente));

            // Act
            List<OrcamentoMensalEntity> result = orcamentoMensalService.listarPorPeriodo(USER_ID, PERIODO_PADRAO);

            // Assert
            assertEquals(1, result.size());
            assertEquals(PERIODO_PADRAO, result.get(0).getPeriodo());
        }

        @Test
        void deveLancarExcecaoQuandoPeriodoInvalido() {
            // Act & Assert
            assertThrows(InvalidDataException.class, () -> {
                orcamentoMensalService.listarPorPeriodo(USER_ID, null);
            });
        }

        @Test
        void deveBuscarPorIdComSucesso() {
            // Arrange
            when(orcamentoMensalRepository.findByUuidAndUserUuid(ORCAMENTO_ID, USER_ID))
                    .thenReturn(Optional.of(orcamentoExistente));

            // Act
            OrcamentoMensalEntity result = orcamentoMensalService.buscarPorId(USER_ID, ORCAMENTO_ID);

            // Assert
            assertNotNull(result);
            assertEquals(ORCAMENTO_ID, result.getUuid());
        }

        @Test
        void deveLancarExcecaoQuandoOrcamentoNaoEncontrado() {
            // Arrange
            when(orcamentoMensalRepository.findByUuidAndUserUuid(ORCAMENTO_ID, USER_ID))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(OrcamentoMensalNotFoundException.class, () -> {
                orcamentoMensalService.buscarPorId(USER_ID, ORCAMENTO_ID);
            });
        }
    }

    @Nested
    class AtualizarOrcamentoMensal {
        @Test
        void deveAtualizarOrcamentoComSucesso() {
            // Arrange
            when(orcamentoMensalRepository.findByUuidAndUserUuid(ORCAMENTO_ID, USER_ID))
                    .thenReturn(Optional.of(orcamentoExistente));
            when(categoriaRepository.findByNomeAndUserUuid(CATEGORIA_PADRAO, USER_ID))
                    .thenReturn(Optional.of(categoria));
            when(orcamentoMensalRepository.findByCategoriaAndPeriodoAndUserUuid(categoria, PERIODO_DIFERENTE, USER_ID))
                    .thenReturn(Optional.empty());
            when(orcamentoMensalRepository.save(any(OrcamentoMensalEntity.class)))
                    .thenReturn(orcamentoExistente);

            // Act
            OrcamentoMensalEntity result = orcamentoMensalService.atualizarOrcamentoMensal(
                    USER_ID, ORCAMENTO_ID, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);

            // Assert
            assertNotNull(result);
            verify(orcamentoMensalRepository).save(any(OrcamentoMensalEntity.class));
        }

        @Test
        void deveLancarExcecaoQuandoOrcamentoNaoExiste() {
            // Arrange
            when(orcamentoMensalRepository.findByUuidAndUserUuid(ORCAMENTO_ID, USER_ID))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(OrcamentoMensalNotFoundException.class, () -> {
                orcamentoMensalService.atualizarOrcamentoMensal(
                        USER_ID, ORCAMENTO_ID, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);
            });
        }

        @Test
        void deveLancarExcecaoQuandoCategoriaNaoExiste() {
            // Arrange
            when(orcamentoMensalRepository.findByUuidAndUserUuid(ORCAMENTO_ID, USER_ID))
                    .thenReturn(Optional.of(orcamentoExistente));
            when(categoriaRepository.findByNomeAndUserUuid(CATEGORIA_PADRAO, USER_ID))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CategoriaNameNotFoundException.class, () -> {
                orcamentoMensalService.atualizarOrcamentoMensal(
                        USER_ID, ORCAMENTO_ID, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);
            });
        }

        @Test
        void deveLancarExcecaoQuandoOrcamentoDuplicado() {
            // Arrange
            OrcamentoMensalEntity outroOrcamento = new OrcamentoMensalEntity();
            outroOrcamento.setUuid(UUID.randomUUID().toString());

            when(orcamentoMensalRepository.findByUuidAndUserUuid(ORCAMENTO_ID, USER_ID))
                    .thenReturn(Optional.of(orcamentoExistente));
            when(categoriaRepository.findByNomeAndUserUuid(CATEGORIA_PADRAO, USER_ID))
                    .thenReturn(Optional.of(categoria));
            when(orcamentoMensalRepository.findByCategoriaAndPeriodoAndUserUuid(categoria, PERIODO_DIFERENTE, USER_ID))
                    .thenReturn(Optional.of(outroOrcamento));

            // Act & Assert
            assertThrows(OrcamentoMensalAlreadyExistsException.class, () -> {
                orcamentoMensalService.atualizarOrcamentoMensal(
                        USER_ID, ORCAMENTO_ID, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);
            });
        }
    }

    @Nested
    class DeletarOrcamentoMensal {
        @Test
        void deveDeletarOrcamentoComSucesso() {
            // Arrange
            when(orcamentoMensalRepository.findByUuidAndUserUuid(ORCAMENTO_ID, USER_ID))
                    .thenReturn(Optional.of(orcamentoExistente));
            doNothing().when(orcamentoMensalRepository).delete(orcamentoExistente);

            // Act
            orcamentoMensalService.excluirOrcamentoMensal(USER_ID, ORCAMENTO_ID);

            // Assert
            verify(orcamentoMensalRepository).delete(orcamentoExistente);
        }

        @Test
        void deveLancarExcecaoQuandoOrcamentoNaoExiste() {
            // Arrange
            when(orcamentoMensalRepository.findByUuidAndUserUuid(ORCAMENTO_ID, USER_ID))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(OrcamentoMensalNotFoundException.class, () -> {
                orcamentoMensalService.excluirOrcamentoMensal(USER_ID, ORCAMENTO_ID);
            });
        }
    }
}