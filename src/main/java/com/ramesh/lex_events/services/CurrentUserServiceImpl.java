package com.ramesh.lex_events.services;

import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CurrentUserServiceImpl implements CurrentUserService{

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserNameIgnoreCase(username)
                .orElseThrow(() -> new IllegalStateException("User not found."));
    }
}
