package br.com.finance.manager.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/auth")
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
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        LoginResponse response = tokenService.generateToken(authentication);

        log.info(LogMessagesConstants.OUTPUT_ENDPOINT_NO_CONTENT, MethodNamesConstants.LOGIN);
        return ResponseEntity.ok(response);
    }
}
