package br.com.finance.manager.api.services.impl;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.finance.manager.api.configs.exception.BusinessRuleException;
import br.com.finance.manager.api.configs.exception.EntityNotFoundException;
import br.com.finance.manager.api.configs.exception.InvalidEntityDataException;
import br.com.finance.manager.api.models.CategoryModel;
import br.com.finance.manager.api.payloads.requests.CreateCategoryRequest;
import br.com.finance.manager.api.payloads.responses.CategoryResponse;
import br.com.finance.manager.api.repositories.CategoryRepository;
import br.com.finance.manager.api.services.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final String CATEGORY_CACHE_KEY = "categories";

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CATEGORY_CACHE_KEY, allEntries = true)
    public CategoryResponse create(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new InvalidEntityDataException("Category already exists");
        }

        CategoryModel categoryModel = new CategoryModel();
        categoryModel.setName(request.getName());
        return new CategoryResponse(categoryRepository.save(categoryModel));
    }

    @Override
    public CategoryResponse getById(UUID id) {
        return new CategoryResponse(findById(id));
    }

    @SuppressWarnings("null")
    @Override
    @Cacheable(cacheNames = CATEGORY_CACHE_KEY)
    public Page<CategoryResponse> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(CategoryResponse::new);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CATEGORY_CACHE_KEY, allEntries = true)
    public void deleteById(UUID id) {
        CategoryModel categoryModel = findById(id);

        if (!categoryModel.getEntries().isEmpty()) {
            throw new BusinessRuleException("Category already related with entries");
        }

        categoryRepository.delete(categoryModel);
    }

    @SuppressWarnings("null")
    private CategoryModel findById(UUID id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Category doesn't exists"));
    }
}
