package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.dto.BankTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.dto.BuddyTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Transaction getTransaction(@PathVariable long id) { return transactionService.getTransaction(id); }

    @GetMapping("/all")
    public Iterable<Transaction> getTransactions() { return transactionService.getTransactions(); }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody BuddyTransactionRequestDTO buddyTransactionRequestDTO) {
        transactionService.transfer(buddyTransactionRequestDTO);
        return ResponseEntity.ok("Transfer successful");
    }

    @PostMapping("/bank-transaction")
    public ResponseEntity<String> makeBankTransaction(@RequestBody BankTransactionRequestDTO bankTransactionRequestDTO) {
        transactionService.makeBankTransaction(bankTransactionRequestDTO);
        return ResponseEntity.ok("Bank transaction successful");
    }
}
