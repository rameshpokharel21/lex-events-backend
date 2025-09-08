package com.ramesh.lex_events.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @NotBlank
    @Column(name="username", unique = true)
    private String userName;


    @NotBlank
    @Email
    @Column(name="email", unique = true)
    private String email;

    @Column(name="phone_number")
    private String phoneNumber;

    @NotBlank
    @Size(min=6)
    @Column(name="password")
    @ToString.Exclude
    @JsonIgnore
    private String password;


    @Column(name="is_email_verified")
    @NotNull
    private Boolean isEmailVerified = false;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        fetch = FetchType.EAGER)
    @JoinTable(name="user_role",
        joinColumns = @JoinColumn(name="user_id"),
        inverseJoinColumns = @JoinColumn(name="role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;

    public User(String userName, String email, String phoneNumber, String password) {
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}
