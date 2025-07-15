package com.solayof.schoolinventorymanagement.modelAssembler;


import com.solayof.schoolinventorymanagement.dtos.CollectorDTO;
import com.solayof.schoolinventorymanagement.entity.Collector;
import com.solayof.schoolinventorymanagement.restControllers.CollectorController;

import org.springframework.lang.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CollectorModelAssembler implements RepresentationModelAssembler<Collector, EntityModel<CollectorDTO>>{
    @SuppressWarnings("null")
    @Override
    public EntityModel<CollectorDTO> toModel(@NonNull Collector collector) {
        return EntityModel.of(
            CollectorDTO.fromCollector(collector),
            linkTo(methodOn(CollectorController.class).getOne(collector.getId())).withSelfRel(),
            linkTo(methodOn(CollectorController.class).createCollector(CollectorDTO.fromCollector(collector))).withRel("createCollector")
        );
    }
}

