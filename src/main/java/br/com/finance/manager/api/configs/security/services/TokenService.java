package br.com.finance.manager.api.configs.security.services;

import org.springframework.security.core.Authentication;

import br.com.finance.manager.api.payloads.responses.LoginResponse;

public interface TokenService {
    LoginResponse generateToken(Authentication authentication);
    String getSubject(String tokenJWT);
}
