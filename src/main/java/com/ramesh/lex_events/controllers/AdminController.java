package com.ramesh.lex_events.controllers;

import com.ramesh.lex_events.models.AppRole;
import com.ramesh.lex_events.models.Role;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.RoleRepository;
import com.ramesh.lex_events.repositories.UserRepository;
import com.ramesh.lex_events.dto.response.MessageResponse;
import com.ramesh.lex_events.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;



    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/promote/{username}")
    public ResponseEntity<?> promoteToAdmin(@PathVariable String username){
        Optional<User> userOpt = userRepository.findByUserNameIgnoreCase(username);
        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        User user = userOpt.get();
        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found."));

        //avoid adding duplicate admin role
        if(!user.getRoles().contains(adminRole)){
            user.getRoles().add(adminRole);
            userRepository.save(user);
            return ResponseEntity.ok("promoted to admin.");
        }else{
            return ResponseEntity.badRequest().body("User is already an admin.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.ok(new MessageResponse("user deleted successfully."));
    }
}
