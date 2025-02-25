package br.com.gestorfinanceiro.exceptions;

import br.com.gestorfinanceiro.exceptions.auth.login.EmailNotFoundException;
import br.com.gestorfinanceiro.exceptions.auth.login.InvalidPasswordException;
import br.com.gestorfinanceiro.exceptions.auth.register.EmailAlreadyExistsException;
import br.com.gestorfinanceiro.exceptions.auth.register.UsernameAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handler para erros internos inesperados do sistema que não possuem tratamento específico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        logException("Erro genérico", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    // Handler para falhas de autenticação durante o login (e-mail não encontrado ou senha inválida)
    @ExceptionHandler({EmailNotFoundException.class, InvalidPasswordException.class})
    public ResponseEntity<ApiError> handleLoginException(RuntimeException ex) {
        logException("Erro de autenticação", ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
    }

    // Handler para erros de validação de dados no register (campos obrigatórios não preenchidos ou não válidos)
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public ResponseEntity<ApiError> handleBindException(org.springframework.validation.BindException ex) {
        logException("Erro de validação", ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, Objects.requireNonNull(ex.getFieldError()).getDefaultMessage());
    }

    // Handler para erros de duplicação de dados no register (ex: e-mail ou username já cadastrados)
    @ExceptionHandler({EmailAlreadyExistsException.class, UsernameAlreadyExistsException.class})
    public ResponseEntity<ApiError> handleDuplicateDataException(RuntimeException ex) {
        logException("Erro de duplicação de dados", ex);
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }


    // Metodo auxiliar para logar a exceção
    private void logException(String messagePrefix, Exception ex) {
        // Mensagem sempre exibida no nível ERROR (sem stack trace)
        logger.error("{}: {}", messagePrefix, ex.getMessage());

        // O stack trace só será mostrado se o nível de log for DEBUG
        if (logger.isDebugEnabled()) {
            logger.debug("Detalhes da exceção:", ex);
        }
    }

    // METODOS AUXILIARES

    // Metodo auxiliar para construir o ResponseEntity com ApiError
    private ResponseEntity<ApiError> buildErrorResponse(HttpStatus status, String message) {
        // Cria um objeto ApiError com a mensagem de erro e o status HTTP
        ApiError apiError = new ApiError(status, message);
        return new ResponseEntity<>(apiError, status);
    }
}