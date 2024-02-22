package br.com.finance.manager.api.services;

import br.com.finance.manager.api.payloads.requests.LoginRequest;
import br.com.finance.manager.api.payloads.responses.LoginResponse;

public interface AuthenticationService {
    LoginResponse login(LoginRequest request);
}
