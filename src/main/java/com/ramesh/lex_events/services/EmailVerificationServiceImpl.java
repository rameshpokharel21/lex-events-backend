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
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.from}")
    private  String senderEmail;
    @Value("${app.verification.code.expiry-minutes:5}")
    private long expiryMinutes;

    @Override
    public void sendOtpCode() {
        User currentUser = SecurityUtils.getCurrentUser(userRepository);
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(expiryMinutes);
        log.info("generated code for {} is {} ", currentUser.getEmail(), code);

        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setUser(currentUser);
        emailVerification.setVerificationCode(code);
        emailVerification.setExpiryTime(expiry);

        emailRepo.save(emailVerification);
        log.info("Generated otp for {}: {}", currentUser.getEmail(), code);
        //send email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(currentUser.getEmail());
            message.setSubject("Your Otp Verification Code");
            message.setText("Use this code to verify your email: " + code);
            message.setFrom(senderEmail);
            mailSender.send(message);
        } catch (MailException e) {
           log.error("error sending email: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean verifyOtpCode(String userInput) {
        User currentUser = SecurityUtils.getCurrentUser(userRepository);
        Optional<EmailVerification> optional = emailRepo.findTopByUserOrderByExpiryTimeDesc(currentUser);
        if(optional.isPresent()){
            EmailVerification verification = optional.get();
            boolean nonExpired = verification.getExpiryTime().isAfter(LocalDateTime.now());
            if(verification.getVerificationCode().equals(userInput) && nonExpired) {

                //emailRepo.save(verification);

               return true;
            }
        }
        return false;
    }

   /* @Override
    public boolean isEmailVerified(User user) {
       return emailRepo.existsByUserAndIsVerifiedTrue(user);
    }*/

    @Override
    @Transactional
    public void clearVerificationState(User user){
       emailRepo.deleteByUser(user);
    }

    @Override
    public Optional<EmailVerification> getLatestUnexpiredCodeForUser(User user){
       return emailRepo.findTopByUserOrderByExpiryTimeDesc(user)
               .filter(ev -> ev.getExpiryTime().isAfter(LocalDateTime.now()));
        /* Optional<EmailVerification> optional =
                emailRepo.findTopByUserOrderByExpiryTimeDesc(user);
        if(optional.isPresent() && optional.get().getExpiryTime().isAfter(LocalDateTime.now())){
            return optional;
        }
        return Optional.empty();*/
    }




}
