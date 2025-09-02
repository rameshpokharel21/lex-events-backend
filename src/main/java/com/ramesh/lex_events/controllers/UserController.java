package com.ramesh.lex_events.controllers;


import com.ramesh.lex_events.dto.request.OtpVerificationRequest;
import com.ramesh.lex_events.dto.request.PhoneVerificationRequest;
import com.ramesh.lex_events.dto.response.MessageResponse;
import com.ramesh.lex_events.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody @Valid PhoneVerificationRequest request){
        String code = userService.sendOtpForVerification(request.getPhoneNumber());
        //if using Twilio
        logger.info("OPT generated for " + request.getPhoneNumber() + ": " + code);
        return ResponseEntity.ok(new MessageResponse("OTP sent successfully."));
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<?> verifyPhone(@RequestBody @Valid OtpVerificationRequest request){
        boolean verified = userService.verifyPhoneNumber(request.getPhoneNumber(), request.getOtp());
        return verified ? ResponseEntity.ok("Phone verified.")
                : ResponseEntity.badRequest().body("Invalid OTP.");
    }



}
