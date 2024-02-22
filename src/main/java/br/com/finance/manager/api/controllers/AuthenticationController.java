package br.com.finance.manager.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.finance.manager.api.constants.LogMessagesConstants;
import br.com.finance.manager.api.constants.MethodNamesConstants;
import br.com.finance.manager.api.payloads.requests.LoginRequest;
import br.com.finance.manager.api.payloads.responses.LoginResponse;
import br.com.finance.manager.api.services.AuthenticationService;

import jakarta.validation.Valid;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/v1/finance/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info(String.format(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.LOGIN, request));
        LoginResponse response = authenticationService.login(request);

        log.info(String.format(LogMessagesConstants.OUTPUT_ENDPOINT_NO_CONTENT, MethodNamesConstants.LOGIN));
        return ResponseEntity.ok(response);
    }
}
