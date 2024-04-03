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

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            return getUser(email);
        } else {
            throw new UserNotFoundException("User is not authenticated or does not exist");
        }
    }

    public List<BuddyDTO> getBuddyDTOs() {
        User currentUser = getCurrentUser();
        List<User> buddyList = new ArrayList<>(currentUser.getBuddies());

        return buddyList.stream()
                .map(buddy -> new BuddyDTO(buddy.getEmail(), buddy.getFirstName(), buddy.getLastName()))
                .sorted(Comparator.comparing(BuddyDTO::lastName))
                .collect(Collectors.toList());
    }

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

    public boolean isRecipientBuddyOfCurrentUser(User currentUser, User recipient) {
        return currentUser.getBuddies().contains(recipient);
    }

    public User getUser(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser
                .orElseThrow(() -> new UserNotFoundException("User does not exist (id provided: " + id + ")"));
    }

    public User getUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser
                .orElseThrow(() -> new UserNotFoundException("User does not exist (email provided: " + email + ")"));
    }


    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

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

    public void delete(long id) {
        userRepository.delete(getUser(id));
    }
}
