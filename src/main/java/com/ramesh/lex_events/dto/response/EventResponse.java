package com.ramesh.lex_events.dto.response;

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
