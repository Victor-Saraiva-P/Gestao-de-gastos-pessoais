package br.com.gestorfinanceiro.dto;


import jakarta.validation.constraints.*;

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
    @Pattern(regexp = "^(USER|ADMIN)$", message = "A role deve ser USER ou ADMIN")
    // role é colocada como string para facilitar a validação
    private String role;

    public UserDTO() {
    }

    public UserDTO(String name, String email, String password, String role) {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
