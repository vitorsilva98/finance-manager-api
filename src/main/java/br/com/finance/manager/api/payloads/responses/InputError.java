package br.com.finance.manager.api.payloads.responses;

import java.util.List;

public record InputError(String message, List<InputErrorDetails> errors) {

}
