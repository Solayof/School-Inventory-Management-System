package com.solayof.schoolinventorymanagement.modelAssembler;


import com.solayof.schoolinventorymanagement.dtos.AssignmentDTO;
import com.solayof.schoolinventorymanagement.entity.Assignment;
import com.solayof.schoolinventorymanagement.restControllers.AssignmentController;

import org.springframework.lang.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class AssignmentModelAssembler implements RepresentationModelAssembler<Assignment, EntityModel<AssignmentDTO>>{
    @SuppressWarnings("null")
    @Override
    public EntityModel<AssignmentDTO> toModel(@NonNull Assignment assignment) {
        return EntityModel.of(
            AssignmentDTO.fromAssignment(assignment),
            linkTo(methodOn(AssignmentController.class).getOne(assignment.getId())).withSelfRel(),
            linkTo(methodOn(AssignmentController.class).createCollector(AssignmentDTO.fromAssignment(assignment))).withRel("createAssignment")
        );
    }
}
