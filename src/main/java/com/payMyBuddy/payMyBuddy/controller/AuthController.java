package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.dto.UserRegisterDTO;
import com.payMyBuddy.payMyBuddy.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class responsible for handling requests related to authentication and registration.
 */
@Controller
public class AuthController {

    private final AuthService authService;

    /**
     * Constructs a new AuthController instance with the specified AuthService dependency.
     *
     * @param authService the service responsible for authentication and registration operations
     */
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;;
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

    /**
     * Registers a new user based on the provided user registration data.
     * If the email is already taken, it adds an error message to the model and returns the registration page.
     * Otherwise, it registers the user and redirects to the login page with a success message.
     *
     * @param userRegisterDTO the DTO containing the user registration data
     * @param model           the model to which error messages can be added
     * @return a redirection to the login page with a success message if registration is successful,
     *         or the registration page with an error message if the email is already taken
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registerDTO") UserRegisterDTO userRegisterDTO, Model model) {
        if (authService.existsByEmail(userRegisterDTO.email())) {
            model.addAttribute("registerError", "Email already taken!");
            return "registration_page";
        }

        authService.register(userRegisterDTO);

        return "redirect:login?success=true";
    }

    /**
     * Handles the logout functionality by invalidating the current session.
     * After invalidating the session, redirects the user to the login page.
     *
     * @param request the HttpServletRequest object containing the current request
     * @return a redirection string to the login page
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "redirect:login";
    }
}
