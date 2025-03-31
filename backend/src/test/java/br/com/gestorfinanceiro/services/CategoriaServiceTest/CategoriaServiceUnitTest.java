package br.com.gestorfinanceiro.services.CategoriaServiceTest;

import br.com.gestorfinanceiro.TestDataUtil;
import br.com.gestorfinanceiro.dto.categoria.CategoriaCreateDTO;
import br.com.gestorfinanceiro.dto.categoria.CategoriaUpdateDTO;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaAlreadyExistsException;
import br.com.gestorfinanceiro.exceptions.categoria.CategoriaIdNotFoundException;
import br.com.gestorfinanceiro.exceptions.common.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.user.UserNotFoundException;
import br.com.gestorfinanceiro.models.CategoriaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.repositories.CategoriaRepository;
import br.com.gestorfinanceiro.repositories.DespesaRepository;
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
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}
