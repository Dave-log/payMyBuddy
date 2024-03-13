package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.dto.BankAccountRegisterDTO;
import com.payMyBuddy.payMyBuddy.exceptions.BankAccountNotFoundException;
import com.payMyBuddy.payMyBuddy.model.BankAccount;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.BankAccountRepository;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserService userService;

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository, UserService userService) {
        this.bankAccountRepository = bankAccountRepository;
        this.userService = userService;
    }

    public BankAccount getBankAccount(long id) {
        return bankAccountRepository.findById(id).orElse(null);
    }

    public Iterable<BankAccount> getBankAccounts() {
        return bankAccountRepository.findAll();
    }

    @Transactional
    public void addBankAccount(BankAccountRegisterDTO bankAccountRegisterDTO) {
        User currentUser = userService.getCurrentUser();

        BankAccount newBankAccount = new BankAccount();
        newBankAccount.setUser(currentUser);
        newBankAccount.setAccountNumber(bankAccountRegisterDTO.accountNumber());
        newBankAccount.setAccountType(bankAccountRegisterDTO.accountType());
        newBankAccount.setIban(bankAccountRegisterDTO.iban());
        newBankAccount.setBic(bankAccountRegisterDTO.bic());
        newBankAccount.setBankName(bankAccountRegisterDTO.bankName());

        currentUser.getBankAccounts().add(newBankAccount);
        System.out.println("jusque là ça marche (add new BA to user BA list");
        bankAccountRepository.save(newBankAccount);
        System.out.println("jusque là ça marche (save du BA dans BA repo");
        userService.update(currentUser);
        System.out.println("jusque là ça marche (update du user");
    }

    @Transactional
    public void removeBankAccount(String iban) {
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findByIban(iban);

        if (optionalBankAccount.isEmpty()) {
            throw new BankAccountNotFoundException("Bank account not found (iban provided: " + iban + ")");
        }

        bankAccountRepository.delete(optionalBankAccount.get());
    }
}
