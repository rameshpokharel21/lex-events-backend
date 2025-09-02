package com.ramesh.lex_events.repositories;

import com.ramesh.lex_events.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByUserNameIgnoreCase(String username);
    boolean existsByUserNameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
}
