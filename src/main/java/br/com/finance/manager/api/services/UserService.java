package br.com.finance.manager.api.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.finance.manager.api.payloads.requests.ChangeUserNameRequest;
import br.com.finance.manager.api.payloads.requests.CreateUserRequest;
import br.com.finance.manager.api.payloads.requests.UpdateUserRequest;
import br.com.finance.manager.api.payloads.responses.UserResponse;

public interface UserService {
    UserResponse create(CreateUserRequest request);
    UserResponse getById(UUID id);
    Page<UserResponse> getAll(Pageable pageable);
    UserResponse update(UUID id, UpdateUserRequest request);
    UserResponse changeName(ChangeUserNameRequest request, String username);
}
