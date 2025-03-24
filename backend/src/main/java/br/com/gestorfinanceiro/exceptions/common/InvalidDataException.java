package br.com.gestorfinanceiro.exceptions.common;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}
