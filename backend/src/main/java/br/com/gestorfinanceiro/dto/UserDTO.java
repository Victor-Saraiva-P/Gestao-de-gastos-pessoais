package br.com.gestorfinanceiro.dto;


import br.com.gestorfinanceiro.models.enums.Roles;
public class UserDTO {
    
    private String username;
    private String email;
    private String password;
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
