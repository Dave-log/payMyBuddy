package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.model.CustomUserDetails;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service class implementing Spring Security's UserDetailsService interface
 * to provide custom user details retrieval.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs a new CustomUserDetailsService instance with the specified UserRepository dependency.
     *
     * @param userRepository the repository for user-related operations
     */
    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) { this.userRepository = userRepository; }

    /**
     * Retrieves user details by username (email).
     *
     * @param email the email (username) of the user to retrieve
     * @return the UserDetails representation of the user
     * @throws UsernameNotFoundException if no user exists with the provided email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + email + " not found"));
        return new CustomUserDetails(user);
    }
}
