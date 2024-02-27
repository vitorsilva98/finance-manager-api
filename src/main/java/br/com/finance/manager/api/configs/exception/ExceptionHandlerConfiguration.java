package br.com.finance.manager.api.configs.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestControllerAdvice
public class ExceptionHandlerConfiguration {

    private static final String LOG_MESSAGE = "An exception occurred = %s";

    /* Authentication and authorization exceptions */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> handlerBadCredentialsResponse() {
        Error error = new Error("Username or password invalid");
        log.error(String.format(LOG_MESSAGE, error));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Error> handlerAccessDeniedResponse() {
        Error error = new Error("User don't have access");
        log.error(String.format(LOG_MESSAGE, error));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Error> handlerUserDisabledResponse() {
        Error error = new Error("User disabled");
        log.error(String.format(LOG_MESSAGE, error));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /* Service exceptions */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Error> handlerEntityNotFoundResponse(EntityNotFoundException ex) {
        Error error = new Error(ex.getMessage());
        log.error(String.format(LOG_MESSAGE, error));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidEntityDataException.class)
    public ResponseEntity<Error> handlerInvalidEntityDataResponse(InvalidEntityDataException ex) {
        Error error = new Error(ex.getMessage());
        log.error(String.format(LOG_MESSAGE, error));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Error> handlerBusinessRuleResponse(BusinessRuleException ex) {
        Error error = new Error(ex.getMessage());
        log.error(String.format(LOG_MESSAGE, error));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<InputError> handlerInputErrorValidationResponse(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getFieldErrors();
        InputError inputError = new InputError("Invalid inputs", errors.stream().map(InputErrorDetails::new).toList());
        log.error(String.format(LOG_MESSAGE, inputError));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(inputError);
    }

    /* Records exceptions */
    private record Error(String message) {}

    private record InputError(String message, List<InputErrorDetails> errors) {}

    private record InputErrorDetails(String field, String message) {
        public InputErrorDetails(FieldError fieldError) {
            this(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }
}
