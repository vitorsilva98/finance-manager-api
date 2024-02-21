package br.com.finance.manager.api.configs.security.services.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.com.finance.manager.api.configs.security.services.TokenService;
import br.com.finance.manager.api.models.UserModel;
import br.com.finance.manager.api.payloads.responses.LoginResponse;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class TokenServiceImpl implements TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.issuer}")
    private String issuer;

    @Value("${api.security.token.expires}")
    private Integer expires;

    @Override
    public LoginResponse generateToken(Authentication authentication) {
        try {
            UserModel userModel = (UserModel) authentication.getPrincipal();

            Instant expirationDate = getExpirationDate();

            String token = JWT.create()
                .withIssuer(issuer)
                .withSubject(userModel.getUsername())
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));

            return new LoginResponse(token, expirationDate.toEpochMilli());
        } catch (Exception e) {
            log.error(String.format("Error generating token = %s", e.getMessage()));
            throw new RuntimeException("Error generating token");
        }
    }

    @Override
    public String getSubject(String tokenJWT) {
        try {
            return JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .build()
                .verify(tokenJWT)
                .getSubject();
        } catch (Exception e) {
            log.error(String.format("Error decoding token = %s", e.getMessage()));
            throw new RuntimeException("Error decoding token");
        }
    }

    private Instant getExpirationDate() {
        return LocalDateTime.now()
            .plusMinutes(expires)
            .toInstant(ZoneOffset.of("-03:00"));
    }
}
