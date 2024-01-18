package br.com.finance.manager.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.finance.manager.api.configs.security.services.TokenService;
import br.com.finance.manager.api.constants.LogMessagesConstants;
import br.com.finance.manager.api.constants.MethodNamesConstants;
import br.com.finance.manager.api.payloads.requests.LoginRequest;
import br.com.finance.manager.api.payloads.responses.LoginResponse;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/finance")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.LOGIN, request);
        var authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        var authentication = authenticationManager.authenticate(authenticationToken);
        LoginResponse response = tokenService.generateToken(authentication);

        log.info(LogMessagesConstants.OUTPUT_ENDPOINT, MethodNamesConstants.LOGIN, response);
        return ResponseEntity.ok(response);
    }
}
