package br.com.gestorfinanceiro.dto;

import jakarta.validation.constraints.NotNull;

public class EstaAtivoDTO {
    @NotNull(message = "A estaAtivo é obrigatória.")
    private Boolean estaAtivo;

    public Boolean getEstaAtivo() {
        return estaAtivo;
    }

    public void setEstaAtivo(Boolean estaAtivo) {
        this.estaAtivo = estaAtivo;
    }
}
