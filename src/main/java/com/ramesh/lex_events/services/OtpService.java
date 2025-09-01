package com.ramesh.lex_events.services;

public interface OtpService {
    String generateOtp(String phoneNumber);

    boolean verifyOtp(String phoneNumber, String otp);
}
