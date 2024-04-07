package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.constants.TransactionConst;
import com.payMyBuddy.payMyBuddy.enums.TransactionType;
import com.payMyBuddy.payMyBuddy.model.BuddyTransaction;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service class responsible for calculating transaction fees and updating user balances accordingly.
 */
@Service
public class TransactionCalculatorService {

    private final UserService userService;

    /**
     * Constructs a new TransactionCalculatorService instance with the specified UserService dependency.
     *
     * @param userService the service for user-related operations
     */
    @Autowired
    public TransactionCalculatorService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Calculates the transaction fee based on the given amount.
     *
     * @param amount the transaction amount
     * @return the calculated fee
     */
    public BigDecimal calculateFee(BigDecimal amount) {
        BigDecimal fee = amount.multiply(TransactionConst.FEE_PERCENTAGE);
        fee = fee.setScale(2, RoundingMode.HALF_UP);
        return fee;
    }

    /**
     * Updates the balances of sender and recipient users based on the transaction details.
     *
     * @param transaction    the transaction details
     * @param amountPlusFee  the total amount including fee
     * @param amountMinusFee the amount after deducting the fee
     */
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
