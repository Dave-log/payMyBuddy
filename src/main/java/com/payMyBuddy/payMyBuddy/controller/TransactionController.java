package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.dto.BankTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.dto.BuddyTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.exceptions.TransactionNotFoundException;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable long id) {
        try {
            return ResponseEntity.ok(transactionService.getTransaction(id));
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public Iterable<Transaction> getTransactions() { return transactionService.getTransactions(); }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody BuddyTransactionRequestDTO buddyTransactionRequestDTO) {
        try {
            transactionService.transfer(buddyTransactionRequestDTO);
            return ResponseEntity.ok("Transfer successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
    public ResponseEntity<String> deleteTransaction(@PathVariable long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok("Transaction deleted successfully");
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
