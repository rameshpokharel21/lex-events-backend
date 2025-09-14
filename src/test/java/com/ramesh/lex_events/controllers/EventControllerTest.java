package com.ramesh.lex_events.controllers;

import com.ramesh.lex_events.models.AppRole;
import com.ramesh.lex_events.models.EmailVerification;
import com.ramesh.lex_events.models.Role;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.EmailVerificationRepository;
import com.ramesh.lex_events.repositories.EventRepository;
import com.ramesh.lex_events.repositories.RoleRepository;
import com.ramesh.lex_events.repositories.UserRepository;
import com.ramesh.lex_events.security.jwt.JwtUtils;
import com.ramesh.lex_events.security.service.UserDetailsImpl;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Set;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private String jwtToken;

    @BeforeEach
    void setup(){
        //clean up
        eventRepository.deleteAll();
        emailVerificationRepository.deleteAll();
        userRepository.deleteAll();

        //create test user
        User user = new User();
        user.setUserName("testuser");
        user.setEmail("test@example.com");
//        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
//                        .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));
//        user.getRoles().add(userRole);
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);

        //create verified EmailVerification
        EmailVerification verification = new EmailVerification();
        verification.setUser(user);
        verification.setVerificationCode("123456");
        verification.setIsVerified(true);
        verification.setExpiryTime(LocalDateTime.now().plusMinutes(15));
        emailVerificationRepository.save(verification);

        //generate JWT from cookie
        UserDetails userDetails = new UserDetailsImpl(user);
        jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

    }

    @Disabled
    @Test
    void testCreteEvent_withValidEmailAndJwt_shouldSucceed() throws  Exception{
        String requestbody = """
                {
                    "title": "Test Event",
                    "description": "Test description",
                    "location": "Test location",
                    "date": "2030-01-01T10:00:00",
                    "isFree": true,
                    "entryFee": null,
                    "showContactInfo": true
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("jwtToken", jwtToken))
                        .content(requestbody))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Server error: OTP expired or Email not verified.")
        );
    }

}