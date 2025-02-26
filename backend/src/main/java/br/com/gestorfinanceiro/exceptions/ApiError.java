package br.com.gestorfinanceiro.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> errors // Novo campo para armazenar detalhes dos erros
) {
    public ApiError(HttpStatus status, String message) {
        this(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, null);
    }

    public ApiError(HttpStatus status, String message, Map<String, String> errors) {
        this(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, errors);
    }

    public String toJson() {
        return """
                {
                    "timestamp": "%s",
                    "status": %d,
                    "error": "%s",
                    "message": "%s",
                    "errors": %s
                }
                """.formatted(this.timestamp, this.status, this.error, this.message, this.errors);
    }
}
