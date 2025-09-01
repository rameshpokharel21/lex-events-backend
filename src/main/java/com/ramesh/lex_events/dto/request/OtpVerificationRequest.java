package com.ramesh.lex_events.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpVerificationRequest {
    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String otp;
}
