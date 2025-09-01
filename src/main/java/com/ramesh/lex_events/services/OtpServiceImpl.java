package com.ramesh.lex_events.services;

import com.ramesh.lex_events.models.PhoneVerification;
import com.ramesh.lex_events.repositories.PhoneVerificationRepository;
import com.ramesh.lex_events.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OtpServiceImpl implements OtpService{

    private final Logger logger = LoggerFactory.getLogger(OtpServiceImpl.class);
   private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private final PhoneVerificationRepository phoneVerificationRepository;
    private final UserRepository userRepository;

    public OtpServiceImpl(PhoneVerificationRepository phoneVerificationRepository, UserRepository userRepository) {
        this.phoneVerificationRepository = phoneVerificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String generateOtp(String phoneNumber) {
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
        logger.info("Generated OTP for phone {}: {} ", phoneNumber, otp);
        PhoneVerification verification = new PhoneVerification();
        verification.setPhoneNumber(phoneNumber);
        verification.setOtpCode(otp);
        verification.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        verification.setIsVerified(false);
        phoneVerificationRepository.save(verification);
        return otp;
    }



    @Override
    @Transactional
    public boolean verifyOtp(String phoneNumber, String inputCode) {
        logger.info("Verifying OTP for phone: {}", phoneNumber);
        Optional<PhoneVerification> optionalCode = phoneVerificationRepository
                .findTopByPhoneNumberAndIsVerifiedFalseOrderByExpiryTimeDesc(phoneNumber);
        if(optionalCode.isPresent()){
            PhoneVerification verification = optionalCode.get();
            if(verification.getOtpCode().equals(inputCode) && verification.getExpiryTime().isAfter(LocalDateTime.now())){
                verification.setIsVerified(true);
                phoneVerificationRepository.save(verification);

                //update user record too

                userRepository.findByPhoneNumber(phoneNumber).ifPresent(user -> {
                    user.setIsPhoneVerified(true);
                    userRepository.save(user);
                    logger.info("user {} marked as phone verified", user.getUserName());
                });
                return true;
            }
        }
        return false;
    }
}
