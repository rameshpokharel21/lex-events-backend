package com.ramesh.lex_events.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerificationStatusResponse {
    private boolean verified;
    private LocalDateTime expiresAt;
}
