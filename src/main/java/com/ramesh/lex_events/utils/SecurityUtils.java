package com.ramesh.lex_events.utils;

import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.UserRepository;
import com.ramesh.lex_events.security.service.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    public static User getCurrentUser(UserRepository userRepository){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !(auth.getPrincipal() instanceof UserDetails)) {
            throw new RuntimeException("No authenticated User.");
        }
        Long userId = ((UserDetailsImpl)auth.getPrincipal()).getId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found."));
    }
}
