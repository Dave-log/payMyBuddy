package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.dto.UserRegisterDTO;
import com.payMyBuddy.payMyBuddy.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:home";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login_page";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "error_page";
    }

    @GetMapping("/register")
    public String getRegistrationPage() {
        return "registration_page";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registerDTO") UserRegisterDTO userRegisterDTO, Model model) {
        if (authService.existsByEmail(userRegisterDTO.email())) {
            model.addAttribute("registerError", "Email already taken!");
            return "registration_page";
        }

        authService.register(userRegisterDTO);

        return "redirect:login?success=true";
    }
}
