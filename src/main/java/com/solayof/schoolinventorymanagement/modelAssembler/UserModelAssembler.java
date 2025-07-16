package com.solayof.schoolinventorymanagement.modelAssembler;


import org.springframework.lang.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.solayof.schoolinventorymanagement.entity.UserEntity;
import com.solayof.schoolinventorymanagement.restControllers.UserController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;



@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserEntity, EntityModel<UserEntity>> {
    @SuppressWarnings("null")
    @Override
    public EntityModel<UserEntity> toModel(@NonNull UserEntity user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getOne(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).users()).withRel("users")
                );
    }
}
