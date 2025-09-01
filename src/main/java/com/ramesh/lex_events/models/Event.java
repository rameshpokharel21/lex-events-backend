package com.ramesh.lex_events.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @NotBlank(message = "name can not be blank")
    private String title;

    @NotBlank(message="description can not be blank")
    private String description;
    @NotBlank(message = "location cannot be blank")
    private String location;
    @NotNull
    private LocalDateTime date;

    @Column(name="is_free")
    private Boolean isFree;

    @Column(name="entry_fee")
    private BigDecimal entryFee;

    @Column(name="show_contact_info")
    private Boolean showContactInfo = false;

    @ManyToOne
    @JoinColumn(name="created_by")
    @NotNull
    private User createdBy;
}
