package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.dto.BankAccountRegisterDTO;
import com.payMyBuddy.payMyBuddy.model.BankAccount;
import com.payMyBuddy.payMyBuddy.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("bank-account")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("{id}")
    public BankAccount getBankAccount(@PathVariable long id) { return bankAccountService.getBankAccount(id); }

    @GetMapping("all")
    public Iterable<BankAccount> getBankAccounts() { return bankAccountService.getBankAccounts(); }

    @PostMapping("add")
    public void addBankAccount(@RequestBody BankAccountRegisterDTO bankAccount) {
        bankAccountService.addBankAccount(bankAccount);
    }

    @DeleteMapping("{iban}")
    public void removeBankAccount(@PathVariable String iban) {
        bankAccountService.removeBankAccount(iban);
    }

}
