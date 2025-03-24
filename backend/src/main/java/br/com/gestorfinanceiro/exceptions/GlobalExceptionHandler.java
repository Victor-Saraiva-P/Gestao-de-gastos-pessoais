package br.com.gestorfinanceiro.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;


@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Handler para erros internos inesperados do sistema que não possuem tratamento específico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemaType problemaType = ProblemaType.ERRO_DE_SISTEMA;

        Problema problema = createProblemaBuilder(status, problemaType, ex.getMessage()).build();

        return this.handleExceptionInternal(ex, problema, new HttpHeaders(), status, webRequest);
    }

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