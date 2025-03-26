package br.com.gestorfinanceiro.models;

import br.com.gestorfinanceiro.models.enums.DespesasCategorias;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "orcamento_mensal")
public class OrcamentoMensalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DespesasCategorias categoria;

    @Column(nullable = false)
    private BigDecimal valorLimite;

    @Column(nullable = false)
    private YearMonth periodo;

    public OrcamentoMensalEntity() {
    }

    // Getters and Setters

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public DespesasCategorias getCategoria() {
        return categoria;
    }

    public void setCategoria(DespesasCategorias categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getValorLimite() {
        return valorLimite;
    }

    public void setValorLimite(BigDecimal valorLimite) {
        this.valorLimite = valorLimite;
    }

    public YearMonth getPeriodo() {
        return periodo;
    }

    public void setPeriodo(YearMonth periodo) {
        this.periodo = periodo;
    }
}
