package com.ramesh.lex_events.dto.response;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
public class EventResponse {
    private Long EventId;

    private String title;
    private String description;
    private String location;
    private LocalDateTime date;

    private SimpleUser creator;

    private Boolean isFree;

    private BigDecimal entryFee;

    @Getter
    @Setter
    public static class SimpleUser{
        private Long userId;
        private String userName;
        private String email;
        private String phoneNumber;
    }

}
