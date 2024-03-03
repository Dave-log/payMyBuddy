package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
}
