package com.solayof.schoolinventorymanagement.modelAssembler;


import com.solayof.schoolinventorymanagement.dtos.ItemDTO;
import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.restControllers.ItemController;

import org.springframework.lang.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ItemModelAssembler implements RepresentationModelAssembler<Item, EntityModel<ItemDTO>>{
    @SuppressWarnings("null")
    @Override
    public EntityModel<ItemDTO> toModel(@NonNull Item item) {
        return EntityModel.of(
            ItemDTO.fromItem(item),
            linkTo(methodOn(ItemController.class).getOne(item.getId())).withSelfRel(),
            linkTo(methodOn(ItemController.class).createItem(ItemDTO.fromItem(item))).withRel("createItem")
        );
    }
}

