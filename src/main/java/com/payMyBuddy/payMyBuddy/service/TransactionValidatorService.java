package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.model.BankAccount;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionValidatorService {

    private final UserService userService;
    private final BankAccountService bankAccountService;

    @Autowired
    public TransactionValidatorService(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    public boolean isValidTransaction(Transaction transaction, long recipientId, BigDecimal amount) {
        User user = userService.getUser(transaction.getSender().getId());

        return switch (transaction.getType()) {
            case DEPOSIT -> {
                BankAccount bankAccount = bankAccountService.getBankAccount(recipientId);
                yield user != null
                        && bankAccount != null
                        && isBankAccountOwnedByUser(user, bankAccount)
                        && isPositiveAmount(amount)
                        && isSufficientBalance(user, amount);
            }
            case WITHDRAWAL -> {
                BankAccount bankAccount = bankAccountService.getBankAccount(recipientId);
                yield user != null
                        && bankAccount != null
                        && isBankAccountOwnedByUser(user, bankAccount)
                        && isPositiveAmount(amount);
            }
            case TRANSFER -> {
                User buddy = userService.getUser(recipientId);
                yield user != null
                        && buddy != null
                        && isBuddyInUserFriendList(user, buddy)
                        && isPositiveAmount(amount)
                        && isSufficientBalance(user, amount);
            }
        };
    }

    private boolean isBuddyInUserFriendList(User user, User buddy) {
        return user.getBuddies().contains(buddy);
    }

    private boolean isBankAccountOwnedByUser(User user, BankAccount bankAccount) {
        return user.getBankAccounts().contains(bankAccount);
    }

    private boolean isSufficientBalance(User user, BigDecimal amount) {
        return user.getBalance().compareTo(amount) >= 0;
    }

    private boolean isPositiveAmount(BigDecimal amount) {
        return amount.signum() > 0;
    }
}
