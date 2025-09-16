package com.ramesh.lex_events.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponse {

    private Long userId;
    @JsonIgnore
    private String jwtCookie;
    private String username;

    private Set<String> roles;

    public LoginResponse(Long userId, String username, Set<String> roles) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }
}
