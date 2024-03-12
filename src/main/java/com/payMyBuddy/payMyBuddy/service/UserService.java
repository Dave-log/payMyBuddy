package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.exceptions.UserNotFoundException;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUserToBuddyList(User user, String buddyEmail) {

        User buddyToAdd = userRepository.findByEmail(buddyEmail);

        //if (buddyToAdd != null && userRepository.)
    }

    public User get(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    public User update(User user) {
        User userToUpdate = userRepository.findByEmail(user.getEmail());
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
