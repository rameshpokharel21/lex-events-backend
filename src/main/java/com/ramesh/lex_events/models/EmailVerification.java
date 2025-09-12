package com.ramesh.lex_events.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="email_verifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String verificationCode;

    @NotNull
    private LocalDateTime expiryTime;

    //private Boolean isVerified = false;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
