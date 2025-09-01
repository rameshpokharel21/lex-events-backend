package com.ramesh.lex_events.controllers;

import com.ramesh.lex_events.dto.request.OtpVerificationRequest;
import com.ramesh.lex_events.dto.request.PhoneVerificationRequest;
import com.ramesh.lex_events.dto.response.MessageResponse;
import com.ramesh.lex_events.services.OtpService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final Logger logger = LoggerFactory.getLogger(OtpController.class);
    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestParam @Valid PhoneVerificationRequest request){
        String code = otpService.generateOtp(request.getPhoneNumber());


        //if using Twilio
        logger.info("OPT generated for " + request.getPhoneNumber() + ": " + code);
        return ResponseEntity.ok(new MessageResponse("OTP sent successfully."));
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody @Valid OtpVerificationRequest request){
        boolean success = otpService.verifyOtp(request.getPhoneNumber(), request.getOtp());
        if(success){
            return ResponseEntity.ok(Map.of("message", "Phone verified successfully."));
        }else{
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid or Expired OTP."));
        }
    }
}
