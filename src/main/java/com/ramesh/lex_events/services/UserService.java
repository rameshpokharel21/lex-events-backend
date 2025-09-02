package com.ramesh.lex_events.services;

import com.ramesh.lex_events.models.User;

import java.util.Optional;

public interface UserService {

    User registerUser(User user);


    Optional<User> findByUsername(String username);
    void deleteUser(Long userId);

    User getUserByUsername(String username);

    String sendOtpForVerification(String phoneNumber);
    boolean verifyPhoneNumber(String phoneNumber, String otp);

}
