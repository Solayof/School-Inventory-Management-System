package com.solayof.schoolinventorymanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import com.solayof.schoolinventorymanagement.constants.ERole;
import com.solayof.schoolinventorymanagement.entity.RoleEntity;
import com.solayof.schoolinventorymanagement.entity.UserEntity;
import com.solayof.schoolinventorymanagement.repository.RoleRepository;
import com.solayof.schoolinventorymanagement.repository.UserRepository;
import com.solayof.schoolinventorymanagement.services.UserService;

import java.util.Optional;
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

        Optional<RoleEntity> optRoleManger = roleRepository.findByName(ERole.ROLE_MANAGER);
        if(optRoleManger.isEmpty()){
            RoleEntity role = new RoleEntity();
            role.setName(ERole.ROLE_MANAGER);
            roleRepository.save(role);
        }

        

        Optional<UserEntity> optUser = userRepository.findByEmail("solayof@gmail.com");
        if (optUser.isEmpty()) {
        UserEntity superadmin = new UserEntity();
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
        }
    }
}
