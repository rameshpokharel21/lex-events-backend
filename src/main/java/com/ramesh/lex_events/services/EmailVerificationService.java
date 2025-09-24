package com.ramesh.lex_events.services;

import com.ramesh.lex_events.dto.response.VerificationStatusResponse;
import com.ramesh.lex_events.models.User;

public interface EmailVerificationService {
    void sendOtpCode();
    boolean verifyOtpCode(String userInput);

    VerificationStatusResponse isEmailVerified(User user);
    void cleanExpiredVerifications();

}
