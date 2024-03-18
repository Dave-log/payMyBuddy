package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.constants.TransactionConstants;
import com.payMyBuddy.payMyBuddy.enums.TransactionType;
import com.payMyBuddy.payMyBuddy.model.BuddyTransaction;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TransactionCalculatorService {

    private final UserService userService;

    @Autowired
    public TransactionCalculatorService(UserService userService) {
        this.userService = userService;
    }

    public BigDecimal calculateFee(BigDecimal amount) {
        BigDecimal fee = amount.multiply(TransactionConstants.FEE_PERCENTAGE);
        fee = fee.setScale(2, RoundingMode.HALF_UP);
        return fee;
    }

    public void updateBalances(Transaction transaction, BigDecimal amountPlusFee, BigDecimal amountMinusFee) {
        User sender = transaction.getSender();
        User recipient = new User();

        if (transaction.getType().equals(TransactionType.TRANSFER)) {
            recipient = ((BuddyTransaction) transaction).getRecipientUser();
        }

        BigDecimal senderBalance = sender.getBalance();

        switch (transaction.getType()) {
            case DEPOSIT -> {
                sender.setBalance(senderBalance.subtract(amountPlusFee));
            }
            case WITHDRAWAL -> {
                sender.setBalance(senderBalance.add(amountMinusFee));
            }
            case TRANSFER -> {
                sender.setBalance(senderBalance.subtract(amountPlusFee));
                recipient.setBalance(recipient.getBalance().add(amountMinusFee));
                userService.update(recipient);
            }
        };

        userService.update(sender);
    }
}
