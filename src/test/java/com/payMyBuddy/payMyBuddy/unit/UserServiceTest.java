package com.payMyBuddy.payMyBuddy.unit;

import com.payMyBuddy.payMyBuddy.exceptions.BuddyAlreadyInBuddyListException;
import com.payMyBuddy.payMyBuddy.exceptions.BuddyNotFoundInBuddyListException;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
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

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("johndoe@example.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

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

    @Test
    public void removeBuddy_Successful() {
        // Arrange
        User currentUser = new User();
        currentUser.setEmail("johndoe@example.com");

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("johndoe@example.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        User buddyToRemove = new User();
        buddyToRemove.setEmail("janedoe@example.com");
        currentUser.getBuddies().add(buddyToRemove);

        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail(buddyToRemove.getEmail())).thenReturn(Optional.of(buddyToRemove));

        // Act
        userService.removeBuddy(buddyToRemove.getEmail());

        // Assert
        assertFalse(currentUser.getBuddies().contains(buddyToRemove));
        verify(userRepository, times(1)).save(currentUser);
    }

    @Test
    public void removeBuddy_BuddyNotFoundInList() {
        // Arrange
        User currentUser = new User();
        currentUser.setEmail("johndoe@example.com");

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("johndoe@example.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        User buddyToRemove = new User();
        buddyToRemove.setEmail("janedoe@example.com");

        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail(buddyToRemove.getEmail())).thenReturn(Optional.of(buddyToRemove));

        // Act & Assert
        assertThrows(BuddyNotFoundInBuddyListException.class, () -> userService.removeBuddy(buddyToRemove.getEmail()));
        verify(userRepository, never()).save(currentUser);
    }

    @Test
    public void getUser_UserExists() {
        long id = 1L;
        User user = new User();
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User retrievedUser = userService.getUser(id);

        assertEquals(id, retrievedUser.getId());
    }

    @Test
    public void getUser_UserNotFound_WithId() {
        long nonExistentId = 0L;
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(nonExistentId));
    }

    @Test
    public void getUser_UserNotFound_WithEmail() {
        String nonExistingEmail = "fake@example.com";
        when(userRepository.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(nonExistingEmail));
    }

    @Test
    public void getUsers() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("johndoe@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("janedoe@example.com");

        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        Iterable<User> actualUsers = userService.getUsers();

        // Assert
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void update_ValidUser() {
        // Arrange
        User userToUpdate = new User();
        userToUpdate.setEmail("johndoe@example.com");

        User updatedUser = new User();
        updatedUser.setEmail("janedoe@example.com");

        when(userRepository.findByEmail(userToUpdate.getEmail())).thenReturn(Optional.of(userToUpdate));
        when(userRepository.save(userToUpdate)).thenReturn(updatedUser);

        // Act
        User result = userService.update(userToUpdate);

        // Assert
        assertEquals(updatedUser, result);
    }

    @Test
    public void delete() {
        long userId = 1L;
        User userToDelete = new User();
        userToDelete.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));

        userService.delete(userId);

        verify(userRepository).delete(userToDelete);
    }
}
