package br.com.finance.manager.api.payloads.responses;

import org.springframework.validation.FieldError;

public record InputErrorDetails(String field, String message) {
    public InputErrorDetails(FieldError fieldError) {
        this(fieldError.getField(), fieldError.getDefaultMessage());
    }
}
