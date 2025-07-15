package com.solayof.schoolinventorymanagement.modelAssembler;

import com.solayof.schoolinventorymanagement.dtos.CategoryDto;
import com.solayof.schoolinventorymanagement.entity.Category;
import com.solayof.schoolinventorymanagement.restControllers.CategoryController;

import org.springframework.lang.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CategoryModelAssembler implements RepresentationModelAssembler<Category, EntityModel<CategoryDto>>{
    @SuppressWarnings("null")
    @Override
    public EntityModel<CategoryDto> toModel(@NonNull Category category) {
        return EntityModel.of(
            CategoryDto.fromCategory(category),
            linkTo(methodOn(CategoryController.class).getOne(category.getId())).withSelfRel(),
            linkTo(methodOn(CategoryController.class).createCategory(CategoryDto.fromCategory(category))).withRel("createCategory")
        );
    }
}
