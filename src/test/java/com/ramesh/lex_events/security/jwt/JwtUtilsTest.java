package com.ramesh.lex_events.security.jwt;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;

   // private String jwtCookie = "${spring.app.jwt.cookie}";

    @Test
    void testGetJwtFromCookie(){
        ReflectionTestUtils.setField(jwtUtils, "jwtCookie", "jwtToken");
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie jwtCookie = new Cookie("jwtToken", "test-jwt-value");
        request.setCookies(jwtCookie);
        String jwt = jwtUtils.getJwtFromCookie(request);
    }


}