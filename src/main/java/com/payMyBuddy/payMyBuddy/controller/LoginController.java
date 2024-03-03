package com.payMyBuddy.payMyBuddy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @GetMapping("/userlog")
    public String helloUser() {
        return "Welcome, User";
    }

    @GetMapping("/adminlog")
    public String helloAdmin() {
        return "Welcome, Admin";
    }
}
