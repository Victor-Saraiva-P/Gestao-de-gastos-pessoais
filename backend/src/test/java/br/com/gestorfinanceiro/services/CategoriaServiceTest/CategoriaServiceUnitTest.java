package br.com.gestorfinanceiro.services.CategoriaServiceTest;

import br.com.gestorfinanceiro.TestDataUtil;
import br.com.gestorfinanceiro.dto.categoria.CategoriaCreateDTO;
import br.com.gestorfinanceiro.dto.categoria.CategoriaUpdateDTO;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaAcessDeniedException;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaAlreadyExistsException;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaIdNotFoundException;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaOperationException;
import br.com.gestorfinanceiro.exceptions.common.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.user.UserNotFoundException;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.DespesaEntity;
import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.CategoriaType;
import br.com.gestorfinanceiro.repositories.CategoriaRepository;
import br.com.gestorfinanceiro.repositories.DespesaRepository;
import br.com.gestorfinanceiro.repositories.ReceitaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.impl.CategoriaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceUnitTest {
    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DespesaRepository despesaRepository;

    @Mock
    private CategoriaRepository categoriaRepository;
    
    @Mock
    private ReceitaRepository receitaRepository;

    //------------------TESTES DO criarCategoria ----------------------//
    @Test
    void deveCriarCategoria() {
        UserEntity user = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456");
        CategoriaCreateDTO categoriaDto = TestDataUtil.criarCategoriaCreateDTOUtil("Categoria A", "DESPESAS");

        when(userRepository.findById("123-456")).thenReturn(Optional.of(user));
        when(categoriaRepository.findByNomeAndTipoAndUserUuid("Categoria A", categoriaDto.getTipoEnum(),
                user.getUuid())).thenReturn(Optional.empty());
        when(categoriaRepository.save(org.mockito.ArgumentMatchers.any(CategoriaEntity.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        CategoriaEntity categoriaCriada = categoriaService.criarCategoria(categoriaDto, "123-456");

        assertEquals("Categoria A", categoriaCriada.getNome());
        assertEquals(user, categoriaCriada.getUser());
        assertEquals("DESPESAS", categoriaCriada.getTipo()
                .name());
    }

    static Stream<Arguments> providerDadosInvalidos() {
        return Stream.of(Arguments.of("", "DESPESAS", "Nome vazio deve lançar exceção"),
                Arguments.of(null, "DESPESAS", "Nome nulo deve lançar exceção"),
                Arguments.of("Categoria A", "", "Tipo vazio deve lançar exceção"),
                Arguments.of("Categoria A", null, "Tipo nulo deve lançar exceção"));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaForNula() {
        String userId = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456")
                .getUuid();

        assertThrows(InvalidDataException.class, () -> categoriaService.criarCategoria(null, userId));
    }

    @ParameterizedTest
    @MethodSource("providerDadosInvalidos")
    void deveLancarExcecaoQuandoDadosForemInvalidos(String nome, String tipo, String descricaoTeste) {
        String userId = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456")
                .getUuid();
        CategoriaCreateDTO categoriaDto = TestDataUtil.criarCategoriaCreateDTOUtil(nome, tipo);

        assertThrows(InvalidDataException.class, () -> categoriaService.criarCategoria(categoriaDto, userId),
                descricaoTeste);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        String userId = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456")
                .getUuid();
        CategoriaCreateDTO categoriaDto = TestDataUtil.criarCategoriaCreateDTOUtil("Categoria A", "DESPESAS");

        when(userRepository.findById("123-456")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> categoriaService.criarCategoria(categoriaDto, userId));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaJaExistir() {
        UserEntity user = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456");
        String userId = user.getUuid();

        CategoriaCreateDTO categoriaDto = TestDataUtil.criarCategoriaCreateDTOUtil("Categoria A", "DESPESAS");
        CategoriaEntity categoria = TestDataUtil.criarCategoriaEntityUtil("Categoria A", "DESPESAS");

        when(userRepository.findById("123-456")).thenReturn(Optional.of(user));
        when(categoriaRepository.findByNomeAndTipoAndUserUuid("Categoria A", categoriaDto.getTipoEnum(),
                "123-456")).thenReturn(Optional.of(categoria));

        assertThrows(CategoriaAlreadyExistsException.class,
                () -> categoriaService.criarCategoria(categoriaDto, userId));
    }

    //------------------TESTES DO listarCategoriasUsuario ----------------------//
    @Test
    void deveListarCategorias() {
        UserEntity user = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456");
        CategoriaEntity categoria = TestDataUtil.criarCategoriaEntityComUserUtil("Categoria A", "DESPESAS", user);

        when(userRepository.findById(user.getUuid())).thenReturn(Optional.of(user));
        when(categoriaRepository.findAllByUserUuid(user.getUuid())).thenReturn(java.util.List.of(categoria));

        assertEquals(java.util.List.of(categoria), categoriaService.listarCategorias(user.getUuid()));
    }

    @Test
    void deveListarCategoriasVaziaQuandoNaoHouverCategorias() {
        UserEntity user = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456");
        String userId = user.getUuid();

        when(userRepository.findById(user.getUuid())).thenReturn(Optional.of(user));
        when(categoriaRepository.findAllByUserUuid(user.getUuid())).thenReturn(java.util.List.of());

        assertEquals(java.util.List.of(), categoriaService.listarCategorias(userId));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistirAoListarCategorias() {
        when(userRepository.findById("123-456")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> categoriaService.listarCategorias("123-456"));
    }

    //------------------TESTES DO atualizarCategoria ----------------------//
    @Test
    void deveAtualizarCategoria() {
        UserEntity user = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456");
        CategoriaEntity categoria = TestDataUtil.criarCategoriaEntityComUserUtil("Categoria A", "DESPESAS", user);

        when(categoriaRepository.findById(categoria.getUuid())).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(org.mockito.ArgumentMatchers.any(CategoriaEntity.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        CategoriaEntity categoriaAtualizada = categoriaService.atualizarCategoria(categoria.getUuid(),
                TestDataUtil.criarCategoriaUpdateDTOUtil("Categoria B"), user.getUuid());

        assertEquals("Categoria B", categoriaAtualizada.getNome());
        assertEquals("DESPESAS", categoriaAtualizada.getTipo()
                .name());
        assertEquals(user, categoriaAtualizada.getUser());

        org.mockito.Mockito.verify(categoriaRepository)
                .findById(categoria.getUuid());
        org.mockito.Mockito.verify(categoriaRepository)
                .save(categoriaAtualizada);
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaInexistente() {
        when(categoriaRepository.findById("uuid-inexistente")).thenReturn(Optional.empty());
        CategoriaUpdateDTO categoriaQualquer = TestDataUtil.criarCategoriaUpdateDTOUtil("Qualquer Nome");

        assertThrows(CategoriaIdNotFoundException.class,
                () -> categoriaService.atualizarCategoria("uuid-inexistente",
                        categoriaQualquer, "id-usuario-qualquer"));
    }

    @Test
    void deveLancarExcecaoQuandoNovoNomeCategoriaAtualizarForVazio() {
        UserEntity user = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456");
        String categoriaId = TestDataUtil.criarCategoriaEntityComUserUtil("Categoria A", "DESPESAS", user)
                .getUuid();

        CategoriaUpdateDTO categoriaVazia = TestDataUtil.criarCategoriaUpdateDTOUtil("");
        assertThrows(InvalidDataException.class,
                () -> categoriaService.atualizarCategoria(categoriaId, categoriaVazia, "123-456"));
    }

    @Test
    void deveLancarExcecaoQuandoNovoNomeCategoriaAtualizarForNulo() {
        UserEntity user = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456");
        String categoriaId = TestDataUtil.criarCategoriaEntityComUserUtil("Categoria A", "DESPESAS", user)
                .getUuid();

        CategoriaUpdateDTO categoriaNula = TestDataUtil.criarCategoriaUpdateDTOUtil(null);
        assertThrows(InvalidDataException.class,
                () -> categoriaService.atualizarCategoria(categoriaId, categoriaNula, "123-456"));
    }

    //------------------TESTES DO excluirCategoria ----------------------//
    @Test
    void deveExcluirCategoriaComSucesso() {
        UserEntity user = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456");
        CategoriaEntity categoria = TestDataUtil.criarCategoriaEntityComUserUtil("Categoria A", "DESPESAS", user);

        when(userRepository.findById(user.getUuid())).thenReturn(Optional.of(user));
        when(categoriaRepository.findById(categoria.getUuid())).thenReturn(Optional.of(categoria));
        when(despesaRepository.findAllByCategoria(categoria)).thenReturn(Collections.emptyList());
        doNothing().when(categoriaRepository)
                .delete(categoria);

        categoriaService.excluirCategoria(categoria.getUuid(), user.getUuid());

        verify(userRepository).findById(user.getUuid());
        verify(categoriaRepository).findById(categoria.getUuid());
        verify(despesaRepository).findAllByCategoria(categoria);
        verify(categoriaRepository).delete(categoria);
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaIdForNuloAoExcluir() {
        assertThrows(InvalidDataException.class, () -> categoriaService.excluirCategoria(null, "id-usuario-qualquer"));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaIdForVazioAoExcluir() {
        assertThrows(InvalidDataException.class, () -> categoriaService.excluirCategoria("", "id-usuario-qualquer"));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaInexistenteAoExcluir() {
        when(categoriaRepository.findById("uuid-inexistente")).thenReturn(Optional.empty());

        assertThrows(CategoriaIdNotFoundException.class,
                () -> categoriaService.excluirCategoria("uuid-inexistente", "id-usuario-qualquer"));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaForSemCategoriaAoExcluir() {
        UserEntity user = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456");
        CategoriaEntity categoria = TestDataUtil.criarCategoriaEntityComUserUtil("Sem categoria", "DESPESAS", user);
        categoria.setSemCategoria(true);


        when(categoriaRepository.findById(categoria.getUuid())).thenReturn(Optional.of(categoria));

        assertThrows(br.com.gestorfinanceiro.exceptions.categoria.CategoriaOperationException.class,
                () -> categoriaService.excluirCategoria(categoria.getUuid(), "123-456"));
    }

    private final String userId = "user-123";
    private final String categoriaId = "cat-456";
    private final String tipoDespesa = "DESPESAS";
    private final String tipoReceitas = "RECEITAS";
    
    private UserEntity createUser() {
        UserEntity user = new UserEntity();
        user.setUuid(userId);
        return user;
    }
    
    private CategoriaEntity createCategoria(String nome, CategoriaType tipo, boolean semCategoria) {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setUuid(categoriaId);
        categoria.setNome(nome);
        categoria.setTipo(tipo);
        categoria.setUser(createUser());
        categoria.setSemCategoria(semCategoria);
        return categoria;
    }

    @Test
    void atualizarCategoria_DeveRetornarCategoriaAtualizada_QuandoDadosValidos() {
        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO("Novo Nome");
        CategoriaEntity categoriaExistente = createCategoria("Nome Antigo", CategoriaType.DESPESAS, false);
        
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoriaExistente));
        when(categoriaRepository.findByNomeAndTipoAndUserUuid(any(), any(), any())).thenReturn(Optional.empty());
        when(categoriaRepository.save(any())).thenReturn(categoriaExistente);
        
        CategoriaEntity result = categoriaService.atualizarCategoria(categoriaId, updateDTO, userId);
        
        assertEquals("Novo Nome", result.getNome());
        verify(categoriaRepository).findById(categoriaId);
        verify(categoriaRepository).findByNomeAndTipoAndUserUuid("Novo Nome", CategoriaType.DESPESAS, userId);
        verify(categoriaRepository).save(categoriaExistente);
    }

    @Test
    void atualizarCategoria_DeveLancarExcecao_QuandoNomeJaExiste() {
        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO("Nome Existente");
        CategoriaEntity categoriaExistente = createCategoria("Nome Antigo", CategoriaType.DESPESAS, false);
        CategoriaEntity outraCategoria = createCategoria("Nome Existente", CategoriaType.DESPESAS, false);
        
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoriaExistente));
        when(categoriaRepository.findByNomeAndTipoAndUserUuid("Nome Existente", CategoriaType.DESPESAS, userId))
            .thenReturn(Optional.of(outraCategoria));
        
        assertThrows(CategoriaAlreadyExistsException.class, 
            () -> categoriaService.atualizarCategoria(categoriaId, updateDTO, userId));
    }

    @Test
    void excluirCategoria_DeveExcluirERedirecionarDespesas_QuandoCategoriaValida() {
        CategoriaEntity categoriaParaExcluir = createCategoria("Alimentação", CategoriaType.DESPESAS, false);
        CategoriaEntity semCategoria = createCategoria("Sem Categoria", CategoriaType.DESPESAS, true);
        DespesaEntity despesa = new DespesaEntity();
        despesa.setCategoria(categoriaParaExcluir);
        
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoriaParaExcluir));
        when(categoriaRepository.findByIsSemCategoriaAndTipoAndUserUuid(true, CategoriaType.DESPESAS, userId))
            .thenReturn(Optional.of(semCategoria));
        when(despesaRepository.findAllByCategoria(categoriaParaExcluir)).thenReturn(List.of(despesa));
        
        categoriaService.excluirCategoria(categoriaId, userId);
        
        verify(despesaRepository).save(despesa);
        assertEquals(semCategoria, despesa.getCategoria());
        verify(categoriaRepository).delete(categoriaParaExcluir);
    }

    @Test
    void excluirCategoria_DeveLancarExcecao_QuandoTentarExcluirSemCategoria() {
        CategoriaEntity semCategoria = createCategoria("Sem Categoria", CategoriaType.DESPESAS, true);
        
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(semCategoria));
        
        assertThrows(CategoriaOperationException.class, 
            () -> categoriaService.excluirCategoria(categoriaId, userId));
    }

    @Test
    void criarSemCategoria_DeveCriarNovaCategoria_QuandoNaoExistir() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(createUser()));
        when(categoriaRepository.findByIsSemCategoriaAndTipoAndUserUuid(true, CategoriaType.DESPESAS, userId))
            .thenReturn(Optional.empty());
        when(categoriaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        CategoriaEntity result = categoriaService.criarSemCategoria(userId, tipoDespesa);
        
        assertEquals("Sem Categoria", result.getNome());
        assertTrue(result.isSemCategoria());
        assertEquals(CategoriaType.DESPESAS, result.getTipo());
        assertEquals(userId, result.getUser().getUuid());
    }

    @Test
    void criarSemCategoria_DeveLancarExcecao_QuandoJaExistir() {
        CategoriaEntity semCategoriaExistente = createCategoria("Sem Categoria", CategoriaType.DESPESAS, true);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(createUser()));
        when(categoriaRepository.findByIsSemCategoriaAndTipoAndUserUuid(true, CategoriaType.DESPESAS, userId))
            .thenReturn(Optional.of(semCategoriaExistente));
        
        assertThrows(CategoriaAlreadyExistsException.class, 
            () -> categoriaService.criarSemCategoria(userId, tipoDespesa));
    }

    @Test
    void atualizarCategoria_DeveLancarExcecao_QuandoCategoriaUpdateDTONull() {
        CategoriaUpdateDTO updateDTONull = null;
        
        assertThrows(InvalidDataException.class,
            () -> categoriaService.atualizarCategoria(categoriaId, updateDTONull, userId),
            "Passar a categoria é obrigatório.");
    }

    @Test
    void atualizarCategoria_DeveLancarExcecao_QuandoNomeNull() {
        CategoriaUpdateDTO updateDTOComNomeNull = new CategoriaUpdateDTO(null);
        
        assertThrows(InvalidDataException.class,
            () -> categoriaService.atualizarCategoria(categoriaId, updateDTOComNomeNull, userId),
            "O nome é obrigatório.");
    }

    @Test
    void atualizarCategoria_DeveLancarExcecao_QuandoNomeVazio() {
        CategoriaUpdateDTO updateDTOComNomeVazio = new CategoriaUpdateDTO("   ");
        
        assertThrows(InvalidDataException.class,
            () -> categoriaService.atualizarCategoria(categoriaId, updateDTOComNomeVazio, userId),
            "O nome é obrigatório.");
    }

    @Test
    void atualizarCategoria_DeveLancarExcecao_QuandoUsuarioNaoDono() {
        String outroUserId = "outro-user-456";
        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO("Novo Nome");
        CategoriaEntity categoriaExistente = createCategoria("Nome Antigo", CategoriaType.DESPESAS, false);
        
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoriaExistente));
        
        assertThrows(CategoriaAcessDeniedException.class,
            () -> categoriaService.atualizarCategoria(categoriaId, updateDTO, outroUserId));
    }

    @Test
    void excluirCategoria_DeveLancarExcecao_QuandoCategoriaIdNull() {
        String categoriaIdNull = null;
        
        assertThrows(InvalidDataException.class,
            () -> categoriaService.excluirCategoria(categoriaIdNull, userId),
            "O id da categoria é obrigatório.");
    }

    @Test
    void excluirCategoria_DeveLancarExcecao_QuandoCategoriaIdVazio() {
        String categoriaIdVazio = "   ";
        
        assertThrows(InvalidDataException.class,
            () -> categoriaService.excluirCategoria(categoriaIdVazio, userId),
            "O id da categoria é obrigatório.");
    }

    @Test
    void excluirCategoria_DeveLancarExcecao_QuandoUsuarioNaoDono() {
        String outroUserId = "outro-user-456";
        CategoriaEntity categoriaExistente = createCategoria("Alimentação", CategoriaType.DESPESAS, false);
        
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoriaExistente));
        
        assertThrows(CategoriaAcessDeniedException.class,
            () -> categoriaService.excluirCategoria(categoriaId, outroUserId));
    }

    @Test
    void excluirCategoria_DeveLancarExcecao_QuandoCategoriaNaoExiste() {
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());
        
        assertThrows(CategoriaIdNotFoundException.class,
            () -> categoriaService.excluirCategoria(categoriaId, userId));
    }

    @Test
    void criarSemCategoria_DeveLancarExcecao_QuandoTipoNull() {
        String tipoNull = null;
        when(userRepository.findById(userId)).thenReturn(Optional.of(createUser()));
        
        assertThrows(InvalidDataException.class,
            () -> categoriaService.criarSemCategoria(userId, tipoNull),
            "O tipo é obrigatório.");
    }

    @Test
    void criarSemCategoria_DeveLancarExcecao_QuandoTipoVazio() {
        String tipoVazio = "   ";
        when(userRepository.findById(userId)).thenReturn(Optional.of(createUser()));
        
        assertThrows(InvalidDataException.class,
            () -> categoriaService.criarSemCategoria(userId, tipoVazio),
            "O tipo é obrigatório.");
    }

    @Test
    void criarSemCategoria_DeveLancarExcecao_QuandoTipoInvalido() {
        String tipoInvalido = "INVALIDO";
        when(userRepository.findById(userId)).thenReturn(Optional.of(createUser()));
        
        assertThrows(IllegalArgumentException.class,
            () -> categoriaService.criarSemCategoria(userId, tipoInvalido));
    }

    @Test
    void criarSemCategoria_DeveLancarExcecao_QuandoUsuarioNaoExiste() {
        String usuarioInexistente = "usuario-inexistente";
        when(userRepository.findById(usuarioInexistente)).thenReturn(Optional.empty());
        
        assertThrows(UserNotFoundException.class,
            () -> categoriaService.criarSemCategoria(usuarioInexistente, tipoDespesa));
    }

    @Test
    void excluirCategoria_DeveRedirecionarReceitasParaSemCategoria_QuandoTipoReceitas() {
        CategoriaEntity categoriaParaExcluir = createCategoria("Investimentos", CategoriaType.RECEITAS, false);
        CategoriaEntity semCategoria = createCategoria("Sem Categoria", CategoriaType.RECEITAS, true);
        ReceitaEntity receita1 = new ReceitaEntity();
        receita1.setCategoria(categoriaParaExcluir);
        ReceitaEntity receita2 = new ReceitaEntity();
        receita2.setCategoria(categoriaParaExcluir);
        
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoriaParaExcluir));
        when(categoriaRepository.findByIsSemCategoriaAndTipoAndUserUuid(true, CategoriaType.RECEITAS, userId))
            .thenReturn(Optional.of(semCategoria));
        when(receitaRepository.findAllByCategoria(categoriaParaExcluir)).thenReturn(List.of(receita1, receita2));
        
        categoriaService.excluirCategoria(categoriaId, userId);
        
        assertEquals(semCategoria, receita1.getCategoria());
        assertEquals(semCategoria, receita2.getCategoria());
        verify(receitaRepository, times(2)).save(any(ReceitaEntity.class));
        verify(categoriaRepository).delete(categoriaParaExcluir);
    }

    @Test
    void excluirCategoria_NaoDeveLancarErro_QuandoNaoHaReceitasAssociadas() {
        CategoriaEntity categoriaParaExcluir = createCategoria("Investimentos", CategoriaType.RECEITAS, false);
        CategoriaEntity semCategoria = createCategoria("Sem Categoria", CategoriaType.RECEITAS, true);
        
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoriaParaExcluir));
        when(categoriaRepository.findByIsSemCategoriaAndTipoAndUserUuid(true, CategoriaType.RECEITAS, userId))
            .thenReturn(Optional.of(semCategoria));
        when(receitaRepository.findAllByCategoria(categoriaParaExcluir)).thenReturn(List.of());
        
        assertDoesNotThrow(() -> categoriaService.excluirCategoria(categoriaId, userId));
        verify(categoriaRepository).delete(categoriaParaExcluir);
    }

    @Test
    void excluirCategoria_DeveLancarExcecao_QuandoErroAoAtualizarReceitas() {
        CategoriaEntity categoriaParaExcluir = createCategoria("Investimentos", CategoriaType.RECEITAS, false);
        CategoriaEntity semCategoria = createCategoria("Sem Categoria", CategoriaType.RECEITAS, true);
        ReceitaEntity receita = new ReceitaEntity();
        receita.setCategoria(categoriaParaExcluir);
        
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoriaParaExcluir));
        when(categoriaRepository.findByIsSemCategoriaAndTipoAndUserUuid(true, CategoriaType.RECEITAS, userId))
            .thenReturn(Optional.of(semCategoria));
        when(receitaRepository.findAllByCategoria(categoriaParaExcluir)).thenReturn(List.of(receita));
        doThrow(new RuntimeException("Erro ao salvar receita")).when(receitaRepository).save(receita);
        
        CategoriaOperationException exception = assertThrows(CategoriaOperationException.class,
            () -> categoriaService.excluirCategoria(categoriaId, userId));
        
        assertTrue(exception.getMessage().contains("Erro ao excluir categoria"));
    }
}
