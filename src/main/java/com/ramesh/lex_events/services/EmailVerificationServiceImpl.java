package com.ramesh.lex_events.services;

import com.ramesh.lex_events.dto.response.VerificationStatusResponse;
import com.ramesh.lex_events.models.EmailVerification;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.EmailVerificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService{

    private final BrevoEmailRestClientService brevoEmailRestClientService;
    private final EmailVerificationRepository emailRepo;
    private final CurrentUserService currentUserService;

    @Value("${app.verification.code.expiry-minutes:10}")
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
          brevoEmailRestClientService.sendOtpEmail(currentUser.getEmail(), code);
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
    public VerificationStatusResponse isEmailVerified(User user){
        Optional<EmailVerification> latest = emailRepo.findTopByUserAndIsVerifiedTrueOrderByExpiryTimeDesc(user);
        if(latest.isPresent() && latest.get().getExpiryTime().isAfter(LocalDateTime.now())){
            return new VerificationStatusResponse(true, latest.get().getExpiryTime());

        }
        return new VerificationStatusResponse(false, null);
    }


}
