package com.ramesh.lex_events.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;


@Configuration
public class BrevoRestClientConfig {

    @Value("${brevo.api.url}")
    private  String apiUrl;

    @Value("${brevo.api.key}")
    private String apiKey;

    @Bean
    public RestClientCustomizer brevoCustomizer(){
        return builder -> builder
                .baseUrl(apiUrl)
                .defaultHeader("api-key",  apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("accept", MediaType.APPLICATION_JSON_VALUE);
    }

    @Bean
    public RestClient brevoRestClient(RestClient.Builder builder){
        return builder.build();
    }
}
