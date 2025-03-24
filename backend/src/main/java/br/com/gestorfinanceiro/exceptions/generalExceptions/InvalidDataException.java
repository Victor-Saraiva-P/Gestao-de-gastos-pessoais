package br.com.gestorfinanceiro.exceptions.generalExceptions;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}
