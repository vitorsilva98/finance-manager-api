package br.com.finance.manager.api.payloads.responses;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.beans.BeanUtils;

import br.com.finance.manager.api.models.CategoryModel;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CategoryResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;

    public CategoryResponse(CategoryModel categoryModel) {
        // System.out.println("cu");
        // BeanUtils.copyProperties(this, categoryModel);
        this.id = categoryModel.getId();
        this.name = categoryModel.getName();
    }
}
