package com.ramesh.lex_events.services;

import com.ramesh.lex_events.models.EmailVerification;
import com.ramesh.lex_events.models.User;

import java.util.Optional;

public interface EmailVerificationService {
    void sendOtpCode();
    boolean verifyOtpCode(String userInput);

    boolean isEmailVerified(User user);
    void clearVerificationState(User user);

    //Optional<EmailVerification> getLatestUnexpiredCodeForUser(User user);
}
