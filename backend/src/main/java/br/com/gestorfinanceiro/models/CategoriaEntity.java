package br.com.gestorfinanceiro.models;

import br.com.gestorfinanceiro.models.enums.CategoriaType;
import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class CategoriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoriaType tipo;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private boolean isSemCategoria = false;

    // Construtores

    public CategoriaEntity(String nome, String tipo, UserEntity user, String uuid) {
        this.nome = nome;
        this.tipo = CategoriaType.valueOf(tipo);
        this.user = user;
        this.uuid = uuid;
    }

    public CategoriaEntity(String nome, CategoriaType tipo, UserEntity user) {
        this.nome = nome;
        this.tipo = tipo;
        this.user = user;
    }

    public CategoriaEntity(String nome, String tipo) {
        this.nome = nome;
        this.tipo = CategoriaType.valueOf(tipo);
    }

    public CategoriaEntity(String uuid, String nome, CategoriaType tipo, UserEntity user, boolean isDefault, boolean isRemovable) {
        this.uuid = uuid;
        this.nome = nome;
        this.tipo = tipo;
        this.user = user;
        this.isSemCategoria = isDefault;
    }

    public CategoriaEntity() {
    }

    // Getters and Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public CategoriaType getTipo() {
        return tipo;
    }

    public void setTipo(CategoriaType tipo) {
        this.tipo = tipo;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public boolean isSemCategoria() {
        return isSemCategoria;
    }

    public void setSemCategoria(boolean semCategoria) {
        isSemCategoria = semCategoria;
    }
}
