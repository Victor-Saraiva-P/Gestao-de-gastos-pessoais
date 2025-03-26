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
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.AuthService;
import br.com.gestorfinanceiro.services.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CategoriaServiceIntegrationTest {
    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAll();
        userRepository.deleteAll();
    }

    // Teste para ver se o AdminService foi carregado
    @Test
    void deveCarregarAuthService() {
        assertNotNull(categoriaService, "O CategoriaService não deveria ser nulo!");
    }

    //------------------TESTES DO criarCategoria ----------------------//
    @Test
    void deveCriarCategoria() {
        UserEntity user = adicionarUsuario("Usuario A");
        CategoriaCreateDTO categoriaDto = TestDataUtil.criarCategoriaCreateDTOUtil("Categoria A", "DESPESAS");
        CategoriaEntity categoriaCriada = categoriaService.criarCategoria(categoriaDto, user.getUuid());

        assertEquals("Categoria A", categoriaCriada.getNome());
        assertEquals(user, categoriaCriada.getUser());
        assertEquals("DESPESAS", categoriaCriada.getTipo()
                .name());

    }

    @Test
    void deveLancarExcecaoQuandoCategoriaForNula() {
        String userId = adicionarUsuario("Usuario A").getUuid();

        assertThrows(InvalidDataException.class, () -> categoriaService.criarCategoria(null, userId));
    }

    @Test
    void deveLancarExcecaoQuandoNomeCategoriaForVazio() {
        String userId = adicionarUsuario("Usuario A").getUuid();
        CategoriaCreateDTO categoriaDto = TestDataUtil.criarCategoriaCreateDTOUtil("", "DESPESAS");

        assertThrows(InvalidDataException.class, () -> categoriaService.criarCategoria(categoriaDto, userId));
    }

    @Test
    void deveLancarExcecaoQuandoNomeCategoriaForNulo() {
        String userId = adicionarUsuario("Usuario A").getUuid();
        CategoriaCreateDTO categoriaDto = TestDataUtil.criarCategoriaCreateDTOUtil(null, "DESPESAS");

        assertThrows(InvalidDataException.class, () -> categoriaService.criarCategoria(categoriaDto, userId));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        CategoriaCreateDTO categoriaDto = TestDataUtil.criarCategoriaCreateDTOUtil("Categoria A", "DESPESAS");

        assertThrows(UserNotFoundException.class,
                () -> categoriaService.criarCategoria(categoriaDto, "id-que-nao-existe"));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaJaExistir() {
        String userId = adicionarUsuario("Usuario A").getUuid();
        CategoriaCreateDTO categoriaDto = TestDataUtil.criarCategoriaCreateDTOUtil("Categoria A", "DESPESAS");

        // Salva uma primeira vez
        categoriaService.criarCategoria(categoriaDto, userId);

        // Tenta salvar a mesma categoria
        assertThrows(CategoriaAlreadyExistsException.class,
                () -> categoriaService.criarCategoria(categoriaDto, userId));
    }

    @Test
    void deveCriarDuasCategoriasComMesmoNomeMasDiferentesTipos() {
        UserEntity user = adicionarUsuario("Usuario A");
        CategoriaCreateDTO despesaA = TestDataUtil.criarCategoriaCreateDTOUtil("Categoria A", "DESPESAS");
        CategoriaCreateDTO receitaA = TestDataUtil.criarCategoriaCreateDTOUtil("Categoria A", "RECEITAS");

        categoriaService.criarCategoria(despesaA, user.getUuid());
        categoriaService.criarCategoria(receitaA, user.getUuid());

        assertEquals(2, categoriaRepository.findAllByUserUuid(user.getUuid())
                .size());
        assertEquals("Categoria A", categoriaRepository.findAll()
                .get(0)
                .getNome());
        assertEquals("Categoria A", categoriaRepository.findAll()
                .get(1)
                .getNome());
    }

    //------------------TESTES DO listarCategoriasUsuario ----------------------//
    @Test
    void deveListarCategorias() {
        UserEntity user = adicionarUsuario("Usuario A");
        adicionarCategoria("Categoria A", "DESPESAS", user.getUuid());

        List<CategoriaEntity> categorias = categoriaService.listarCategorias(user.getUuid());

        assertEquals(1, categorias.size());
        CategoriaEntity categoriaRetornada = categorias.get(0);
        assertEquals("Categoria A", categoriaRetornada.getNome());
        assertEquals("DESPESAS", categoriaRetornada.getTipo()
                .name());
        assertEquals(user, categoriaRetornada.getUser());
    }

    @Test
    void deveListarCategoriasVaziaQuandoNaoHouverCategorias() {
        UserEntity user = adicionarUsuario("Usuario A");

        assertEquals(java.util.List.of(), categoriaService.listarCategorias(user.getUuid()));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistirAoListarCategorias() {
        assertThrows(UserNotFoundException.class, () -> categoriaService.listarCategorias("id-invalido"));
    }

    //------------------TESTES DO atualizarCategoria ----------------------//
    @Test
    void deveAtualizarCategoria() {
        UserEntity user = adicionarUsuario("Usuario A");
        CategoriaEntity categoria = adicionarCategoria("Categoria A", "DESPESAS", user.getUuid());

        CategoriaEntity categoriaAtualizada = categoriaService.atualizarCategoria(categoria.getUuid(),
                TestDataUtil.criarCategoriaUpdateDTOUtil("Categoria B"), user.getUuid());

        assertEquals("Categoria B", categoriaAtualizada.getNome());
        assertEquals("DESPESAS", categoriaAtualizada.getTipo()
                .name());
        assertEquals(user, categoriaAtualizada.getUser());
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaInexistente() {
        assertThrows(CategoriaIdNotFoundException.class,
                () -> categoriaService.atualizarCategoria("uuid-inexistente",
                        TestDataUtil.criarCategoriaUpdateDTOUtil("Qualquer Nome"), "id-de-usuario-qualquer"));
    }

    @Test
    void deveLancarExcecaoQuandoNovoNomeCategoriaAtualizarForVazio() {
        String userId = adicionarUsuario("Usuario A").getUuid();
        String categoriaId = adicionarCategoria("Categoria A", "DESPESAS", userId).getUuid();

        CategoriaUpdateDTO categoriaVazia = TestDataUtil.criarCategoriaUpdateDTOUtil("");
        assertThrows(InvalidDataException.class, () -> categoriaService.atualizarCategoria(categoriaId,
                categoriaVazia, userId));
    }

    @Test
    void deveLancarExcecaoQuandoNovoNomeCategoriaAtualizarForNulo() {
        String userId = adicionarUsuario("Usuario A").getUuid();
        String categoriaId = adicionarCategoria("Categoria A", "DESPESAS", userId).getUuid();

        CategoriaUpdateDTO categoriaNula = TestDataUtil.criarCategoriaUpdateDTOUtil(null);
        assertThrows(InvalidDataException.class, () -> categoriaService.atualizarCategoria(categoriaId,
                categoriaNula, userId));
    }

    //------------------TESTES DO excluirCategoria ----------------------//
    @Test
    void deveExcluirCategoria() {
        UserEntity user = adicionarUsuario("Usuario A");
        CategoriaEntity categoria = adicionarCategoria("Categoria A", "DESPESAS", user.getUuid());

        categoriaService.excluirCategoria(categoria.getUuid(), user.getUuid());

        assertEquals(0, categoriaRepository.findAll()
                .size());
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaIdForNuloAoExcluir() {
        assertThrows(InvalidDataException.class, () -> categoriaService.excluirCategoria(null, "id-de-usuario-qualquer"));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaIdForVazioAoExcluir() {
        assertThrows(InvalidDataException.class, () -> categoriaService.excluirCategoria("", "id-de-usuario-qualquer"));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaInexistenteAoExcluir() {
        assertThrows(CategoriaIdNotFoundException.class,
                () -> categoriaService.excluirCategoria("uuid-inexistente", "id-de-usuario-qualquer"));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaForSemCategoriaAoExcluir() {
        UserEntity user = adicionarUsuario("Usuario A");
        CategoriaEntity categoria = adicionarCategoria("Sem categoria", "DESPESAS", user.getUuid());

        // Setar a categoria como sem categoria
        categoria.setSemCategoria(true);
        categoriaRepository.save(categoria);

        assertThrows(br.com.gestorfinanceiro.exceptions.categoria.CategoriaOperationException.class,
                () -> categoriaService.excluirCategoria(categoria.getUuid(), user.getUuid()));
    }

    //-------------------------------MÉTODOS AUXILIARES-------------------------------//
    public CategoriaEntity adicionarCategoria(String nome, String tipo, String userId) {
        CategoriaCreateDTO categoriaDto = TestDataUtil.criarCategoriaCreateDTOUtil(nome, tipo);
        return categoriaService.criarCategoria(categoriaDto, userId);
    }

    public UserEntity adicionarUsuario(String nome) {
        UserEntity user = TestDataUtil.criarUsuarioEntityUtil(nome);

        authService.register(user);

        return user;
    }
}
