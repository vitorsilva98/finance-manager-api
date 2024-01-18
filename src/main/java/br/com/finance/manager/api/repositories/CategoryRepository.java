package br.com.finance.manager.api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.finance.manager.api.models.CategoryModel;

public interface CategoryRepository extends JpaRepository<CategoryModel, UUID> {
    boolean existsByName(String name);
}
