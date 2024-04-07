package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.dto.BankTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.dto.BuddyTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.exceptions.InvalidTransactionException;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.service.TransactionService;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class responsible for handling transactions.
 */
@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    /**
     * Constructs a new TransactionController instance with the specified dependencies.
     *
     * @param transactionService the service responsible for transaction-related operations
     * @param userService        the service responsible for user-related operations
     */
    @Autowired
    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    /**
     * Initiates a buddy transaction.
     *
     * @param buddyTransactionRequestDTO the DTO containing information about the buddy transaction
     * @param model                       the model to which attributes will be added
     * @return a redirect to the transfer page with a success message if the transaction is successful; otherwise, redirects to the error page with an error message
     */
    @PostMapping("/buddy-transaction")
    public String transfer(@ModelAttribute("postTransaction") BuddyTransactionRequestDTO buddyTransactionRequestDTO, Model model) {
        User currentUser = userService.getCurrentUser();

        try {
            transactionService.transfer(currentUser, buddyTransactionRequestDTO);
            model.addAttribute("successMessage", "Transaction successful!");

            return "redirect:/transfer?success";
        } catch (InvalidTransactionException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }
    }

    /**
     * Initiates a bank transaction.
     *
     * @param bankTransactionRequestDTO the DTO containing information about the bank transaction
     * @param model                     the model to which attributes will be added
     * @return a redirect to the profile page with the bank accounts section scrolled into view
     */
    @PostMapping("/bank-transaction")
    public String makeBankTransaction(
            @ModelAttribute("bankTransactionRequestDTO") BankTransactionRequestDTO bankTransactionRequestDTO,
            Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        transactionService.makeBankTransaction(bankTransactionRequestDTO);

        return "redirect:/profile#bank-accounts";
    }
}
