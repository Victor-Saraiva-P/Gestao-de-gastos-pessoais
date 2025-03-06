    package br.com.gestorfinanceiro.models;

    import br.com.gestorfinanceiro.models.enums.ReceitaCategorias;
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
        private ReceitaCategorias categoria;

        @Column(nullable = false, precision = 19, scale = 4)
        private BigDecimal valor;


        @Column(nullable = false)
        private String origemDoPagamento;

        @Column(nullable = false, columnDefinition = "TEXT")
        private String observacoes;

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

        public ReceitaCategorias getCategoria() {
            return categoria;
        }

        public void setCategoria(ReceitaCategorias categoria) {
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
    }
