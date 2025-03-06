package br.com.gestorfinanceiro.exceptions.despesa;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}