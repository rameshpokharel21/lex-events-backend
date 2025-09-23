package com.ramesh.lex_events.services;

import com.ramesh.lex_events.models.EmailVerification;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.EmailVerificationRepository;
import com.ramesh.lex_events.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EmailVerificationServiceImplTest {

    @Autowired
    private EmailVerificationService emailService;

    @Autowired
    private EmailVerificationRepository emailRepo;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private BrevoEmailRestClientService brevoEmailService; // Mock the Brevo service

    @MockitoBean
    private CurrentUserService currentUserService;

    @Test
    void testSendOtpCode_savesVerificationAndSendEmail() {
        // Arrange
        User mockUser = new User("testUser", "test@example.com", "password123");
        mockUser = userRepository.save(mockUser);

        Mockito.when(currentUserService.getCurrentUser()).thenReturn(mockUser);

        // Mock the Brevo service to do nothing
        Mockito.doNothing().when(brevoEmailService).sendOtpEmail(Mockito.anyString(), Mockito.anyString());

        // Act
        emailService.sendOtpCode();

        // Assert
        List<EmailVerification> verifications = emailRepo.findAll();
        assertFalse(verifications.isEmpty());

        EmailVerification ev = verifications.getFirst(); // Java 21
        assertEquals(mockUser.getEmail(), ev.getUser().getEmail());
        assertFalse(ev.getIsVerified());

        // Verify that the Brevo service was called with the correct parameters
        Mockito.verify(brevoEmailService, Mockito.times(1))
                .sendOtpEmail(Mockito.eq(mockUser.getEmail()), Mockito.anyString());
    }

    @Test
    void testSendOtpCode_verifiesOtpCodeFormat() {
        // Arrange
        User mockUser = new User("testUser", "test@example.com", "password123");
        mockUser = userRepository.save(mockUser);

        Mockito.when(currentUserService.getCurrentUser()).thenReturn(mockUser);

        // Capture the OTP code to verify its format
        ArgumentCaptor<String> otpCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.doNothing().when(brevoEmailService).sendOtpEmail(Mockito.anyString(), otpCaptor.capture());

        // Act
        emailService.sendOtpCode();

        // Assert
        String otpCode = otpCaptor.getValue();
        assertNotNull(otpCode);
        assertEquals(6, otpCode.length()); // Assuming 6-digit OTP
        assertTrue(otpCode.matches("\\d+")); // Should be numeric

        Mockito.verify(brevoEmailService, Mockito.times(1))
                .sendOtpEmail(Mockito.eq(mockUser.getEmail()), Mockito.anyString());
    }

    @Test
    void testSendOtpCode_handlesEmailServiceException() {
        // Arrange
        User mockUser = new User("testUser", "test@example.com", "password123");
        mockUser = userRepository.save(mockUser);

        Mockito.when(currentUserService.getCurrentUser()).thenReturn(mockUser);

        // Mock the Brevo service to throw an exception
        Mockito.doThrow(new RuntimeException("Email service unavailable"))
                .when(brevoEmailService).sendOtpEmail(Mockito.anyString(), Mockito.anyString());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> emailService.sendOtpCode());

        // Verification should still be saved even if email fails
        List<EmailVerification> verifications = emailRepo.findAll();
        assertFalse(verifications.isEmpty());
    }

    @Test
    void testSendOtpCode_noCurrentUser_throwsException() {
        // Arrange
        Mockito.when(currentUserService.getCurrentUser()).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> emailService.sendOtpCode());
    }
}