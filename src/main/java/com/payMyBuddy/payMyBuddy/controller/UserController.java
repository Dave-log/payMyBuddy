package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("all")
    public Iterable<User> getUsers() {
        return userService.getUsers();
    }
}
