package br.com.gestorfinanceiro.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UserAdminUpdateDTO {
    @NotNull(message = "A estaAtivo é obrigatória.")
    private Boolean estaAtivo;

    @NotBlank(message = "A Role é obrigatória.")
    @Pattern(regexp = "^(ADMIN|USER)$", message = "A Role deve ser ADMIN ou ROLE_USER.")
    private String Role;

    // Construtores
    public UserAdminUpdateDTO() {
    }

    // Getters and Setters
    public Boolean getEstaAtivo() {
        return estaAtivo;
    }

    public void setEstaAtivo(Boolean estaAtivo) {
        this.estaAtivo = estaAtivo;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }
}
