package br.com.gestorfinanceiro.exceptions;

import br.com.gestorfinanceiro.exceptions.auth.login.EmailNotFoundException;
import br.com.gestorfinanceiro.exceptions.auth.login.InvalidPasswordException;
import br.com.gestorfinanceiro.exceptions.auth.register.EmailAlreadyExistsException;
import br.com.gestorfinanceiro.exceptions.auth.register.UsernameAlreadyExistsException;
import br.com.gestorfinanceiro.exceptions.despesa.DespesaNotFoundException;
import br.com.gestorfinanceiro.exceptions.despesa.DespesaOperationException;
import br.com.gestorfinanceiro.exceptions.receita.ReceitaNotFoundException;
import br.com.gestorfinanceiro.exceptions.receita.ReceitaOperationException;
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

    private static final Logger customLogger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_DETAIL_FIELD = "Detalhes do erro";
    private static final String DEFAULT_ERROR_MESSAGE = "Erro desconhecido";

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

    // Handler para quando a receita não for encontrada
    @ExceptionHandler(ReceitaNotFoundException.class)
    public ResponseEntity<ApiError> handleReceitaNotFoundException(ReceitaNotFoundException ex) {
        logException("Receita não encontrada", ex);

        // Criando um Map para adicionar detalhes ao campo errors
        Map<String, String> errors = Map.of(
                ERROR_DETAIL_FIELD, "Não há receitas para este usuário ou o usuário não existe."
        );

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), errors);
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    // Handler para erros operacionais relacionados à receita
    @ExceptionHandler(ReceitaOperationException.class)
    public ResponseEntity<ApiError> handleReceitaOperationException(ReceitaOperationException ex) {
        logException("Erro na operação com receita", ex);

        Map<String, String> errors = Map.of(
                ERROR_DETAIL_FIELD, ex.getCause() != null ? ex.getCause().getMessage() : DEFAULT_ERROR_MESSAGE
        );

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Erro ao atualizar receita. Por favor, tente novamente.", errors);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

        // Handler para quando a despesa não for encontrada
    @ExceptionHandler(DespesaNotFoundException.class)
    public ResponseEntity<ApiError> handleDespesaNotFoundException(DespesaNotFoundException ex) {
        logException("Despesa não encontrada", ex);

        Map<String, String> errors = Map.of(
                ERROR_DETAIL_FIELD, "Não há despesas para este usuário ou o UUID fornecido é inválido."
        );

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), errors);
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    // Handler para erros operacionais relacionados à despesa
    @ExceptionHandler(DespesaOperationException.class)
    public ResponseEntity<ApiError> handleDespesaOperationException(DespesaOperationException ex) {
        logException("Erro na operação com despesa", ex);

        Map<String, String> errors = Map.of(
                ERROR_DETAIL_FIELD, ex.getCause() != null ? ex.getCause().getMessage() : DEFAULT_ERROR_MESSAGE
        );

        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao processar a operação na despesa. Por favor, tente novamente.", errors);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
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
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse(DEFAULT_ERROR_MESSAGE),
                        (existing, replacement) -> existing // Se houver duplicatas, mantém a primeira mensagem
                ));

        // Criando o objeto ApiError com a mensagem formatada
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Houve um erro com os dados inseridos", errors);

        return ResponseEntity.badRequest().body(apiError);
    }

    // Metodo auxiliar para logar a exceção
    private void logException(String messagePrefix, Exception ex) {
        // Mensagem sempre exibida no nível ERROR (sem stack trace)
        customLogger.error("{}: {}", messagePrefix, ex.getMessage());

        // O stack trace só será mostrado se o nível de log for DEBUG
        if (customLogger.isDebugEnabled()) {
            customLogger.debug("Detalhes da exceção:", ex);
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