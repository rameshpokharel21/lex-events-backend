package com.ramesh.lex_events.controllers;

import com.ramesh.lex_events.dto.response.UserResponse;
import com.ramesh.lex_events.models.AppRole;
import com.ramesh.lex_events.models.Role;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.RoleRepository;
import com.ramesh.lex_events.repositories.UserRepository;
import com.ramesh.lex_events.dto.response.MessageResponse;
import com.ramesh.lex_events.services.EventService;
import com.ramesh.lex_events.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final EventService eventService;




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

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsersList(){
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long userId){
        userService.deleteUser(userId);
        log.info("Deleting user with id: {}", userId);
        return ResponseEntity.ok(new MessageResponse("user deleted successfully."));
    }


    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<?> deleteEventById(@PathVariable Long eventId){
        eventService.deleteEventById(eventId);
        log.info("Deleting event with id: {}", eventId);
        return ResponseEntity.ok(new MessageResponse("Event deleted successfully"));
    }
}
