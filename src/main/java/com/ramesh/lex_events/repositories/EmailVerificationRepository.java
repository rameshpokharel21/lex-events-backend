package com.ramesh.lex_events.repositories;

import com.ramesh.lex_events.models.EmailVerification;
import com.ramesh.lex_events.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification>findTopByUserAndIsVerifiedFalseOrderByExpiryTimeDesc(User user);


}
