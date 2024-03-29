package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.model.BankAccount;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionValidatorService {

    private final UserService userService;
    private final BankAccountService bankAccountService;

    @Autowired
    public TransactionValidatorService(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    public boolean isValidTransaction(Transaction transaction, BigDecimal amount) {
        User user = userService.getUser(transaction.getSender().getId());
        long recipientId = transaction.getRecipientId();

        return switch (transaction.getType()) {
            case DEPOSIT -> {
                BankAccount bankAccount = bankAccountService.getBankAccount(recipientId);
                yield user != null
                        && bankAccount != null
                        && bankAccountService.isBankAccountOwnedByUser(user, bankAccount)
                        && isSufficientBalance(user, amount);
            }
            case WITHDRAWAL -> {
                BankAccount bankAccount = bankAccountService.getBankAccount(recipientId);
                yield user != null
                        && bankAccount != null
                        && bankAccountService.isBankAccountOwnedByUser(user, bankAccount);
            }
            case TRANSFER -> {
                User buddy = userService.getUser(recipientId);
                yield user != null
                        && buddy != null
                        && isSufficientBalance(user, amount);
            }
        };
    }

    private boolean isSufficientBalance(User user, BigDecimal amount) {
        return user.getBalance().compareTo(amount) >= 0;
    }
}
