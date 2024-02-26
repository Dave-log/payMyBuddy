package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.model.BankAccount;
import com.payMyBuddy.payMyBuddy.model.TransactionType;
import com.payMyBuddy.payMyBuddy.model.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionValidator {

    private final UserService userService;
    private final BankAccountService bankAccountService;

    public TransactionValidator(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    public boolean isValidTransaction(int userId, int recipientId, TransactionType transactionType, BigDecimal amount) {
        Optional<User> userOptional = userService.getUser(userId);

        return switch (transactionType) {
            case DEPOSIT, WITHDRAWAL -> {
                Optional<BankAccount> bankAccountOptional = bankAccountService.getBankAccount(recipientId);
                yield userOptional.isPresent() && bankAccountOptional.isPresent()
                        && isBankAccountOwnedByUser(userOptional.get(), bankAccountOptional.get())
                        && isSufficientBalance(userOptional.get(), amount);
            }
            case TRANSFER -> {
                Optional<User> buddyOptional = userService.getUser(recipientId);
                yield userOptional.isPresent() && buddyOptional.isPresent()
                        && isBuddyInUserFriendList(userOptional.get(), buddyOptional.get());
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
        return user.getCurrentBalance().compareTo(amount) >= 0;
    }
}
