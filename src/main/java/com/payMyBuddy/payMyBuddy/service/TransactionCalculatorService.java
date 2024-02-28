package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.constants.TransactionConstants;
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

    public void calculateFee(Transaction transaction) {

        BigDecimal fee = transaction.getAmount().multiply(TransactionConstants.FEE_PERCENTAGE);
        fee = fee.setScale(2, RoundingMode.HALF_UP);
        transaction.setFee(fee);
    }

    public void updateBalances(Transaction transaction, BigDecimal amountPlusFee, BigDecimal amountMinusFee) {

        User sender = transaction.getSender();
        User recipientUser = new User();
        if (transaction.getRecipient() instanceof User) {
            recipientUser = (User) transaction.getRecipient();
        }

        BigDecimal userCurrentBalance = sender.getCurrentBalance();

        switch (transaction.getType()) {
            case DEPOSIT -> {
                sender.setCurrentBalance(userCurrentBalance.subtract(amountPlusFee));
                // TODO : make a deposit to the bankAccount via the bank API
                // TODO : transfer the fee to PayMyBuddy Account
            }
            case WITHDRAWAL -> {
                sender.setCurrentBalance(userCurrentBalance.add(amountMinusFee));
                // TODO : make a withdrawal to the bankAccount via the bank API
                // TODO : transfer the fee to PayMyBuddy Account
            }
            case TRANSFER -> {
                sender.setCurrentBalance(userCurrentBalance.subtract(amountPlusFee));
                recipientUser.setCurrentBalance(recipientUser.getCurrentBalance().add(amountMinusFee));
                // TODO : transfer the fee to PayMyBuddy Account
                userService.saveUser(recipientUser);
            }
        }
        ;
        userService.saveUser(sender);
    }
}
