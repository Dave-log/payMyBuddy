package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.dto.BuddyDTO;
import com.payMyBuddy.payMyBuddy.dto.PasswordUpdateDTO;
import com.payMyBuddy.payMyBuddy.dto.ProfileUpdateDTO;
import com.payMyBuddy.payMyBuddy.exceptions.BuddyAlreadyInBuddyListException;
import com.payMyBuddy.payMyBuddy.exceptions.BuddyNotFoundInBuddyListException;
import com.payMyBuddy.payMyBuddy.exceptions.UserNotFoundException;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class providing user-related operations.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructs a new UserService instance with the specified dependencies.
     *
     * @param userRepository   the repository for user data access
     * @param passwordEncoder the password encoder
     */
    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @return the current user
     * @throws UserNotFoundException if the user is not authenticated or does not exist
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            return getUser(email);
        } else {
            throw new UserNotFoundException("User is not authenticated or does not exist");
        }
    }

    /**
     * Retrieves a list of buddies for the current user.
     *
     * @return the list of buddy DTOs
     */
    public List<BuddyDTO> getBuddyDTOs() {
        User currentUser = getCurrentUser();
        List<User> buddyList = new ArrayList<>(currentUser.getBuddies());

        return buddyList.stream()
                .map(buddy -> new BuddyDTO(buddy.getEmail(), buddy.getFirstName(), buddy.getLastName()))
                .sorted(Comparator.comparing(BuddyDTO::lastName))
                .collect(Collectors.toList());
    }

    /**
     * Adds a buddy to the current user's buddy list.
     *
     * @param buddyEmail the email of the buddy to add
     */
    public void addBuddy(String buddyEmail) {
        User currentUser = getCurrentUser();
        User buddyToAdd = getUser(buddyEmail);
        if (currentUser.getBuddies().contains(buddyToAdd)) {
            throw new BuddyAlreadyInBuddyListException("Buddy already in buddy list (email provided: " + buddyEmail + ")");
        } else {
            currentUser.getBuddies().add(buddyToAdd);
            userRepository.save(currentUser);
        }
    }

    /**
     * Removes a buddy from the current user's buddy list.
     *
     * @param buddyEmail the email of the buddy to remove
     */
    public void removeBuddy(String buddyEmail) {
        User currentUser = getCurrentUser();
        User buddyToRemove = getUser(buddyEmail);

        if (currentUser.getBuddies().contains(buddyToRemove)) {
            currentUser.getBuddies().remove(buddyToRemove);
            userRepository.save(currentUser);
        } else {
            throw new BuddyNotFoundInBuddyListException("Buddy not found in buddy list (email provided: " + buddyEmail + ")");
        }
    }

    /**
     * Checks if a given user is a buddy of the current user.
     *
     * @param currentUser the current user
     * @param recipient   the user to check
     * @return true if the recipient is a buddy of the current user, false otherwise
     */
    public boolean isRecipientBuddyOfCurrentUser(User currentUser, User recipient) {
        return currentUser.getBuddies().contains(recipient);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user with the specified ID
     * @throws UserNotFoundException if no user exists with the provided ID
     */
    public User getUser(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser
                .orElseThrow(() -> new UserNotFoundException("User does not exist (id provided: " + id + ")"));
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email the email of the user to retrieve
     * @return the user with the specified email
     * @throws UserNotFoundException if no user exists with the provided email
     */
    public User getUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser
                .orElseThrow(() -> new UserNotFoundException("User does not exist (email provided: " + email + ")"));
    }

    /**
     * Retrieves all users.
     *
     * @return an Iterable of all users
     */
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Updates the profile of the current user.
     *
     * @param profileUpdateDTO the DTO containing the updated profile information
     */
    public void updateProfile(ProfileUpdateDTO profileUpdateDTO) {
        User currentUser = getCurrentUser();

        if (!profileUpdateDTO.firstNameInput().isEmpty()) {
            currentUser.setFirstName(profileUpdateDTO.firstNameInput());
        }

        if (!profileUpdateDTO.lastNameInput().isEmpty()) {
            currentUser.setLastName(profileUpdateDTO.lastNameInput());
        }

        userRepository.save(currentUser);
    }

    /**
     * Updates the password of the current user.
     *
     * @param passwordUpdateDTO the DTO containing the new and current passwords
     * @throws IllegalArgumentException if the current password is incorrect, the new password is the same as the current password,
     *                                  or the new password does not match the confirm password
     */
    public void updatePassword(PasswordUpdateDTO passwordUpdateDTO) {
        User currentUser = getCurrentUser();

        // check if current password is correct
        if (!passwordEncoder.matches(passwordUpdateDTO.currentPassword(), currentUser.getPassword())) {
            throw new IllegalArgumentException("Incorrect current password");
        }
        // check if new password and current password are different
        if (passwordEncoder.matches(passwordUpdateDTO.newPassword(), currentUser.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the current password");
        }
        // check if new password and confirm password are the same
        if (!passwordUpdateDTO.newPassword().equals(passwordUpdateDTO.confirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        String encodedPassword = passwordEncoder.encode(passwordUpdateDTO.newPassword());
        currentUser.setPassword(encodedPassword);

        userRepository.save(currentUser);
    }

    /**
     * Updates the specified user.
     *
     * @param user the user to update
     * @return the updated user
     */
    public User update(User user) {
        User userToUpdate = getUser(user.getEmail());
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
        userToUpdate.setRole(user.getRole());
        userToUpdate.setBalance(user.getBalance());
        userToUpdate.setBuddies(user.getBuddies());

        return userRepository.save(userToUpdate);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     */
    public void delete(long id) {
        userRepository.delete(getUser(id));
    }
}
