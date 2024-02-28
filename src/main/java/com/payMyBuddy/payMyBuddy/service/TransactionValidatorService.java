package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.model.BankAccount;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.User;
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

    public boolean isValidTransaction(Transaction transaction, int recipientId, BigDecimal amount) {
        Optional<User> userOptional = userService.getUser(transaction.getSender().getId());

        return switch (transaction.getType()) {
            case DEPOSIT -> {
                Optional<BankAccount> bankAccountOptional = bankAccountService.getBankAccount(recipientId);
                yield userOptional.isPresent()
                        && bankAccountOptional.isPresent()
                        && isBankAccountOwnedByUser(userOptional.get(), bankAccountOptional.get())
                        && isSufficientBalance(userOptional.get(), amount);
            }
            case WITHDRAWAL -> {
                Optional<BankAccount> bankAccountOptional = bankAccountService.getBankAccount(recipientId);
                yield userOptional.isPresent()
                        && bankAccountOptional.isPresent()
                        && isBankAccountOwnedByUser(userOptional.get(), bankAccountOptional.get());
            }
            case TRANSFER -> {
                Optional<User> buddyOptional = userService.getUser(recipientId);
                yield userOptional.isPresent()
                        && buddyOptional.isPresent()
                        && isBuddyInUserFriendList(userOptional.get(), buddyOptional.get())
                        && isSufficientBalance(userOptional.get(), amount);
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
