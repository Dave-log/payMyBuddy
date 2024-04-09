package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.dto.*;
import com.payMyBuddy.payMyBuddy.model.BankAccount;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.service.BankAccountService;
import com.payMyBuddy.payMyBuddy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class responsible for handling requests related to the user's profile.
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final BankAccountService bankAccountService;

    /**
     * Constructs a new ProfileController instance with the specified dependencies.
     *
     * @param userService        the service responsible for user-related operations
     * @param bankAccountService the service responsible for bank account-related operations
     */
    public ProfileController(
            UserService userService,
            BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    /**
     * Displays the profile page with the current user's information, buddies, and bank accounts.
     *
     * @param model the model to which attributes will be added
     * @return the profile page view
     */
    @GetMapping()
    public String getProfilePage(Model model) {
        User currentUser = userService.getCurrentUser();
        List<BuddyDTO> buddies = userService.getBuddyDTOs();
        List<BankAccount> bankAccounts = bankAccountService.getUserBankAccounts(currentUser);

        model.addAttribute("parentPages", new String[]{"home"});
        model.addAttribute("currentPage", "profile");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("buddies", buddies);
        model.addAttribute("bankAccounts", bankAccounts);

        model.addAttribute("profileUpdateDTO", new ProfileUpdateDTO("",""));
        model.addAttribute("selectedBuddy", new BuddyDTO("", "", ""));

        return "profile_page";
    }

    /**
     * Updates the profile information of the current user.
     *
     * @param profileUpdateDTO the DTO containing the updated profile information
     * @param model            the model to which attributes will be added
     * @return the profile page view
     */
    @PostMapping("/update-profile")
    public String updateProfile(
            @ModelAttribute("profileUpdateDTO") ProfileUpdateDTO profileUpdateDTO,
            Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        userService.updateProfile(profileUpdateDTO);

        return "profile_page";
    }

    /**
     * Updates the password of the current user.
     *
     * @param passwordUpdateDTO the DTO containing the updated password information
     * @param model             the model to which attributes will be added
     * @param request           the HTTP servlet request
     * @return the profile page view if the password update is successful; otherwise, redirects to the login page with an error message
     */
    @PostMapping("/update-password")
    public String updatePassword(
            @ModelAttribute("passwordUpdateDTO") PasswordUpdateDTO passwordUpdateDTO,
            Model model,
            HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);
        System.out.println("ALERT PASSWORD CHANGE : " + passwordUpdateDTO.newPassword());
        try {
            userService.updatePassword(passwordUpdateDTO);
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

    /**
     * Adds a buddy to the current user's list of buddies.
     *
     * @param email the email of the buddy to add
     * @return a redirect to the profile page with the buddies section scrolled into view
     */
    @PostMapping("/add-buddy")
    public String addBuddy(@RequestParam("email") String email) {
        userService.addBuddy(email);
        return "redirect:/profile#buddies";
    }

    /**
     * Removes a buddy from the current user's list of buddies.
     *
     * @param buddyEmail the email of the buddy to remove
     * @return a redirect to the profile page with the buddies section scrolled into view
     */
    @PostMapping("/remove-buddy")
    public String removeBuddy(@RequestParam("buddyEmail") String buddyEmail) {
        userService.removeBuddy(buddyEmail);
        return "redirect:/profile#buddies";
    }

    /**
     * Adds a bank account for the current user.
     *
     * @param bankAccountRegisterDTO the DTO containing the bank account information to add
     * @param model                   the model to which attributes will be added
     * @return a redirect to the profile page with the bank accounts section scrolled into view
     */
    @PostMapping("/add-bank-account")
    public String addBankAccount(
            @ModelAttribute("BankAccountRegisterDTO")BankAccountRegisterDTO bankAccountRegisterDTO,
            Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        bankAccountService.addBankAccount(bankAccountRegisterDTO);

        return "redirect:/profile#bank-accounts";
    }

    /**
     * Removes a bank account from the current user's bank accounts.
     *
     * @param iban the IBAN of the bank account to remove
     * @return a redirect to the profile page with the bank accounts section scrolled into view
     */
    @PostMapping("/remove-bank-account")
    public String removeBankAccount(@RequestParam("iban") String iban) {
        bankAccountService.removeBankAccount(iban);
        return "redirect:/profile#bank-accounts";
    }
}
