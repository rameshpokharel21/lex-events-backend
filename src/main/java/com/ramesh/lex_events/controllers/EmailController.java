package com.ramesh.lex_events.controllers;


import com.ramesh.lex_events.dto.request.OtpVerificationRequest;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.UserRepository;
import com.ramesh.lex_events.services.EmailVerificationService;
import com.ramesh.lex_events.services.UserService;
import com.ramesh.lex_events.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailVerificationService emailVerificationService;
    private final UserRepository userRepository;

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

   /* @GetMapping("/is-verified")
    public ResponseEntity<Map<String, Boolean>> isEmailVerified(){
        User user = SecurityUtils.getCurrentUser(userRepository);
        boolean verified = emailVerificationService.isEmailVerified(user);
        return ResponseEntity.ok(Map.of("verified", verified));
    }*/


}
