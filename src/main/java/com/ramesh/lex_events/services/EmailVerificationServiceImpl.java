package com.ramesh.lex_events.services;

import com.ramesh.lex_events.models.EmailVerification;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.EmailVerificationRepository;
import com.ramesh.lex_events.repositories.UserRepository;
import com.ramesh.lex_events.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService{

    private final EmailVerificationRepository emailRepo;
    private final JavaMailSender mailSender;
    private final CurrentUserService currentUserService;

    @Value("${spring.mail.properties.from}")
    private  String senderEmail;
    @Value("${app.verification.code.expiry-minutes:15}")
    private long expiryMinutes;

    @Override
    public void sendOtpCode() {
        User currentUser = currentUserService.getCurrentUser();
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(expiryMinutes);
        //log.info("generated code for {} is {} ", currentUser.getEmail(), code);

        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setUser(currentUser);
        emailVerification.setVerificationCode(code);
        emailVerification.setExpiryTime(expiry);
        emailVerification.setIsVerified(false);
        emailRepo.save(emailVerification);
        //send email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(currentUser.getEmail());
            message.setSubject("Your Otp Verification Code");
            message.setText("Use this code to verify your email:\n" + code);
            message.setFrom(senderEmail);
            mailSender.send(message);
        } catch (MailException e) {
           log.error("error sending email: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean verifyOtpCode(String userInput) {
        User currentUser = currentUserService.getCurrentUser();
        Optional<EmailVerification> optional = emailRepo.findTopByUserAndIsVerifiedFalseOrderByExpiryTimeDesc(currentUser);
        if(optional.isPresent()){
            EmailVerification verification = optional.get();
            boolean nonExpired = verification.getExpiryTime().isAfter(LocalDateTime.now());
            if(verification.getVerificationCode().equals(userInput) && nonExpired) {
                verification.setIsVerified(true);
                emailRepo.save(verification);

               return true;
            }
        }
        return false;
    }


    @Override
    @Transactional
    public void clearVerificationState(User user){
       emailRepo.deleteByUser(user);
    }



    @Override
    public boolean isEmailVerified(User user){
        Optional<EmailVerification> latest = emailRepo.findTopByUserAndIsVerifiedTrueOrderByExpiryTimeDesc(user);
        return latest.isPresent() && latest.get().getExpiryTime().isAfter(LocalDateTime.now());
    }


}
