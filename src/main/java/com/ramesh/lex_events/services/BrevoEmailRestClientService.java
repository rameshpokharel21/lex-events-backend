package com.ramesh.lex_events.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class BrevoEmailRestClientService {


    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${app.name:lex events}")
    private String senderName;

    private final RestClient restClient;

    public void sendOtpEmail(String to, String code){
        String subject = "Your Otp Verification Code";
        String text = "Use this code to verify your email:\n" + code;

        Map<String, Object> emailBody = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", to)),
                "subject", subject,
                "textContent", text
        );

        try{
            String responseString = restClient.post()
                    .uri("/smtp/email")
                    .body(emailBody)
                    .retrieve()
                    .onStatus(status -> status == HttpStatus.UNAUTHORIZED, (request, response) -> {
                        throw new RuntimeException("Invalid Brevo API key");
                    })
                    .onStatus(status -> status == HttpStatus.BAD_REQUEST, (request, response) -> {
                        String error = new String(response.getBody().readAllBytes());
                        throw new RuntimeException("Bad request to Brevo: " + error);
                    })
                    .body(String.class);
            log.info("OTP email sent successfully to {}", to);

        }catch(Exception e){
            log.error("Exception while sending email via Brevo API: {}", e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }


}

