package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.model.BankAccount;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/currentBuddies")
    public Set<User> getCurrentUserBuddies() {
        User currentUser = userService.getCurrentUser();
        return currentUser.getBuddies();
    }

    @GetMapping("/currentBankAccounts")
    public List<BankAccount> getCurrentUserBankAccounts() {
        User currentUser = userService.getCurrentUser();
        return currentUser.getBankAccounts();
    }

    @GetMapping("{id}")
    public User getUser(@PathVariable long id) {
        return userService.getUser(id);
    }

    @GetMapping("all")
    public Iterable<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("{id}/buddies")
    public Set<User> getUserBuddies(@PathVariable long id) {
        return userService.getUser(id).getBuddies();
    }

    @PutMapping()
    public User update(@RequestBody User user) { return userService.update(user); }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) { userService.delete(id);}

    @PostMapping("add-buddy/{email}")
    public void addBuddy(@PathVariable String email) {
        userService.addBuddy(email);
    }

    @DeleteMapping("remove-buddy/{email}")
    public void removeBuddy(@PathVariable String email) {
        userService.removeBuddy(email);
    }

    @GetMapping("/transactions")
    public List<Transaction> getUserTransactions() {
        User currentUser = userService.getCurrentUser();
        return currentUser.getTransactions();
    }
}
