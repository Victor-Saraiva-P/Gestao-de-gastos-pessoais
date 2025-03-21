package br.com.gestorfinanceiro.dto;

import br.com.gestorfinanceiro.models.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class StatusUserDTO {
    @NotBlank(message = "É necessário definir o status")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "O status deve ser ACTIVE ou INACTIVE")
    private String status;

    public String getStatus() {
        return status;
    }

    public Status getStatusEnum() {
        return Status.valueOf(status);
    }
}
