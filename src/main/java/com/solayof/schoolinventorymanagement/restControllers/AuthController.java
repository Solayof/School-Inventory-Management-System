package com.solayof.schoolinventorymanagement.restControllers;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solayof.schoolinventorymanagement.constants.ERole;
import com.solayof.schoolinventorymanagement.dtos.JwtResponseDTO;
import com.solayof.schoolinventorymanagement.dtos.LoginDTO;
import com.solayof.schoolinventorymanagement.dtos.ResponseMessageDTO;
import com.solayof.schoolinventorymanagement.dtos.SignupDTO;
import com.solayof.schoolinventorymanagement.entity.RoleEntity;
import com.solayof.schoolinventorymanagement.entity.UserEntity;
import com.solayof.schoolinventorymanagement.repository.RoleRepository;
import com.solayof.schoolinventorymanagement.repository.UserRepository;
import com.solayof.schoolinventorymanagement.services.JwtService;
import com.solayof.schoolinventorymanagement.services.UserDetailsImpl;
import com.solayof.schoolinventorymanagement.services.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/noauth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserService userService;


    @PostMapping("/signin")
    @Operation(
        summary = "signin a user",
        description = "signin a user and return authenication token"
        )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "successfully signin a user",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = JwtResponseDTO.class),
                        examples = @ExampleObject(
                            name = "usertExample",
                            summary = "Sample user",
                            value = """
                            
                            """
                        )
        )
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden: No authencation provided or jwt as expired or user dont have right permission to access the resources",
                    content = @Content(
                        mediaType = "application/json",
                        
                        examples = @ExampleObject(
                            name = "ErrorExample",
                            summary = "return nothing",
                            value = """
                
                            """
                        )
        )
            ),
            
    })
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDTO entity, HttpServletResponse httpResponse) {
       Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(entity.getEmail(), entity.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());


        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true); 
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        httpResponse.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponseDTO(
            jwt,
            userDetails.getId(),
            userDetails.getFirstName(),
            userDetails.getMiddleName(),
            userDetails.getLastName(),
            userDetails.getEmail(),
            roles   
        ));
    }

    @PostMapping("/signup")
    @Operation(
        summary = "create a user",
        description = "create a user and return authenication token"
        )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "successfully create a user",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = SignupDTO.class),
                        examples = @ExampleObject(
                            name = "usertExample",
                            summary = "Sample user",
                            value = """
                            {
                                "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzb2xvcEBnbWFpbC5jb20iLCJpYXQiOjE3NTEzMjc4MDEsImV4cCI6MTc1MTM0NzgwMX0.ZgLwA-fiVhF86jMJr6rPJ9B3Qkogytc5lMjOLfk_XE4",
                                "id": "aed7ebde-33e3-443b-990d-786f336cd9cc",
                                "message": "success"
                            }
                            """
                        )
        )
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden: No authencation provided or jwt as expired or user dont have right permission to access the resources",
                    content = @Content(
                        mediaType = "application/json",
                        
                        examples = @ExampleObject(
                            name = "ErrorExample",
                            summary = "return nothing",
                            value = """
                
                            """
                        )
        )
            ),
            
    })
    public ResponseEntity<?> createUser(@Valid @RequestBody SignupDTO entity, HttpServletResponse httpResponse) {
       if (userRepository.existsByEmail(entity.getEmail())) {
        ResponseMessageDTO response = new ResponseMessageDTO();
       response.setMessage("Email already exist");
        return ResponseEntity
        .badRequest()
        .body(response);
       }

       UserEntity user = new UserEntity();
       user.setFirstName(entity.getFirstName());
       user.setMiddleName(entity.getMiddleName());
       user.setLastName(entity.getLastName());
       user.setEmail(entity.getEmail());
       user.setDob(entity.getDob());
       user.setGender(entity.getGender());
       user.setPassword(entity.getPassword());


       Set<String> entityRoles = entity.getRoles();
       Set<RoleEntity> roles = new HashSet<>();

       if (entityRoles == null) {
        RoleEntity userRole = roleRepository.findByName(ERole.ROLE_USER)
        .orElseThrow(() -> new RuntimeException("Role not found"));
        roles.add(userRole);
       } else {
        entityRoles.forEach(role -> {
            switch (role) {
                case "ROLE_ADMIN":
                    RoleEntity adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                    roles.add(adminRole);
                    break;
                case "ROLE_USER":
                    RoleEntity userRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                    roles.add(userRole);
                    break;
                case "ROLE_MODERATOR":
                    RoleEntity mdRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                    roles.add(mdRole);
                    break;
                default:
                    RoleEntity usrRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                    roles.add(usrRole);
            }
        });
       }

       RoleEntity roleRead = roleRepository.findByName(ERole.READ)
            .orElseThrow(() -> new RuntimeException("Role not found"));
        roles.add(roleRead);


       user.setRoles(roles);

       userService.addUser(user);
       Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(entity.getEmail(), entity.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateJwtToken(authentication);

        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true); 
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        httpResponse.addCookie(cookie);

       ResponseMessageDTO response = new ResponseMessageDTO();
       response.setJwt(jwt);
       response.setId(user.getId());
       response.setMessage("success");
       ResponseEntity<ResponseMessageDTO> res = new ResponseEntity<ResponseMessageDTO>(response, HttpStatus.CREATED);
       return res;

    }
    
}
