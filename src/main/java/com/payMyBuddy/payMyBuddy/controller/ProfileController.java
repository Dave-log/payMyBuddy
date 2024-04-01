package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.dto.ProfileUpdateDTO;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) { this.userService = userService; }

    @GetMapping()
    public String getProfilePage(Model model) {
        User currentUser = userService.getCurrentUser();

        model.addAttribute("parentPages", new String[]{"home"});
        model.addAttribute("currentPage", "profile");
        model.addAttribute("currentUser", currentUser);

        return "profile_page";
    }

    @PostMapping()
    public String updatePassword(
            @ModelAttribute("profileUpdateDTO") ProfileUpdateDTO profileUpdateDTO,
            Model model,
            HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        try {
            userService.updatePassword(profileUpdateDTO);
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            return "redirect:/login?passwordChanged=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "profile_page";
        }
    }

}
