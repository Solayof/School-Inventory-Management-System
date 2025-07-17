package com.solayof.schoolinventorymanagement.modelAssembler;


import com.solayof.schoolinventorymanagement.dtos.ReminderDTO;
import com.solayof.schoolinventorymanagement.entity.Reminder;
import com.solayof.schoolinventorymanagement.restControllers.ReminderController;

import org.springframework.lang.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ReminderModelAssembler implements RepresentationModelAssembler<Reminder, EntityModel<ReminderDTO>> {
    @SuppressWarnings("null")
    @Override
    public EntityModel<ReminderDTO> toModel(@NonNull Reminder reminder) {
        return EntityModel.of(
            ReminderDTO.fromReminder(reminder),
            linkTo(methodOn(ReminderController.class).getOne(reminder.getId())).withSelfRel(),
            linkTo(methodOn(ReminderController.class).createItem(ReminderDTO.fromReminder(reminder))).withRel("createReminder")
        );
    }
}
