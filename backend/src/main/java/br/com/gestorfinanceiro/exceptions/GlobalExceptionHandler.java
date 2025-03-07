package br.com.gestorfinanceiro.exceptions;

import br.com.gestorfinanceiro.exceptions.auth.login.EmailNotFoundException;
import br.com.gestorfinanceiro.exceptions.auth.login.InvalidPasswordException;
import br.com.gestorfinanceiro.exceptions.auth.register.EmailAlreadyExistsException;
import br.com.gestorfinanceiro.exceptions.auth.register.UsernameAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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

    // Handler para erros de duplicação de dados no register (ex: e-mail ou username já cadastrados)
    @ExceptionHandler({EmailAlreadyExistsException.class, UsernameAlreadyExistsException.class})
    public ResponseEntity<ApiError> handleDuplicateDataException(RuntimeException ex) {
        logException("Erro de duplicação de dados", ex);
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Handler para erros de parse de JSON na requisição (ex: JSON malformado, formato de data inválido, tipos incompatíveis)
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        String mensagem = "Formato de JSON inválido";
        String logMessage = "Erro de parse JSON";

        // DateTimeParseException
        if (containsCause(ex, DateTimeParseException.class)) {
            logMessage = "Erro de formatação de data";
            mensagem = "Formato de data inválido. Use o padrão YYYY-MM-DD";
        }

        logException(logMessage, ex);
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, mensagem);
        return new ResponseEntity<>(apiError, headers, HttpStatus.BAD_REQUEST);
    }

    // Handler para erros de validação dos campos de entrada
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        // Mapeando os erros de validação (campo -≥ mensagem de erro)
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Erro desconhecido"),
                        (existing, replacement) -> existing // Se houver duplicatas, mantém a primeira mensagem
                ));

        // Criando o objeto ApiError com a mensagem formatada
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Houve um erro com os dados inseridos", errors);

        return ResponseEntity.badRequest().body(apiError);
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

    // Metodo auxiliar para construir o ResponseEntity com ApiError
    private ResponseEntity<ApiError> buildErrorResponse(HttpStatus status, String message) {
        // Cria um objeto ApiError com a mensagem de erro e o status HTTP
        ApiError apiError = new ApiError(status, message);
        return new ResponseEntity<>(apiError, status);
    }

    // Metodo auxiliar para verificar se a exceção ou suas causas contém uma determinada classe
    private boolean containsCause(Throwable ex, Class<?> causeClass) {
        while (ex != null) {
            if (causeClass.isInstance(ex)) {
                return true;
            }
            ex = ex.getCause();
        }
        return false;
    }

}