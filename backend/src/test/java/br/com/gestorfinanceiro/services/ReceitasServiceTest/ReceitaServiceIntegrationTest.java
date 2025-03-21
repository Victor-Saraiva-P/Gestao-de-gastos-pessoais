package br.com.gestorfinanceiro.services.ReceitasServiceTest;

import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.ReceitasCategorias;
import br.com.gestorfinanceiro.models.enums.Roles;
import br.com.gestorfinanceiro.repositories.ReceitaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.ReceitaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ReceitaServiceIntegrationTest {

    @Autowired
    private ReceitaService receitaService;

    @Autowired
    private ReceitaRepository receitaRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        receitaRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void deveCarregarReceitaService() {
        assertNotNull(receitaService, "O ReceitaService não deveria ser nulo!");
    }

    @Nested
    class CriarReceitaTest {

    }

    @Nested
    class ListarReceitasTest {

    }

    @Nested
    class BuscarReceitaPorIdTest {

    }

    @Nested
    class AtualizarReceitaTest {

    }

    @Nested
    class excluirReceitaTest {

    }

    @Nested
    class gerarGraficoTest {

    }

    @Nested
    class buscaAvancadaTest {

    }

    //----------------- Métodos Auxiliares -----------------//

    private UserEntity criarUsuarioTest() {
        UserEntity user = new UserEntity();
        user.setUsername("Jorge");
        user.setEmail("jorge@gmail.com");
        user.setPassword("123456");
        user.setRole(Roles.USER);
        return userRepository.save(user);
    }

    private ReceitaEntity criarReceitaTest() {
        ReceitaEntity receita = new ReceitaEntity();
        receita.setData(LocalDate.now());
        receita.setValor(BigDecimal.valueOf(100));
        receita.setCategoria(ReceitasCategorias.SALARIO);
        receita.setOrigemDoPagamento("Teste");
        receita.setObservacoes("Teste");
        return receita;
    }

    private void criarReceitaComValorEData(UserEntity user, BigDecimal valor, LocalDate data) {
        ReceitaEntity receita = new ReceitaEntity();
        receita.setData(data);
        receita.setValor(valor);
        receita.setCategoria(ReceitasCategorias.SALARIO);
        receita.setOrigemDoPagamento("Teste");
        receita.setObservacoes("Salario teste");
        receita.setUser(user);
        receitaService.criarReceita(receita, user.getUuid());
    }

    private void criarReceitaComCategoriaEValor(UserEntity user, BigDecimal valor, ReceitasCategorias categoria) {
        ReceitaEntity receita = new ReceitaEntity();
        receita.setData(LocalDate.of(2024, 1, 15));
        receita.setValor(valor);
        receita.setCategoria(categoria);
        receita.setOrigemDoPagamento("Teste");
        receita.setObservacoes("Salario teste");
        receita.setUser(user);
        receitaService.criarReceita(receita, user.getUuid());
    }

}
