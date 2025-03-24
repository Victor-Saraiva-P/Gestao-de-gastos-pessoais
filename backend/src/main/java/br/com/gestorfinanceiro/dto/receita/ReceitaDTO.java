package br.com.gestorfinanceiro.dto.receita;

import br.com.gestorfinanceiro.dto.common.TransacaoDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ReceitaDTO extends TransacaoDTO {
    @NotBlank(message = "A origem do pagamento é obrigatória.")
    private String origemDoPagamento;

    // Sobrescreva para aplicar validações específicas
    @Override
    @NotBlank(message = "A categoria é obrigatória.")
    @Pattern(
            regexp = "SALARIO|RENDIMENTO_DE_INVESTIMENTO|COMISSOES|BONUS|BOLSA_DE_ESTUDOS",
            message = "Categoria inválida. Valores permitidos: SALARIO, RENDIMENTO_DE_INVESTIMENTO, COMISSOES, BONUS, BOLSA_DE_ESTUDOS."
    )
    public String getCategoria() {
        return super.getCategoria();
    }

    public String getOrigemDoPagamento() {
        return origemDoPagamento;
    }

    public void setOrigemDoPagamento(String origemDoPagamento) {
        this.origemDoPagamento = origemDoPagamento;
    }
}