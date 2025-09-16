package com.ramesh.lex_events.services;

import com.ramesh.lex_events.dto.response.UserResponse;
import com.ramesh.lex_events.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerUser(User user);


    Optional<User> findByUsername(String username);
    void deleteUser(Long userId);

    User getUserByUsername(String username);

    List<UserResponse> getAllUsers();

}
