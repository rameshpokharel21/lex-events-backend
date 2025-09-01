package com.ramesh.lex_events.controllers;


import com.ramesh.lex_events.dto.request.OtpVerificationRequest;
import com.ramesh.lex_events.dto.request.PhoneVerificationRequest;
import com.ramesh.lex_events.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody  PhoneVerificationRequest request){
        userService.sendOtpForVerification(request.getPhoneNumber());
        return ResponseEntity.ok("OTP sent successfully.");
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<?> verifyPhone(@RequestBody OtpVerificationRequest request){
        boolean verified = userService.verifyPhoneNumber(request.getPhoneNumber(), request.getOtp());
        return verified ? ResponseEntity.ok("Phone verified.")
                : ResponseEntity.badRequest().body("Invalid OTP.");
    }



}
