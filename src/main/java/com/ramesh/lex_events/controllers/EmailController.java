package com.ramesh.lex_events.controllers;


import com.ramesh.lex_events.dto.request.OtpVerificationRequest;
import com.ramesh.lex_events.dto.response.VerificationStatusResponse;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.services.CurrentUserService;
import com.ramesh.lex_events.services.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailVerificationService emailVerificationService;
    private final CurrentUserService currentUserService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendEmailCode() {
        emailVerificationService.sendOtpCode();
        return ResponseEntity.ok("OTP sent to email.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyEmailOtp(@RequestBody @Valid OtpVerificationRequest request){
        boolean verified = emailVerificationService.verifyOtpCode(request.getOtp());
        return verified
                ? ResponseEntity.ok("Email verified.")
                : ResponseEntity.badRequest().body("Invalid or Expired code.");
    }

    @GetMapping("/is-verified")
    public ResponseEntity<VerificationStatusResponse> isEmailVerified(){
        User user = currentUserService.getCurrentUser();
        VerificationStatusResponse status  = emailVerificationService.isEmailVerified(user);
        return ResponseEntity.ok(status);
    }


}
