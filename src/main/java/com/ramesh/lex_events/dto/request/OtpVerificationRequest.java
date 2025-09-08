package com.ramesh.lex_events.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OtpVerificationRequest {

    @NotBlank
    private String otp;
}
