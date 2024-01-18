package br.com.finance.manager.api.controllers;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.finance.manager.api.constants.LogMessagesConstants;
import br.com.finance.manager.api.constants.MethodNamesConstants;
import br.com.finance.manager.api.payloads.requests.CreateCategoryRequest;
import br.com.finance.manager.api.payloads.responses.CategoryResponse;
import br.com.finance.manager.api.services.CategoryService;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/finance/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody @Valid CreateCategoryRequest request, UriComponentsBuilder uriBuilder) {
        log.info(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.CREATE_CATEGORY, request);
        CategoryResponse response = categoryService.create(request);
        URI getByIdUri = uriBuilder.path("/api/v1/finance/categories/{id}").buildAndExpand(response.getId()).toUri();

        log.info(LogMessagesConstants.OUTPUT_ENDPOINT, MethodNamesConstants.CREATE_CATEGORY, response);
        return ResponseEntity.created(getByIdUri).body(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable UUID id) {
        log.info(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.GET_CATEGORY_BY_ID, id);
        CategoryResponse response = categoryService.getById(id);

        log.info(LogMessagesConstants.OUTPUT_ENDPOINT, MethodNamesConstants.GET_CATEGORY_BY_ID, response);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAll(@PageableDefault(sort = {"name"}) Pageable pageable) {
        log.info(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.GET_ALL_CATEGORIES, pageable);
        Page<CategoryResponse> response = categoryService.getAll(pageable);

        log.info(LogMessagesConstants.OUTPUT_ENDPOINT, MethodNamesConstants.GET_ALL_CATEGORIES, response);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        log.info(LogMessagesConstants.INPUT_ENDPOINT, MethodNamesConstants.DELETE_CATEGORY_BY_ID, id);
        categoryService.deleteById(id);

        log.info(LogMessagesConstants.OUTPUT_ENDPOINT_NO_CONTENT, MethodNamesConstants.DELETE_CATEGORY_BY_ID);
        return ResponseEntity.noContent().build();
    }
}
