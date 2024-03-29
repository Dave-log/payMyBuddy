package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.dto.UserRegisterDTO;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existsByEmail(String email) { return userRepository.existsByEmail(email); }

    public void register(UserRegisterDTO registerDTO) {

        User user = new User();
        user.setFirstname(registerDTO.firstName());
        user.setLastname(registerDTO.lastName());
        user.setEmail(registerDTO.email());
        user.setPassword(passwordEncoder.encode(registerDTO.password()));

        userRepository.save(user);
    }
}
