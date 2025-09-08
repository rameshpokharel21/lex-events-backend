package com.ramesh.lex_events.services;

import com.ramesh.lex_events.models.User;

public interface EmailVerificationService {
    void sendOtpCode();
    boolean verifyOtpCode(String userInput);
}
