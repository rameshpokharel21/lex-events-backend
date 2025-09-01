package com.ramesh.lex_events.repositories;

import com.ramesh.lex_events.models.PhoneVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {
    Optional<PhoneVerification> findTopByPhoneNumberAndIsVerifiedFalseOrderByExpiryTimeDesc(String phoneNumber );
}
