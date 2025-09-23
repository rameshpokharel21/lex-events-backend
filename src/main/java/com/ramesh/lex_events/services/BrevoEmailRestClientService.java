package com.ramesh.lex_events.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
            ResponseEntity<Void> response = restClient.post()
                    .uri("/smtp/email")
                    .body(emailBody)
                    .retrieve()
                    .toEntity(Void.class);

            if(response.getStatusCode().is2xxSuccessful()){
                log.info("OTP email sent successfully to {}", to);
            }else{
                log.error("Failed to send email via Brevo: Status: {}, Body={}", response.getStatusCode(), response.getBody());
            }

        }catch(Exception e){
            log.error("Exception while sending email via Brevo API: {}", e.getMessage());
        }
    }


}

