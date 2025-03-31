package br.com.gestorfinanceiro.services.OrcamentoMensalServiceTest;

import br.com.gestorfinanceiro.exceptions.categoria.CategoriaNameNotFoundException;
import br.com.gestorfinanceiro.exceptions.common.InvalidUuidException;
import br.com.gestorfinanceiro.exceptions.orcamentomensal.OrcamentoMensalAlreadyExistsException;
import br.com.gestorfinanceiro.exceptions.orcamentomensal.OrcamentoMensalNotFoundException;
import br.com.gestorfinanceiro.exceptions.common.InvalidDataException;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.OrcamentoMensalEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.models.enums.Roles;
import br.com.gestorfinanceiro.repositories.CategoriaRepository;
import br.com.gestorfinanceiro.repositories.OrcamentoMensalRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.OrcamentoMensalService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OrcamentoMensalServiceIntegrationTest {

    private static final String CATEGORIA_PADRAO = "Alimentacao";
    private static final String CATEGORIA_INVALIDA = "CategoriaInexistente";
    private static final BigDecimal VALOR_PADRAO = BigDecimal.valueOf(100);
    private static final BigDecimal VALOR_ATUALIZADO = BigDecimal.valueOf(200);
    private static final BigDecimal VALOR_NEGATIVO = BigDecimal.valueOf(-100);
    private static final YearMonth PERIODO_PADRAO = YearMonth.of(2023, 1);
    private static final YearMonth PERIODO_DIFERENTE = YearMonth.of(2023, 2);

    @Autowired
    private OrcamentoMensalService orcamentoMensalService;

    @Autowired
    private OrcamentoMensalRepository orcamentoMensalRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UserRepository userRepository;

    private String userId;

    @BeforeEach
    void setUp() {
        limparBancoDeDados();
        UserEntity user = criarUsuarioTest();
        userId = user.getUuid();
        criarCategoriaTest(CATEGORIA_PADRAO, user);
    }

    private void limparBancoDeDados() {
        orcamentoMensalRepository.deleteAll();
        categoriaRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        orcamentoMensalRepository.deleteAllInBatch();
        categoriaRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Nested
    class CriarOrcamentoMensal {
        @Test
        void deveCriarOrcamentoMensalComSucesso() {
            // Act
            OrcamentoMensalEntity orcamento = orcamentoMensalService.criarOrcamentoMensal(
                    userId, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);

            // Assert
            assertNotNull(orcamento.getUuid());
            assertEquals(CATEGORIA_PADRAO, orcamento.getCategoria().getNome());
            assertEquals(VALOR_PADRAO, orcamento.getValorLimite());
            assertEquals(PERIODO_PADRAO, orcamento.getPeriodo());
            assertEquals(userId, orcamento.getUser().getUuid());
        }

        @Test
        void deveLancarExcecaoQuandoCategoriaNaoExiste() {
            // Assert
            assertThrows(CategoriaNameNotFoundException.class, () -> {
                // Act
                orcamentoMensalService.criarOrcamentoMensal(
                        userId, CATEGORIA_INVALIDA, VALOR_PADRAO, PERIODO_PADRAO);
            });
        }

        @Test
        void deveLancarExcecaoQuandoValorNegativo() {
            // Assert
            assertThrows(InvalidDataException.class, () -> {
                // Act
                orcamentoMensalService.criarOrcamentoMensal(
                        userId, CATEGORIA_PADRAO, VALOR_NEGATIVO, PERIODO_PADRAO);
            });
        }

        @Test
        void deveLancarExcecaoQuandoOrcamentoDuplicado() {
            // Arrange
            orcamentoMensalService.criarOrcamentoMensal(
                    userId, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);

            // Assert
            assertThrows(OrcamentoMensalAlreadyExistsException.class, () -> {
                // Act
                orcamentoMensalService.criarOrcamentoMensal(
                        userId, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_PADRAO);
            });
        }

        @Test
        void deveTestarMetodoValidarParametro() {
            // Assert
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.criarOrcamentoMensal(null, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);
            });
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.criarOrcamentoMensal("", CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);
            });
            assertThrows(InvalidDataException.class, () -> {
                // Act
                orcamentoMensalService.criarOrcamentoMensal(userId, CATEGORIA_PADRAO, null, PERIODO_PADRAO);
            });
            assertThrows(InvalidDataException.class, () -> {
                // Act
                orcamentoMensalService.criarOrcamentoMensal(userId, CATEGORIA_PADRAO, VALOR_PADRAO, null);
            });
            assertThrows(InvalidDataException.class, () -> {
                // Act
                orcamentoMensalService.criarOrcamentoMensal(userId, null, VALOR_PADRAO, PERIODO_PADRAO);
            });
            assertThrows(InvalidDataException.class, () -> {
                // Act
                orcamentoMensalService.criarOrcamentoMensal(userId, "", VALOR_PADRAO, PERIODO_PADRAO);
            });
        }
    }

    @Nested
    class ListarOrcamentosMensais {
        @Test
        void deveListarTodosOrcamentosPorUsuario() {
            // Arrange
            orcamentoMensalService.criarOrcamentoMensal(
                    userId, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);
            orcamentoMensalService.criarOrcamentoMensal(
                    userId, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);

            // Act
            List<OrcamentoMensalEntity> orcamentos = orcamentoMensalService.listarTodosPorUsuario(userId);

            // Assert
            assertEquals(2, orcamentos.size());
        }

        @Test
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            // Assert
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.listarTodosPorUsuario(null);
            });
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.listarTodosPorUsuario("");
            });
        }

        @Test
        void deveLancarExcecaoQuandoNenhumOrcamentoEncontrado() {
            // Assert
            assertThrows(OrcamentoMensalNotFoundException.class, () -> {
                // Act
                orcamentoMensalService.listarTodosPorUsuario(userId);
            });
        }
    }

    @Nested
    class listarOrcamentosPorPeriodo {
        @Test
        void deveListarOrcamentosPorPeriodo() {
            // Arrange
            orcamentoMensalService.criarOrcamentoMensal(
                    userId, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);
            orcamentoMensalService.criarOrcamentoMensal(
                    userId, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);

            // Act
            List<OrcamentoMensalEntity> orcamentos = orcamentoMensalService.listarPorPeriodo(userId, PERIODO_PADRAO);

            // Assert
            assertEquals(1, orcamentos.size());
            assertEquals(PERIODO_PADRAO, orcamentos.get(0).getPeriodo());
        }

        @Test
        void deveLancarExcecaoQuandoPeriodoInvalido() {
            // Assert
            assertThrows(InvalidDataException.class, () -> {
                // Act
                orcamentoMensalService.listarPorPeriodo(userId, null);
            });
        }

        @Test
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            // Assert
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.listarPorPeriodo(null, PERIODO_PADRAO);
            });
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.listarPorPeriodo("", PERIODO_PADRAO);
            });
        }

        @Test
        void deveLancarExcecaoQuandoOrcamentoMensalVazio() {
            // Assert
            assertThrows(OrcamentoMensalNotFoundException.class, () -> {
                // Act
                orcamentoMensalService.listarPorPeriodo(userId, PERIODO_PADRAO);
            });
        }
    }

    @Nested
    class BuscarOrcamentoMensalPorId {

        @Test
        void deveBuscarOrcamentoMensalPorId() {
            // Arrange
            OrcamentoMensalEntity orcamento = orcamentoMensalService.criarOrcamentoMensal(
                    userId, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);

            // Act
            OrcamentoMensalEntity orcamentoBuscado = orcamentoMensalService.buscarPorId(userId, orcamento.getUuid());

            // Assert
            assertEquals(orcamento.getUuid(), orcamentoBuscado.getUuid());
            assertEquals(orcamento.getCategoria().getNome(), orcamentoBuscado.getCategoria().getNome());
            assertEquals(0, orcamento.getValorLimite().compareTo(orcamentoBuscado.getValorLimite()));
            assertEquals(orcamento.getPeriodo(), orcamentoBuscado.getPeriodo());
        }

        @Test
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            // Assert
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.buscarPorId(null, "uuid-inexistente");
            });
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.buscarPorId("", "uuid-inexistente");
            });
        }

        @Test
        void deveLancarExcecaoQuandoOrcamentoNaoEncontrado() {
            // Assert
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.buscarPorId(userId, null);
            });
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.buscarPorId(userId, "");
            });
        }
    }

    @Nested
    class AtualizarOrcamentoMensal {
        private String orcamentoId;

        @BeforeEach
        void setUp() {
            OrcamentoMensalEntity orcamento = orcamentoMensalService.criarOrcamentoMensal(
                    userId, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);
            orcamentoId = orcamento.getUuid();
        }

        @Test
        void deveAtualizarOrcamentoComSucesso() {
            // Act
            OrcamentoMensalEntity orcamentoAtualizado = orcamentoMensalService.atualizarOrcamentoMensal(
                    userId, orcamentoId, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);

            // Assert
            assertEquals(orcamentoId, orcamentoAtualizado.getUuid());
            assertEquals(VALOR_ATUALIZADO, orcamentoAtualizado.getValorLimite());
            assertEquals(PERIODO_DIFERENTE, orcamentoAtualizado.getPeriodo());
        }

        @Test
        void deveLancarExcecaoQuandoOrcamentoNaoExiste() {
            // Assert
            assertThrows(OrcamentoMensalNotFoundException.class, () -> {
                // Act
                orcamentoMensalService.atualizarOrcamentoMensal(
                        userId, "uuid-inexistente", CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);
            });
        }

        @Test
        void deveLancarExcecaoQuandoTentarAtualizarParaOrcamentoDuplicado() {
            // Arrange
            orcamentoMensalService.criarOrcamentoMensal(
                    userId, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);

            // Assert
            assertThrows(OrcamentoMensalAlreadyExistsException.class, () -> {
                // Act
                orcamentoMensalService.atualizarOrcamentoMensal(
                        userId, orcamentoId, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);
            });
        }

        @Test
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            // Assert
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.atualizarOrcamentoMensal(null, orcamentoId, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);
            });
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.atualizarOrcamentoMensal("", orcamentoId, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);
            });
        }

        @Test
        void deveTestarMetodoValidarParametro() {
            // Assert
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.atualizarOrcamentoMensal(userId, null, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);
            });
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.atualizarOrcamentoMensal(userId, "", CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);
            });
            assertThrows(InvalidDataException.class, () -> {
                // Act
                orcamentoMensalService.atualizarOrcamentoMensal(userId, orcamentoId, CATEGORIA_PADRAO, null, PERIODO_DIFERENTE);
            });
            assertThrows(InvalidDataException.class, () -> {
                // Act
                orcamentoMensalService.atualizarOrcamentoMensal(userId, orcamentoId, CATEGORIA_PADRAO, VALOR_ATUALIZADO, null);
            });
            assertThrows(InvalidDataException.class, () -> {
                // Act
                orcamentoMensalService.atualizarOrcamentoMensal(userId, orcamentoId, null, VALOR_ATUALIZADO, PERIODO_DIFERENTE);
            });
            assertThrows(InvalidDataException.class, () -> {
                // Act
                orcamentoMensalService.atualizarOrcamentoMensal(userId, orcamentoId, "", VALOR_ATUALIZADO, PERIODO_DIFERENTE);
            });
        }
    }

    @Nested
    class DeletarOrcamentoMensal {
        private String orcamentoId;

        @BeforeEach
        void setUp() {
            OrcamentoMensalEntity orcamento = orcamentoMensalService.criarOrcamentoMensal(
                    userId, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);
            orcamentoId = orcamento.getUuid();
        }

        @Test
        void deveDeletarOrcamentoComSucesso() {
            // Act
            orcamentoMensalService.excluirOrcamentoMensal(userId, orcamentoId);

            // Assert
            assertThrows(OrcamentoMensalNotFoundException.class, () -> {
                orcamentoMensalService.buscarPorId(userId, orcamentoId);
            });
        }

        @Test
        void deveLancarExcecaoQuandoOrcamentoNaoExiste() {
            // Assert
            assertThrows(OrcamentoMensalNotFoundException.class, () -> {
                // Act
                orcamentoMensalService.excluirOrcamentoMensal(userId, "uuid-inexistente");
            });
        }

        @Test
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            // Assert
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.excluirOrcamentoMensal(null, orcamentoId);
            });
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.excluirOrcamentoMensal("", orcamentoId);
            });
        }

        @Test
        void deveLancarExcecaoQuandoUuidForNuloOuVazio() {
            // Assert
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.excluirOrcamentoMensal(userId, null);
            });
            assertThrows(InvalidUuidException.class, () -> {
                // Act
                orcamentoMensalService.excluirOrcamentoMensal(userId, "");
            });
        }
    }

    @Nested
    class DuplicacaoOrcamentoTest {
        private String orcamentoId;

        @BeforeEach
        void setUp() {
            // Cria um orçamento inicial para os testes de duplicação
            OrcamentoMensalEntity orcamento = orcamentoMensalService.criarOrcamentoMensal(
                    userId, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_PADRAO);
            orcamentoId = orcamento.getUuid();
        }

        @Test
        void deveLancarExcecaoAoCriarOrcamentoDuplicado() {
            // Assert
            assertThrows(OrcamentoMensalAlreadyExistsException.class, () -> {
                // Tentativa de criar outro orçamento com mesma categoria e período
                orcamentoMensalService.criarOrcamentoMensal(
                        userId, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_PADRAO);
            });
        }

        @Test
        void devePermitirAtualizarOrcamentoSemDuplicacao() {
            // Act (deve passar pois está atualizando o mesmo orçamento)
            OrcamentoMensalEntity atualizado = orcamentoMensalService.atualizarOrcamentoMensal(
                    userId, orcamentoId, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_PADRAO);

            // Assert
            assertEquals(VALOR_ATUALIZADO, atualizado.getValorLimite());
        }

        @Test
        void deveLancarExcecaoAoAtualizarParaCategoriaEPeriodoDuplicado() {
            // Arrange - cria um segundo orçamento
            orcamentoMensalService.criarOrcamentoMensal(
                    userId, CATEGORIA_PADRAO, VALOR_PADRAO, PERIODO_DIFERENTE);

            // Assert
            assertThrows(OrcamentoMensalAlreadyExistsException.class, () -> {
                // Tenta atualizar o segundo orçamento para mesma categoria/periodo do primeiro
                orcamentoMensalService.atualizarOrcamentoMensal(
                        userId, orcamentoId, CATEGORIA_PADRAO, VALOR_ATUALIZADO, PERIODO_DIFERENTE);
            });
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

    private void criarCategoriaTest(String nome, UserEntity user) {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNome(nome);
        categoria.setTipo(CategoriaType.RECEITAS);
        categoria.setUser(user);
        categoriaRepository.save(categoria);
    }
}