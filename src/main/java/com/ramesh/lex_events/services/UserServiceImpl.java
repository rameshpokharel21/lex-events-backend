package com.ramesh.lex_events.services;

import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OtpService otpService;

    public UserServiceImpl(UserRepository userRepository, OtpService otpService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
    }


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

        user.getRoles().clear();
        userRepository.delete(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUserNameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("user not found with username: " + username));
    }

    @Override
    public String sendOtpForVerification(String phoneNumber) {
        return otpService.generateOtp(phoneNumber);
    }

    @Override
    public boolean verifyPhoneNumber(String phoneNumber, String otp) {
        boolean verified = otpService.verifyOtp(phoneNumber, otp);
        if(verified){
            userRepository.findByPhoneNumber(phoneNumber).ifPresent(user -> {
                user.setIsPhoneVerified(true);
                userRepository.save(user);
            });
        }
        return verified;
    }




}
