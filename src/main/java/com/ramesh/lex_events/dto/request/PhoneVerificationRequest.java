package com.ramesh.lex_events.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PhoneVerificationRequest {
    @NotBlank
    private String phoneNumber;
}
