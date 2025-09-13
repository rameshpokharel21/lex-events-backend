package com.ramesh.lex_events.repositories;

import com.ramesh.lex_events.models.EmailVerification;
import com.ramesh.lex_events.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    //boolean existsByUserAndIsVerifiedTrue(User user);
    Optional<EmailVerification> findTopByUserAndIsVerifiedFalseOrderByExpiryTimeDesc(User user);
    Optional<EmailVerification> findTopByUserAndIsVerifiedTrueOrderByExpiryTimeDesc(User user);
    void deleteByUser(User user);
    Optional<EmailVerification> findTopByUserOrderByExpiryTimeDesc(User user);

}
