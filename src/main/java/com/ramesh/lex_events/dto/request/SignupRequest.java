package com.ramesh.lex_events.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min=3, max=40)
    private String username;

    @NotBlank
    @Email
    private String email;

    //private Set<String> role;
    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    private String phoneNumber;

    @NotBlank
    @Size(min=6)
    @ToString.Exclude
    private String password;


}
