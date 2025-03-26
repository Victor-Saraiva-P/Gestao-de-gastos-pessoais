package br.com.gestorfinanceiro.models;

import br.com.gestorfinanceiro.models.enums.ReceitasCategorias;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "receitas")
public class ReceitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @Column(nullable = false)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReceitasCategorias categoria;

    @ManyToOne
    @JoinColumn(name = "categoria_customizada_id")
    private CategoriaEntity categoriaCustomizada;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;


    @Column(nullable = false)
    private String origemDoPagamento;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String observacoes;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Construtores
    public ReceitaEntity(LocalDate data, ReceitasCategorias categoria, CategoriaEntity categoriaCustomizada, BigDecimal valor, String origemDoPagamento, String observacoes, UserEntity user) {
        this.uuid = uuid;
        this.data = data;
        this.categoria = categoria;
        this.categoriaCustomizada = categoriaCustomizada;
        this.valor = valor;
        this.origemDoPagamento = origemDoPagamento;
        this.observacoes = observacoes;
        this.user = user;
    }

    public ReceitaEntity() {
    }

    // Getters and Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public ReceitasCategorias getCategoria() {
        return categoria;
    }

    public void setCategoria(ReceitasCategorias categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getOrigemDoPagamento() {
        return origemDoPagamento;
    }

    public void setOrigemDoPagamento(String origemDoPagamento) {
        this.origemDoPagamento = origemDoPagamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public CategoriaEntity getCategoriaCustomizada() {
        return categoriaCustomizada;
    }

    public void setCategoriaCustomizada(CategoriaEntity categoriaCustomizada) {
        this.categoriaCustomizada = categoriaCustomizada;
    }
}
