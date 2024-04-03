package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.dto.*;
import com.payMyBuddy.payMyBuddy.model.BankAccount;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.service.BankAccountService;
import com.payMyBuddy.payMyBuddy.service.TransactionService;
import com.payMyBuddy.payMyBuddy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;

    public ProfileController(
            UserService userService,
            BankAccountService bankAccountService,
            TransactionService transactionService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
        this.transactionService = transactionService;
    }

    @GetMapping()
    public String getProfilePage(Model model) {
        User currentUser = userService.getCurrentUser();
        List<BuddyDTO> buddies = userService.getBuddyDTOs();
        List<BankAccount> bankAccounts = bankAccountService.getBankAccounts();

        model.addAttribute("parentPages", new String[]{"home"});
        model.addAttribute("currentPage", "profile");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("buddies", buddies);
        model.addAttribute("bankAccounts", bankAccounts);

        model.addAttribute("profileUpdateDTO", new ProfileUpdateDTO("",""));
        model.addAttribute("selectedBuddy", new BuddyDTO("", "", ""));

        return "profile_page";
    }

    @PostMapping("/update-profile")
    public String updateProfile(
            @ModelAttribute("profileUpdateDTO") ProfileUpdateDTO profileUpdateDTO,
            Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        userService.updateProfile(profileUpdateDTO);

        return "profile_page";
    }

    @PostMapping("/update-password")
    public String updatePassword(
            @ModelAttribute("passwordUpdateDTO") PasswordUpdateDTO passwordUpdateDTO,
            Model model,
            HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

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

    @PostMapping("/add-buddy")
    public String addBuddy(@RequestParam("email") String email) {
        userService.addBuddy(email);
        return "redirect:/profile#buddies";
    }

    @PostMapping("/remove-buddy")
    public String removeBuddy(@RequestParam("buddyEmail") String buddyEmail) {
        userService.removeBuddy(buddyEmail);
        return "redirect:/profile#buddies";
    }

    @PostMapping("/bank-transaction")
    public String makeBankTransaction(
            @ModelAttribute("bankTransactionRequestDTO") BankTransactionRequestDTO bankTransactionRequestDTO,
            Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        transactionService.makeBankTransaction(bankTransactionRequestDTO);

        return "redirect:/profile#bank-accounts";
    }

    @PostMapping("/add-bank-account")
    public String addBankAccount(
            @ModelAttribute("BankAccountRegisterDTO")BankAccountRegisterDTO bankAccountRegisterDTO,
            Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        bankAccountService.addBankAccount(bankAccountRegisterDTO);

        return "redirect:/profile#bank-accounts";
    }

    @PostMapping("/remove-bank-account")
    public String removeBankAccount(@RequestParam("iban") String iban) {
        bankAccountService.removeBankAccount(iban);
        return "redirect:/profile#bank-accounts";
    }
}
