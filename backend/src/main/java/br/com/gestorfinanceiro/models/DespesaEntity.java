package br.com.gestorfinanceiro.models;

import br.com.gestorfinanceiro.models.enums.DespesasCategorias;
import br.com.gestorfinanceiro.models.enums.ReceitaCategorias;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "despesas")
public class DespesaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DespesasCategorias categoria;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;

    @Column(nullable = false)
    private String destinoPagamento;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String observacoes;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

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

    public DespesasCategorias getCategoria() {
        return categoria;
    }

    public void setCategoria(DespesasCategorias categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDestinoPagamento() {
        return destinoPagamento;
    }

    public void setDestinoPagamento(String destinoPagamento) {
        this.destinoPagamento = destinoPagamento;
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
}
