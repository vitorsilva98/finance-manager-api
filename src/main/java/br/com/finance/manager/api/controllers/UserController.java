package br.com.finance.manager.api.controllers;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.finance.manager.api.constants.LogMessagesConstants;
import br.com.finance.manager.api.constants.MethodNamesConstants;
import br.com.finance.manager.api.payloads.requests.ChangeUserNameRequest;
import br.com.finance.manager.api.payloads.requests.CreateUserRequest;
import br.com.finance.manager.api.payloads.requests.UpdateUserRequest;
import br.com.finance.manager.api.payloads.responses.UserResponse;
import br.com.finance.manager.api.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/v1/finance/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid CreateUserRequest request, UriComponentsBuilder uriBuilder) {
        log.info(String.format(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.CREATE_USER, request));
        UserResponse response = userService.create(request);
        URI getByIdUri = uriBuilder.path("/api/v1/finance/users/{id}").buildAndExpand(response.getId()).toUri();

        log.info(String.format(LogMessagesConstants.OUTPUT_ENDPOINT, MethodNamesConstants.CREATE_USER, response));
        return ResponseEntity.created(getByIdUri).body(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        log.info(String.format(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.GET_USER_BY_ID, id));
        UserResponse response = userService.getById(id);

        log.info(String.format(LogMessagesConstants.OUTPUT_ENDPOINT, MethodNamesConstants.GET_USER_BY_ID, response));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAll(@PageableDefault(sort = {"name"}) Pageable pageable) {
        log.info(String.format(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.GET_ALL_USERS, pageable));
        Page<UserResponse> response = userService.getAll(pageable);

        log.info(String.format(LogMessagesConstants.OUTPUT_ENDPOINT, MethodNamesConstants.GET_ALL_USERS, response));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PatchMapping
    public ResponseEntity<UserResponse> changeName(@RequestBody @Valid ChangeUserNameRequest request, HttpServletRequest httpServletRequest) {
        log.info(String.format(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.CHANGE_USER_NAME, request));
        UserResponse response = userService.changeName(request, httpServletRequest.getAttribute("subject").toString());

        log.info(String.format(LogMessagesConstants.OUTPUT_ENDPOINT, MethodNamesConstants.CHANGE_USER_NAME, response));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable UUID id, @RequestBody @Valid UpdateUserRequest request) {
        log.info(String.format(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.UPDATE_USER, id));
        UserResponse response = userService.update(id, request);

        log.info(String.format(LogMessagesConstants.OUTPUT_ENDPOINT, MethodNamesConstants.UPDATE_USER, response));
        return ResponseEntity.ok(response);
    }
}
