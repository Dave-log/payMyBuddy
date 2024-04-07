package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.dto.UserRegisterDTO;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for authentication-related operations.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new AuthService instance with the specified dependencies.
     *
     * @param userRepository  the repository for user-related operations
     * @param passwordEncoder the encoder for password hashing
     */
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Checks if a user with the given email exists.
     *
     * @param email the email to check for existence
     * @return true if a user with the given email exists; otherwise, false
     */
    public boolean existsByEmail(String email) { return userRepository.existsByEmail(email); }

    /**
     * Registers a new user with the provided information.
     *
     * @param registerDTO the DTO containing user registration information
     */
    public void register(UserRegisterDTO registerDTO) {

        User user = new User();
        user.setFirstName(registerDTO.firstName());
        user.setLastName(registerDTO.lastName());
        user.setEmail(registerDTO.email());
        user.setPassword(passwordEncoder.encode(registerDTO.password()));

        userRepository.save(user);
    }
}
