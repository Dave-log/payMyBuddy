package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.model.BankAccount;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service class responsible for validating transactions.
 */
@Service
public class TransactionValidatorService {

    private final UserService userService;
    private final BankAccountService bankAccountService;

    /**
     * Constructs a new TransactionValidatorService instance with the specified dependencies.
     *
     * @param userService        the service for user-related operations
     * @param bankAccountService the service for bank account-related operations
     */
    @Autowired
    public TransactionValidatorService(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    /**
     * Checks whether the given transaction is valid.
     *
     * @param transaction the transaction to validate
     * @param amount      the transaction amount
     * @return true if the transaction is valid, false otherwise
     */
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

    /**
     * Checks whether the user has sufficient balance for the transaction.
     *
     * @param user   the user initiating the transaction
     * @param amount the transaction amount
     * @return true if the user has sufficient balance, false otherwise
     */
    private boolean isSufficientBalance(User user, BigDecimal amount) {
        return user.getBalance().compareTo(amount) >= 0;
    }
}
