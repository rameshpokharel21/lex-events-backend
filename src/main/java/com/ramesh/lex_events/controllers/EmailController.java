package com.ramesh.lex_events.controllers;


import com.ramesh.lex_events.dto.request.OtpVerificationRequest;
import com.ramesh.lex_events.services.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendEmailCode() {
        emailVerificationService.sendOtpCode();
        return ResponseEntity.ok("Verification email code sent.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyEmailOtp(@RequestBody @Valid OtpVerificationRequest request){
        boolean verified = emailVerificationService.verifyOtpCode(request.getOtp());
        return verified
                ? ResponseEntity.ok("Email verified.")
                : ResponseEntity.badRequest().body("Invalid or Expired code.");
    }
}
