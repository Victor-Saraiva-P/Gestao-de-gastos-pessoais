package br.com.gestorfinanceiro.dto.despesa;

import br.com.gestorfinanceiro.dto.common.TransacaoDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class DespesaDTO extends TransacaoDTO {
    @NotBlank(message = "O destino do pagamento é obrigatória.")
    private String destinoPagamento;

    // Sobrescreva para aplicar validações específicas
    @Override
    @NotBlank(message = "A categoria é obrigatória.")
    @Pattern(
            regexp = "ALIMENTACAO|MORADIA|TRANSPORTE|LAZER",
            message = "Categoria inválida. Valores permitidos: ALIMENTACAO, MORADIA, TRANSPORTE, LAZER."
    )
    public String getCategoria() {
        return super.getCategoria();
    }

    public String getDestinoPagamento() {
        return destinoPagamento;
    }

    public void setDestinoPagamento(String destinoPagamento) {
        this.destinoPagamento = destinoPagamento;
    }
}