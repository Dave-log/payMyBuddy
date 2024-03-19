package com.payMyBuddy.payMyBuddy.unit;

import com.payMyBuddy.payMyBuddy.dto.UserRegisterDTO;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    @Test
    public void existsByEmail_EmailExists() {
        String email = "johndoe@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean exists = authService.existsByEmail(email);

        assertTrue(exists);
    }

    @Test
    public void existsByEmail_EmailDoesNotExist() {
        String email = "johndoe@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        boolean exists = authService.existsByEmail(email);

        assertFalse(exists);
    }

    @Test
    public void register_Successful() {
        UserRegisterDTO registerDTO = new UserRegisterDTO(
                "John",
                "Doe",
                "johndoe@example.com",
                "password");
        when(passwordEncoder.encode(registerDTO.password())).thenReturn("encodedPassword");

        authService.register(registerDTO);

        verify(userRepository, times(1)).save(any(User.class));
    }

}
