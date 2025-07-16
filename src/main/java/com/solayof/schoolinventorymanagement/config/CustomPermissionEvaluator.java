package com.solayof.schoolinventorymanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.solayof.schoolinventorymanagement.utils.CommonUtil;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
@Slf4j
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {


    @Autowired
    CommonUtil commonUtil;

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
    //     log.info("Checking permission for targetId: {}, targetType: {}, permission: {}", targetId, targetType, permission);
    //     if (auth == null || targetType == null || permission == null || targetId == null)
    //         return false;

    //     boolean isAdmin = auth.getAuthorities().stream()
    //         .anyMatch(granted -> granted.getAuthority().equals("ROLE_ADMIN") || granted.getAuthority().equals("ROLE_SUPERADMIN"));

    //     if (isAdmin) return true;


    // switch (targetType) {
    //     case "Document":
    //         return checkDocumentPermission(auth, targetId, permission.toString());
    //     case "Course":
    //         log.info("Checking course permission for targetId: {}, permission: {}", targetId, permission);
    //         return checkCoursePermission(auth, targetId, permission.toString());
    //     case "Profile":
    //         return checkProfilePermission(auth, targetId, permission.toString());
    //     default:
    //         return false;
    // }
    return false;


        
    }

    // private boolean checkDocumentPermission(Authentication auth, Serializable targetId, String string) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'checkDocumentPermission'");
    // }

    // private boolean checkCoursePermission(Authentication auth, Serializable targetId, String permission) {
    //     Optional<Course> optionalCourse = courseRepository.findById((UUID) targetId);
    //     if (permission.toLowerCase().equals("write") && optionalCourse.isEmpty()) {
    //         log.info(permission + " permission requested for non-existing course with ID: " + targetId);
    //         return auth.getAuthorities().stream()
    //         .anyMatch(granted -> granted.getAuthority().equals("WRITE")); 
    //     }

    //     if (optionalCourse.isEmpty()) return false;
        
    //     Course course = optionalCourse.get();
    //     UserDetailsImpl currentUser = commonUtil.loggedInUser();
    //     boolean isInstructor = course.getOwner().getEmail().equals(currentUser.getEmail());

    //     switch (permission.toLowerCase()) {
    //     case "read":
    //         return true; 
    //     case "write":
    //         return isInstructor;
    //     case "delete":
    //         return isInstructor;
    //     default:
    //         return false;
    //     }
    // }

    // private boolean checkProfilePermission(Authentication auth, Serializable targetId, String string) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'checkProfilePermission'");
    // }

    @Override
    public boolean hasPermission(Authentication authentication, Object o, Object o1) {
        return false; // not used
    }
}
