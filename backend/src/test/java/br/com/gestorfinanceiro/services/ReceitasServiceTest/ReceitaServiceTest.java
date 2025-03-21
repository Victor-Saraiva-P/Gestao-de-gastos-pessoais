package br.com.gestorfinanceiro.services.ReceitasServiceTest;

import br.com.gestorfinanceiro.models.ReceitaEntity;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.models.enums.ReceitasCategorias;
import br.com.gestorfinanceiro.models.enums.Roles;
import br.com.gestorfinanceiro.repositories.ReceitaRepository;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.ReceitaService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReceitaServiceTest {

    @Mock
    private ReceitaRepository receitaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReceitaServiceImpl receitaService;

    private UserEntity user;
    private ReceitaEntity receita;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setUuid(UUID.randomUUID().toString());
        user.setUsername("Jorge");
        user.setEmail("jorge@gmail.com");
        user.setPassword("123456");
        user.setRole(Roles.USER);

        receita = new ReceitaEntity();
        receita.setUuid(UUID.randomUUID().toString());
        receita.setData(LocalDate.now());
        receita.setValor(BigDecimal.valueOf(10000));
        receita.setCategoria(ReceitasCategorias.SALARIO);
        receita.setOrigemDoPagamento("Empresa X");
        receita.setObservacoes("Salário do mês de janeiro");
        receita.setUser(user);
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
}