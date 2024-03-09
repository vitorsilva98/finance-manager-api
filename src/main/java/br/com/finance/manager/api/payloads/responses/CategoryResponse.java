package br.com.finance.manager.api.payloads.responses;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.beans.BeanUtils;

import br.com.finance.manager.api.models.CategoryModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;

    @SuppressWarnings("null")
    public CategoryResponse(CategoryModel categoryModel) {
        BeanUtils.copyProperties(categoryModel, this);
    }
}
