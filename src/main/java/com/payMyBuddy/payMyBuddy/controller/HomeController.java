package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/Home")
    public String getHomePage(Model model) {
        User currentUser = userService.getCurrentUser();

        model.addAttribute("currentPage", "Home");
        model.addAttribute("firstName", currentUser.getFirstname());
        model.addAttribute("lastName", currentUser.getLastname());
        model.addAttribute("balance", currentUser.getBalance());

        return "home_page";
    }

    @GetMapping("/Transfer")
    public String getTransferPage(Model model) {
        model.addAttribute("parentPages", new String[]{"Home"});
        model.addAttribute("currentPage", "Transfer");
        return "transfer_page";
    }

    @GetMapping("/Profile")
    public String getProfilePage(Model model) {
        model.addAttribute("parentPages", new String[]{"Home"});
        model.addAttribute("currentPage", "Profile");
        return "profile_page";
    }

    @GetMapping("/Contact")
    public String getContactPage(Model model) {
        model.addAttribute("parentPages", new String[]{"Home"});
        model.addAttribute("currentPage", "Contact");
        return "contact_page";
    }

    @GetMapping("/Add-buddy")
    public String getAddBuddyPage(Model model) {
        model.addAttribute("parentPages", new String[]{"Home", "Profile"});
        model.addAttribute("currentPage", "Add-buddy");
        return "addBuddy_page";
    }

    @GetMapping("/Add-bank-account")
    public String getAddBankAccountPage(Model model) {
        model.addAttribute("parentPages", new String[]{"Home", "Profile"});
        model.addAttribute("currentPage", "Add-bank-account");
        return "addBankAccount_page";
    }
}
