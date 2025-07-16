package com.solayof.schoolinventorymanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.github.javafaker.Faker;
import com.solayof.schoolinventorymanagement.constants.ERole;
import com.solayof.schoolinventorymanagement.entity.RoleEntity;
import com.solayof.schoolinventorymanagement.entity.UserEntity;
import com.solayof.schoolinventorymanagement.repository.RoleRepository;
import com.solayof.schoolinventorymanagement.repository.UserRepository;
import com.solayof.schoolinventorymanagement.services.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
public class Setup implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserService userService;

    @Override
    public void run(String... args) throws Exception {
        Optional<RoleEntity> optRoleSuperAdmin = roleRepository.findByName(ERole.ROLE_SUPERADMIN);
        if(optRoleSuperAdmin.isEmpty()){
            RoleEntity role = new RoleEntity();
            role.setName(ERole.ROLE_SUPERADMIN);
            roleRepository.save(role);
        }

        Optional<RoleEntity> optRoleAdmin = roleRepository.findByName(ERole.ROLE_ADMIN);
        if(optRoleAdmin.isEmpty()){
            RoleEntity role = new RoleEntity();
            role.setName(ERole.ROLE_ADMIN);
            roleRepository.save(role);
        }

        Optional<RoleEntity> optRolehod = roleRepository.findByName(ERole.ROLE_HOD);
        if(optRolehod.isEmpty()){
            RoleEntity role = new RoleEntity();
            role.setName(ERole.ROLE_HOD);
            roleRepository.save(role);
        }

        Optional<RoleEntity> optRoleOwn = roleRepository.findByName(ERole.ROLE_MODERATOR);
        if(optRoleOwn.isEmpty()){
            RoleEntity role = new RoleEntity();
            role.setName(ERole.ROLE_MODERATOR);
            roleRepository.save(role);
        }

        Optional<RoleEntity> optRoleMe = roleRepository.findByName(ERole.ROLE_USER);
        if(optRoleMe.isEmpty()){
            RoleEntity role = new RoleEntity();
            role.setName(ERole.ROLE_USER);
            roleRepository.save(role);
        }

        Optional<RoleEntity> optRoleTutor = roleRepository.findByName(ERole.ROLE_TUTOR);
        if(optRoleTutor.isEmpty()){
            RoleEntity role = new RoleEntity();
            role.setName(ERole.ROLE_TUTOR);
            roleRepository.save(role);
        }

        Optional<RoleEntity> optRoleRead = roleRepository.findByName(ERole.READ);
        if(optRoleRead.isEmpty()){
            RoleEntity role = new RoleEntity();
            role.setName(ERole.READ);
            roleRepository.save(role);
        }

        Optional<RoleEntity> optRoleWrite = roleRepository.findByName(ERole.WRITE);
        if(optRoleWrite.isEmpty()){
            RoleEntity role = new RoleEntity();
            role.setName(ERole.WRITE);
            roleRepository.save(role);
        }



        UserEntity superadmin = new UserEntity();
        superadmin.setCreatedAt(LocalDateTime.now());
        superadmin.setDob(LocalDate.now());
        superadmin.setEmail("solayof@gmail.com");
        superadmin.setGender("Male");
        superadmin.setFirstName("Solomon");
        superadmin.setMiddleName("Ayofemi");
        superadmin.setLastName("Moses");
        superadmin.setPassword("solayof");
        Optional<RoleEntity> optRoleSupAdmin = roleRepository.findByName(ERole.ROLE_SUPERADMIN);
        Optional<RoleEntity> optRoleAdm = roleRepository.findByName(ERole.ROLE_ADMIN);
        
        superadmin.setRoles(Set.of(optRoleSupAdmin.get(), optRoleAdm.get()));

        userService.addUser(superadmin);

        Faker faker = new Faker();

        List<String> genders = List.of("Male", "Female");

        // for (int i = 0; i < 10; i++) {
        //     UserEntity sign = new UserEntity();
        //     sign.setCreatedAt(LocalDateTime.now());
        //     sign.setDob(LocalDate.now());
        //     sign.setEmail(faker.internet().emailAddress());
        //     sign.setFirstName(faker.name().firstName());
        //     sign.setMiddleName(faker.name().lastName());
        //     sign.setLastName(faker.name().lastName());
        //     sign.setGender(genders.get(new Random().nextInt(genders.size())));
        //     sign.setPassword(faker.lorem().word());

        //     Optional<UserEntity> user = userRepository.findByEmail(sign.getEmail());

        //     if (user.isEmpty()) {
        //         userService.addUser(sign);
        //     }
            
        // }
        
    }
}
