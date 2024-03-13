package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.dto.BuddyTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("{id}")
    public Transaction getTransaction(@PathVariable long id) { return transactionService.getTransaction(id); }

    @GetMapping("all")
    public Iterable<Transaction> getTransactions() { return transactionService.getTransactions(); }

    @PostMapping("transfer")
    public void makeTransfer(@RequestBody BuddyTransactionRequestDTO buddyTransactionRequestDTO) {
        transactionService.makeTransfer(buddyTransactionRequestDTO);
    }

}
