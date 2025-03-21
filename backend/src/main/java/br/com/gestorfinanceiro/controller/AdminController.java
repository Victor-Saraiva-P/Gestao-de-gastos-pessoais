package br.com.gestorfinanceiro.controller;

import br.com.gestorfinanceiro.dto.StatusUserDTO;
import br.com.gestorfinanceiro.dto.UserForAdminDTO;
import br.com.gestorfinanceiro.mappers.Mapper;
import br.com.gestorfinanceiro.models.UserEntity;
import br.com.gestorfinanceiro.services.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    private final Mapper<UserEntity, UserForAdminDTO> userForAdminDTOMapper;

    public AdminController(AdminService adminService, Mapper<UserEntity, UserForAdminDTO> userWithStatusMapper) {
        this.adminService = adminService;
        this.userForAdminDTOMapper = userWithStatusMapper;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserForAdminDTO>> findAllUsers() {
        List<UserEntity> users = adminService.listUsers();

        // converte a lista de UserEntity para UserForAdminDTO
        List<UserForAdminDTO> usersWithStatus = users.stream()
                .map(userForAdminDTOMapper::mapTo)
                .toList();


        return ResponseEntity.ok(usersWithStatus);
    }

    @PatchMapping("/users/{userID}")
    public ResponseEntity<UserEntity> updateUserStatus(@PathVariable String userID, @RequestBody @Valid StatusUserDTO statusDto) {
        UserEntity user = adminService.atualizarUserStatus(userID, statusDto.getStatusEnum());
        return ResponseEntity.ok(user);
    }
}
