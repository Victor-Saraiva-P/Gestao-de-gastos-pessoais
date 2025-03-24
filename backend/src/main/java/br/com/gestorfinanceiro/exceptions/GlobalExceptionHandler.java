package br.com.gestorfinanceiro.exceptions;

import br.com.gestorfinanceiro.exceptions.despesa.DespesaNotFoundException;
import br.com.gestorfinanceiro.exceptions.despesa.DespesaOperationException;
import br.com.gestorfinanceiro.exceptions.generalExceptions.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.generalExceptions.InvalidUuidException;
import br.com.gestorfinanceiro.exceptions.receita.ReceitaNotFoundException;
import br.com.gestorfinanceiro.exceptions.receita.ReceitaOperationException;
import br.com.gestorfinanceiro.exceptions.user.*;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;


@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ----------------------------------------
    // EXCEÇÕES GENÉRICAS DO SISTEMA
    // ----------------------------------------

    // Handler para erros internos inesperados do sistema que não possuem tratamento específico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemaType problemaType = ProblemaType.ERRO_DE_SISTEMA;

        Problema problema = createProblemaBuilder(status, problemaType, ex.getMessage()).build();

        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // ----------------------------------------
    // EXCEÇÕES DE AUTENTICAÇÃO E USUÁRIO
    // ----------------------------------------

    // Handler para falhas de autenticação: EmailNotFoundException e InvalidPasswordException
    @ExceptionHandler({EmailNotFoundException.class, InvalidPasswordException.class})
    public ResponseEntity<?> handleLoginException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.ERRO_DE_AUTENTICACAO;
        // Mensagem genérica para credenciais inválidas para não expor o sistema
        String detail = "Credenciais inválidas";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para EmailAlreadyExistsException
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> handleEmailDuplicadoException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "E-mail já cadastrado";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para InvalidUserIdException
    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<?> handleInvalidUserIdException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "O userId não pode ser nulo ou vazio";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para UsernameAlreadyExistsException
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<?> handleUsernameAlreadyExistsException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "Nome de usuário já cadastrado";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para UserNotFoundException
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "Usuário não encontrado";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para UserOperationException
    @ExceptionHandler(UserOperationException.class)
    public ResponseEntity<?> handleUserOperationException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "Operação inválida para o usuário";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // ----------------------------------------
    // EXCEÇÕES RELACIONADAS À RECEITA
    // ----------------------------------------

    // Handler para ReceitaNotFoundException
    @ExceptionHandler(ReceitaNotFoundException.class)
    public ResponseEntity<?> handleReceitaNotFoundException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "Receita não encontrada";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para ReceitaOperationException
    @ExceptionHandler(ReceitaOperationException.class)
    public ResponseEntity<?> handleReceitaOperationException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "Operação inválida para a receita";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // ----------------------------------------
    // EXCEÇÕES RELACIONADAS À DESPESA
    // ----------------------------------------

    // Handler para DespesaNotFoundException
    @ExceptionHandler(DespesaNotFoundException.class)
    public ResponseEntity<?> handleDespesaNotFoundException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "Despesa não encontrada";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para DespesaOperationException
    @ExceptionHandler(DespesaOperationException.class)
    public ResponseEntity<?> handleDespesaOperationException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "Operação inválida para a despesa";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // ----------------------------------------
    // EXCEÇÕES GERAIS
    // ----------------------------------------

    // Handler para InvalidDataException
    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<?> handleInvalidDataException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "Dados inválidos";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para InvalidUuidException
    @ExceptionHandler(InvalidUuidException.class)
    public ResponseEntity<?> handleInvalidUuidException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "UUID inválido";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // ----------------------------------------
    // EXCEÇÕES DE VALIDAÇÃO DE PARÂMETROS (OVERRIDES)
    // ----------------------------------------

    // Handler para erros do metodo de validação de argumentos do controller
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        StringBuilder detail = new StringBuilder(
                "Um ou mais campos estão inválidos:\n");
        // Extrai todos os erros de campo e adiciona ao detail
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> {
                    detail.append("- Campo '")
                            .append(fieldError.getField())
                            .append("': ")
                            .append(fieldError.getDefaultMessage())
                            .append("\n");
                });
        Problema problema = createProblemaBuilder(status, problemaType, detail.toString()).build();
        return handleExceptionInternal(ex, problema, headers, status, request);
    }

    // Handler para erros de argumento do metodo typeMismatch
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(@NonNull TypeMismatchException ex,
                                                        @NonNull HttpHeaders headers,
                                                        @NonNull HttpStatusCode status,
                                                        @NonNull WebRequest request) {
        ProblemaType problemaType = ProblemaType.PARAMETRO_INVALIDO;
        String detail = String.format(
                "O parâmetro de URL '%s' recebeu o valor '%s', que é de um tipo inválido. Corrija e tente novamente.",
                ((MethodArgumentTypeMismatchException) ex).getName(),
                ex.getValue());
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return handleExceptionInternal(ex, problema, headers, status, request);
    }

    // ----------------------------------------
    // MÉTODOS DE SUPORTE
    // ----------------------------------------

    // Metodo para personalizar o corpo da resposta padrão
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception ex,
            @Nullable Object body,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        if (body == null) {
            body = Problema.builder()
                    .dataHora(LocalDateTime.now())
                    .mensagem(HttpStatus.valueOf(status.value())
                            .getReasonPhrase())
                    .build();
        } else if (body instanceof String) {
            body = Problema.builder()
                    .dataHora(LocalDateTime.now())
                    .mensagem((String) body)
                    .build();
        }

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    // Metodo auxiliar para construir uma resposta de erro
    private Problema.ProblemaBuilder createProblemaBuilder(HttpStatusCode status,
                                                           ProblemaType problemaType,
                                                           String detail) {
        return Problema.builder()
                .status(status.value())
                .type(problemaType.getUri())
                .title(problemaType.getTitle())
                .detail(detail);
    }
}