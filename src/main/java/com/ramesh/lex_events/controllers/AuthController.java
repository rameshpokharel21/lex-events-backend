package com.ramesh.lex_events.controllers;

import com.ramesh.lex_events.models.AppRole;
import com.ramesh.lex_events.models.Role;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.RoleRepository;
import com.ramesh.lex_events.repositories.UserRepository;
import com.ramesh.lex_events.security.jwt.JwtUtils;
import com.ramesh.lex_events.dto.request.LoginRequest;
import com.ramesh.lex_events.dto.request.SignupRequest;
import com.ramesh.lex_events.dto.response.LoginResponse;
import com.ramesh.lex_events.dto.response.MessageResponse;
import com.ramesh.lex_events.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;



    public AuthController(JwtUtils jwtUtils, AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        logger.info("inside AuthController constructor.");
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        //logger.info("Login request received for username : {}", loginRequest.getUsername());
        Authentication authentication;

        try{
            authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(loginRequest.getUsername().toLowerCase(),loginRequest.getPassword() )
                    );

        }catch(AuthenticationException ex){
            logger.warn("Login failed: {}", ex.getMessage());
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        //logger.info(jwtCookie.toString());
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        LoginResponse response = new LoginResponse(
                userDetails.getId(),
                //jwtCookie.toString(),
                "",
                userDetails.getUsername().toLowerCase(),
                userDetails.getRoleNames()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                //.header(HttpHeaders.SET_COOKIE, jwtCookie.getName() + "="+jwtCookie.getValue())
                .body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){

        if(userRepository.existsByUserNameIgnoreCase(signupRequest.getUsername())){
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: username is already taken"));
        }

        if(userRepository.existsByEmailIgnoreCase(signupRequest.getEmail())){
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: email is already taken."));
        }

        User user = new User(signupRequest.getUsername().toLowerCase(),
                signupRequest.getEmail().toLowerCase(),
                signupRequest.getPhoneNumber(),
                passwordEncoder.encode(signupRequest.getPassword()));

        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: ROLE_USER not found."));


        user.setRoles(Set.of(userRole));
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("user registered successfully."));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        if(authentication == null || !authentication.isAuthenticated()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());
        LoginResponse response = new LoginResponse(userDetails.getId(),
                userDetails.getUsername(), roles);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> signOutUser(){
        ResponseCookie cookie = jwtUtils.clearJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been logged out!"));
    }

}
