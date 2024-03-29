package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.dto.BankTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.dto.BuddyTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.exceptions.InvalidTransactionException;
import com.payMyBuddy.payMyBuddy.exceptions.TransactionNotFoundException;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.service.TransactionService;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @Autowired
    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getTransaction(@PathVariable long id) {
        try {
            return ResponseEntity.ok(transactionService.getTransaction(id));
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    @ResponseBody
    public Iterable<Transaction> getTransactions() { return transactionService.getTransactions(); }

    @PostMapping("/buddy-transaction")
    public String transfer(@ModelAttribute("postTransaction") BuddyTransactionRequestDTO buddyTransactionRequestDTO, Model model) {
        User currentUser = userService.getCurrentUser();

        try {
            transactionService.transfer(currentUser, buddyTransactionRequestDTO);
            model.addAttribute("successMessage", "Transaction successful!");

            return "redirect:/transfer?success";
        } catch (InvalidTransactionException e) {
            model.addAttribute("errorMessage", e.getMessage());
            System.out.println("error : "+e.getMessage());
            return "redirect:/error";
        }
    }

    @PostMapping("/bank-transaction")
    public ResponseEntity<String> makeBankTransaction(@RequestBody BankTransactionRequestDTO bankTransactionRequestDTO) {
        try {
            transactionService.makeBankTransaction(bankTransactionRequestDTO);
            return ResponseEntity.ok("Bank transaction successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteTransaction(@PathVariable long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok("Transaction deleted successfully");
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
