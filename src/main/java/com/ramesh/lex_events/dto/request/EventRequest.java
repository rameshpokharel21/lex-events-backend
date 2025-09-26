package com.ramesh.lex_events.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ramesh.lex_events.validator.ValidEntryFee;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@ValidEntryFee
public class EventRequest {


    @NotBlank(message = "Title is required.")
    private String title;

    @NotBlank(message = "Description is required.")
    private String description;

    @NotBlank(message = "Address is required.")
    private String location;

    @NotNull(message = "Event date and time is required.")
    @JsonFormat(pattern= "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    @NotNull(message = "Please specify if the event is free or paid.")
    private Boolean isFree = true;

    @DecimalMin(value="0.01", message = "Entry fee must be > 0 if event is paid.")
    private BigDecimal entryFee;

    @NotNull(message = "Contact info preference must be specified.")
    private Boolean showContactInfo = false;


}
