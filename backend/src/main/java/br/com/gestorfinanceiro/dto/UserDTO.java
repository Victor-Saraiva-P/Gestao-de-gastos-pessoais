package br.com.gestorfinanceiro.dto;


import br.com.gestorfinanceiro.models.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDTO {
    
    @NotBlank(message = "O username é obrigatório.")
    private String username;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato incorreto de e-mail. Formato válido: usuario@dominio.com")
    private String email;

    @NotBlank(message = "É necessário definir a senha.")
    @Size(min = 6, message = "A senha deve possuir pelo menos 6 caracteres")
    private String password;

    @NotNull(message = "É necessário definir a role.")
    private Roles role;

    public UserDTO() {
    }

    public UserDTO(String name, String email, String password, Roles role) {
        this.username = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }
}
