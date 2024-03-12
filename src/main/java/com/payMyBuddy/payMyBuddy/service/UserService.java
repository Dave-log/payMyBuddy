package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.exceptions.BuddyAlreadyInBuddyListException;
import com.payMyBuddy.payMyBuddy.exceptions.BuddyNotFoundInBuddyListException;
import com.payMyBuddy.payMyBuddy.exceptions.UserNotFoundException;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            System.out.println("email of currentUser : " + email);
            return getUser(email);
        } else {
            throw new UserNotFoundException("User is not authenticated or does not exist");
        }
    }

    public void addBuddy(String buddyEmail) {
        System.out.println("email avant instanciation des users : " + buddyEmail);
        User currentUser = getCurrentUser();
        System.out.println("email après instanciation du currentUser : " + buddyEmail);
        User buddyToAdd = getUser(buddyEmail);
        System.out.println("userService reached");
        if (currentUser.getBuddies().contains(buddyToAdd)) {
            throw new BuddyAlreadyInBuddyListException("Buddy already in buddy list (email provided: " + buddyEmail + ")");
        } else {
            currentUser.getBuddies().add(buddyToAdd);
            userRepository.save(currentUser);
            System.out.println("buddy saved");
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

    public User getUser(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UserNotFoundException("User does not exist (id provided: " + id + ")");
        }
    }

    public User getUser(String email) {
        System.out.println(email);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UserNotFoundException("User does not exist (email provided: " + email + ")");
        }
    }


    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    public User update(User user) {
        User userToUpdate = getUser(user.getEmail());
        if (userToUpdate != null) {
            userToUpdate.setFirstname(user.getFirstname());
            userToUpdate.setLastname(user.getLastname());
            userToUpdate.setPassword(user.getPassword());
            userToUpdate.setBalance(user.getBalance());
            userToUpdate.setRole(user.getRole());
            userToUpdate.setBankAccounts(user.getBankAccounts());
            userToUpdate.setTransactions(user.getTransactions());
            userToUpdate.setBuddies(user.getBuddies());
            return userRepository.save(userToUpdate);
        } else {
            throw new UserNotFoundException("No user found with this email : " + user.getEmail());
        }
    }

    public void delete(User user) {
        userRepository.delete(user);
    }
}
