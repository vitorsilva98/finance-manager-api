package br.com.finance.manager.api.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.finance.manager.api.payloads.requests.CreateCategoryRequest;
import br.com.finance.manager.api.payloads.responses.CategoryResponse;

public interface CategoryService {
    CategoryResponse create(CreateCategoryRequest request);
    CategoryResponse getById(UUID id);
    Page<CategoryResponse> getAll(Pageable pageable);
    void deleteById(UUID id);
}
