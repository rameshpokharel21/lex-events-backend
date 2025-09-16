package com.ramesh.lex_events.services;

import com.ramesh.lex_events.dto.response.UserResponse;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.EmailVerificationRepository;
import com.ramesh.lex_events.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;


    @Override
    public User registerUser(User user) {
        return userRepository.save(user);
    }


    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUserNameIgnoreCase(username);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        //clear roles
        user.getRoles().clear();

        //delete relate email verifications first
        emailVerificationRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUserNameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("user not found with username: " + username));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getUserId(),
                        user.getUserName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getRoles().stream()
                                .map(role -> role.getRoleName().name())//map roles to strings
                                .collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
    }


}
