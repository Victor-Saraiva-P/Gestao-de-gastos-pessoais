package br.com.gestorfinanceiro.services.AdminServiceTest;

import br.com.gestorfinanceiro.TestDataUtil;
import br.com.gestorfinanceiro.dto.user.UserAdminUpdateDTO;
import br.com.gestorfinanceiro.exceptions.user.InvalidUserIdException;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.repositories.UserRepository;
import br.com.gestorfinanceiro.services.impl.AdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceUnitTest {
    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private UserRepository userRepository;

    //------------------TESTES DO LIST USERS ----------------------//
    @Test
    void deveListarUsers() {
        UserEntity userA = TestDataUtil.criarUsuarioEntityUtil("Usuario A");
        UserEntity userB = TestDataUtil.criarUsuarioEntityUtil("Usuario B");
        UserEntity userC = TestDataUtil.criarUsuarioEntityUtil("Usuario C");

        when(userRepository.findAll()).thenReturn(List.of(userA, userB, userC));

        List<UserEntity> users = adminService.listUsers();

        //verifica se a lista de usuários tem 3 usuários
        assertEquals(3, users.size());
        assertEquals("Usuario A", users.get(0).getUsername());
        assertEquals("Usuario B", users.get(1).getUsername());
        assertEquals("Usuario C", users.get(2).getUsername());
    }

    @Test
    void deveListarUsersVazioQuandoNaoTiverUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserEntity> users = adminService.listUsers();

        //verifica se a lista de usuários está vazia
        assertTrue(users.isEmpty());
    }

    //------------------TESTES DO ATUALIZAR USER STATUS ----------------------//
    @Test
    void deveAtualizarUser() {
        UserEntity user = TestDataUtil.criarUsuarioEntityUtil("Usuario A", "123-456");
        user.setEstaAtivo(true);

        UserAdminUpdateDTO updateDTO = new UserAdminUpdateDTO();
        updateDTO.setEstaAtivo(false);
        updateDTO.setRole("ADMIN");

        when(userRepository.findById(user.getUuid())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserEntity userUpdated = adminService.atualizarUser(user.getUuid(), updateDTO);

        // verifica se o usuário foi atualizado
        assertFalse(userUpdated.getEstaAtivo());
    }

    @Test
    void deveLancarInvalidUserIdExceptionQuandoUserIdForNull() {
        UserAdminUpdateDTO updateDTO = new UserAdminUpdateDTO();
        updateDTO.setEstaAtivo(false);

        // verifica se o metodo lança a exceção quando o userId é nulo
        assertThrows(InvalidUserIdException.class, () -> adminService.atualizarUser(null, updateDTO));
    }

    @Test
    void deveLancarInvalidUserIdExceptionQuandoUserIdForVazio() {
        UserAdminUpdateDTO updateDTO = new UserAdminUpdateDTO();
        updateDTO.setEstaAtivo(false);

        // verifica se o metodo lança a exceção quando o userId é vazio
        assertThrows(InvalidUserIdException.class, () -> adminService.atualizarUser("", updateDTO));
    }

    @Test
    void deveLancarUserNotFoundExceptionQuandoNaoEncontrarUser() {
        UserAdminUpdateDTO updateDTO = new UserAdminUpdateDTO();
        updateDTO.setEstaAtivo(false);

        when(userRepository.findById("123-456")).thenReturn(Optional.empty());

        // verifica se o metodo lança a exceção quando o usuário não é encontrado
        assertThrows(Exception.class, () -> adminService.atualizarUser("123-456", updateDTO));
    }
}