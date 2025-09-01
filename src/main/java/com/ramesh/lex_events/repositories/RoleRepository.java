package com.ramesh.lex_events.repositories;

import com.ramesh.lex_events.models.AppRole;
import com.ramesh.lex_events.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
