package br.com.gestorfinanceiro.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Metodo auxiliar para logar a exceção
    private void logException(String messagePrefix, Exception ex) {
        // Mensagem sempre exibida no nível ERROR (sem stack trace)
        logger.error("{}: {}", messagePrefix, ex.getMessage());

        // O stack trace só será mostrado se o nível de log for DEBUG
        if (logger.isDebugEnabled()) {
            logger.debug("Detalhes da exceção:", ex);
        }
    }

    // Metodo auxiliar para construir o ResponseEntity com ApiError
    private ResponseEntity<ApiError> buildErrorResponse(HttpStatus status, String message) {
        // Cria um objeto ApiError com a mensagem de erro e o status HTTP
        ApiError apiError = new ApiError(status, message);
        return new ResponseEntity<>(apiError, status);
    }

    // Handler para erros internos inesperados do sistema que não possuem tratamento específico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        logException("Erro genérico", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}