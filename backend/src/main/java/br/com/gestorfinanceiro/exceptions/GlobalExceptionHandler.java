package br.com.gestorfinanceiro.exceptions;

import br.com.gestorfinanceiro.exceptions.categoria.*;
import br.com.gestorfinanceiro.exceptions.dashboard.DashboardOperationException;
import br.com.gestorfinanceiro.exceptions.orcamentomensal.OrcamentoMensalAlreadyExistsException;
import br.com.gestorfinanceiro.exceptions.orcamentomensal.OrcamentoMensalNotFoundException;
import br.com.gestorfinanceiro.exceptions.orcamentomensal.OrcamentoMensalOperationException;
import br.com.gestorfinanceiro.exceptions.common.InvalidDataException;
import br.com.gestorfinanceiro.exceptions.common.InvalidUuidException;
import br.com.gestorfinanceiro.exceptions.despesa.DespesaNotFoundException;
import br.com.gestorfinanceiro.exceptions.despesa.DespesaOperationException;
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
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ----------------------------------------
    // EXCEÇÕES GENÉRICAS DO SISTEMA
    // ----------------------------------------

    // Handler para erros internos inesperados do sistema que não possuem tratamento específico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(RuntimeException ex, WebRequest webRequest) {
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
    public ResponseEntity<Object> handleLoginException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.ERRO_DE_AUTENTICACAO;
        // Mensagem genérica para credenciais inválidas para não expor o sistema
        String detail = "Credenciais inválidas";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para EmailAlreadyExistsException
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailDuplicadoException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "E-mail já cadastrado";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para InvalidUserIdException
    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<Object> handleInvalidUserIdException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "O userId não pode ser nulo ou vazio";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para UsernameAlreadyExistsException
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUsernameAlreadyExistsException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "Nome de usuário já cadastrado";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para UserNotFoundException
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "Usuário não encontrado";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para UserOperationException
    @ExceptionHandler(UserOperationException.class)
    public ResponseEntity<Object> handleUserOperationException(RuntimeException ex, WebRequest webRequest) {
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
    public ResponseEntity<Object> handleReceitaNotFoundException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "Receita não encontrada";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para ReceitaOperationException
    @ExceptionHandler(ReceitaOperationException.class)
    public ResponseEntity<Object> handleReceitaOperationException(RuntimeException ex, WebRequest webRequest) {
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
    public ResponseEntity<Object> handleDespesaNotFoundException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "Despesa não encontrada";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para DespesaOperationException
    @ExceptionHandler(DespesaOperationException.class)
    public ResponseEntity<Object> handleDespesaOperationException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "Operação inválida para a despesa";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // ----------------------------------------
    // EXCEÇÕES RELACIONADAS A ORÇAMENTO MENSAL
    // ----------------------------------------

    @ExceptionHandler(OrcamentoMensalNotFoundException.class)
    public ResponseEntity<Object> handleOrcamentoMensalNotFoundException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = ex.getMessage();
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    @ExceptionHandler(OrcamentoMensalOperationException.class)
    public ResponseEntity<Object> handleOrcamentoMensalOperationException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = ex.getMessage();
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    @ExceptionHandler(OrcamentoMensalAlreadyExistsException.class)
    public ResponseEntity<Object> handleOrcamentoMensalAlreadyExistsException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.CONFLICT;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = ex.getMessage();
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // ----------------------------------------
    // EXCEÇÕES RELACIONADAS A CATEGORIAS
    // ----------------------------------------

    @ExceptionHandler(CategoriaAcessDeniedException.class)
    public ResponseEntity<Object> handleCategoriaAccessDeniedException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ProblemaType problemaType = ProblemaType.ACESSO_NEGADO;

        Problema problema = createProblemaBuilder(status, problemaType, ex.getMessage())
                .mensagem("Acesso negado à categoria")
                .build();

        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    @ExceptionHandler(CategoriaAlreadyExistsException.class)
    public ResponseEntity<Object> handleCategoriaAlreadyExistsException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.CONFLICT;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;

        Problema problema = createProblemaBuilder(status, problemaType, ex.getMessage())
                .mensagem("Categoria já existe")
                .build();

        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    @ExceptionHandler(CategoriaIdNotFoundException.class)
    public ResponseEntity<Object> handleCategoriaIdNotFoundException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemaType problemaType = ProblemaType.RECURSO_NAO_ENCONTRADO;

        Problema problema = createProblemaBuilder(status, problemaType, ex.getMessage())
                .mensagem("Categoria não encontrada")
                .build();

        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    @ExceptionHandler(CategoriaNameNotFoundException.class)
    public ResponseEntity<Object> handleCategoriaNameNotFoundException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemaType problemaType = ProblemaType.RECURSO_NAO_ENCONTRADO;

        Problema problema = createProblemaBuilder(status, problemaType, ex.getMessage())
                .mensagem("Categoria não encontrada")
                .build();

        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    @ExceptionHandler(CategoriaOperationException.class)
    public ResponseEntity<Object> handleCategoriaOperationException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.OPERACAO_INVALIDA;

        Problema problema = createProblemaBuilder(status, problemaType, ex.getMessage())
                .mensagem("Erro na operação com categoria")
                .build();

        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // ----------------------------------------
    // EXCEÇÕES RELACIONADAS AO DASHBOARD
    // ----------------------------------------

    @ExceptionHandler(DashboardOperationException.class)
    public ResponseEntity<Object> handleDashboardOperationException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = ex.getMessage();

        Problema problema = createProblemaBuilder(status, problemaType, detail)
                .mensagem("Erro durante operação no dashboard")
                .build();

        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // ----------------------------------------
    // EXCEÇÕES GERAIS
    // ----------------------------------------

    // Handler para InvalidDataException
    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<Object> handleInvalidDataException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "Dados inválidos";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // Handler para InvalidUuidException
    @ExceptionHandler(InvalidUuidException.class)
    public ResponseEntity<Object> handleInvalidUuidException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemaType problemaType = ProblemaType.DADOS_INVALIDOS;
        String detail = "UUID inválido";
        Problema problema = createProblemaBuilder(status, problemaType, detail).build();
        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

    // ----------------------------------------
    // EXCEÇÕES DE VALIDAÇÃO DE OVERRIDES
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

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            @NonNull NoHandlerFoundException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        ProblemaType problemaType = ProblemaType.URI_INVALIDA;
        String detail = String.format("A URI informada '%s' não existe! Por favor corrija e tente novamente",
                ex.getRequestURL());

        Problema problema = createProblemaBuilder(status, problemaType, detail).build();

        return this.handleExceptionInternal(ex, problema, headers, status, request);
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
                    .mensagem(HttpStatus.valueOf(status.value())
                            .getReasonPhrase())
                    .build();
        } else if (body instanceof String string) {
            body = Problema.builder()
                    .mensagem(string)
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