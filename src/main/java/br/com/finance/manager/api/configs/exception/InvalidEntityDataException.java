package br.com.finance.manager.api.configs.exception;

public class InvalidEntityDataException extends RuntimeException {
    public InvalidEntityDataException(String message) {
        super(message);
    }
}
