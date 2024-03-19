package com.payMyBuddy.payMyBuddy.unit;

import com.payMyBuddy.payMyBuddy.exceptions.BuddyAlreadyInBuddyListException;
import com.payMyBuddy.payMyBuddy.exceptions.UserNotFoundException;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void getCurrentUser_AuthenticatedUser() {
        // Arrange
        User user = new User();
        user.setEmail("johndoe@example.com");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("johndoe@example.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        // Act
        User currentUser = userService.getCurrentUser();

        // Assert
        assertNotNull(currentUser);
        assertEquals("johndoe@example.com", currentUser.getEmail());
    }

    @Test
    public void getCurrentUser_UnauthenticatedUser() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUser());
    }

    @Test
    public void getCurrentUser_AuthIsNull() {
        // Arrange
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUser());
    }

    @Test
    public void addBuddy_BuddyNotInList() {
        // Arrange
        User currentUser = new User();
        currentUser.setEmail("johndoe@example.com");
        User buddyToAdd = new User();
        buddyToAdd.setEmail("janedoe@example.com");

        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail(buddyToAdd.getEmail())).thenReturn(Optional.of(buddyToAdd));

        // Act
        userService.addBuddy(buddyToAdd.getEmail());

        // Assert
        assertTrue(currentUser.getBuddies().contains(buddyToAdd));
        verify(userRepository, times(1)).save(currentUser);
    }

    @Test
    public void addBuddy_BuddyAlreadyInList() {
        // Arrange
        User currentUser = new User();
        currentUser.setEmail("johndoe@example.com");
        User buddyToAdd = new User();
        buddyToAdd.setEmail("janedoe@example.com");
        currentUser.setBuddies(Set.of(buddyToAdd));

        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail(buddyToAdd.getEmail())).thenReturn(Optional.of(buddyToAdd));

        // Act & Assert
        assertThrows(BuddyAlreadyInBuddyListException.class, () -> userService.addBuddy(buddyToAdd.getEmail()));
        verify(userRepository, never()).save(currentUser);
    }

    @Test
    public void addBuddy_CurrentUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> userService.addBuddy("fake@example.com"));
        verify(userRepository, never()).save(any(User.class));
    }
}
