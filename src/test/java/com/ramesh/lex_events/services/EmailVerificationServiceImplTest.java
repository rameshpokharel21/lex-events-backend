package com.ramesh.lex_events.services;

import com.ramesh.lex_events.models.EmailVerification;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.EmailVerificationRepository;
import com.ramesh.lex_events.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class EmailVerificationServiceImplTest {

   @Autowired
   private EmailVerificationService emailService;

   @Autowired
   private EmailVerificationRepository emailRepo;

   @Autowired
   private UserRepository userRepository;

   @MockitoBean
   private JavaMailSender mailSender;

   @MockitoBean
   private CurrentUserService currentUserService;

   @Test
    void testSendOtpCode_savesVerificationAndSendEmail(){
        //Arrange
        User mockUser = new User("testUser", "test@example.com", "password123");
        mockUser = userRepository.save(mockUser);

        Mockito.when(currentUserService.getCurrentUser()).thenReturn(mockUser);

        //Act
        emailService.sendOtpCode();

        //Assert
       List<EmailVerification> verifications = emailRepo.findAll();
       assertFalse(verifications.isEmpty());

       EmailVerification ev = verifications.get(0);//Java 21 .getFirst();
       assertEquals(mockUser.getEmail(), ev.getUser().getEmail());
       assertFalse(ev.getIsVerified());

        Mockito.verify(mailSender, Mockito.times(1)).send(Mockito.any(SimpleMailMessage.class));

    }

}