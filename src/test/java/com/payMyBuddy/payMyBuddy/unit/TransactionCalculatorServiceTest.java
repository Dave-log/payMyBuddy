package com.payMyBuddy.payMyBuddy.unit;

import com.payMyBuddy.payMyBuddy.enums.TransactionType;
import com.payMyBuddy.payMyBuddy.model.BankTransaction;
import com.payMyBuddy.payMyBuddy.model.BuddyTransaction;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.service.TransactionCalculatorService;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TransactionCalculatorServiceTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionCalculatorService calculatorService;

    @Test
    public void calculateFee_WhenAmountIsPositive() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");

        // Act
        BigDecimal fee = calculatorService.calculateFee(amount);

        // Assert
        BigDecimal expectedFee = new BigDecimal("0.50"); // Fee must be 0.5% from amount
        assertEquals(expectedFee, fee);
    }

    @Test
    public void updateBalances_WhenDeposit() {
        // Arrange
        Transaction transaction = new BankTransaction();
        transaction.setType(TransactionType.DEPOSIT);
        User sender = new User();
        sender.setBalance(new BigDecimal("100.00"));
        transaction.setSender(sender);
        BigDecimal amountPlusFee = new BigDecimal("10.00");

        // Act
        calculatorService.updateBalances(transaction, amountPlusFee, null);

        // Assert
        verify(userService).update(sender);
        BigDecimal expectedBalance = new BigDecimal("90.00");
        assertEquals(expectedBalance, sender.getBalance());
    }

    @Test
    public void updateBalances_WhenWithdrawal() {
        // Arrange
        Transaction transaction = new BankTransaction();
        transaction.setType(TransactionType.WITHDRAWAL);
        User sender = new User();
        sender.setBalance(new BigDecimal("100.00"));
        transaction.setSender(sender);
        BigDecimal amountMinusFee = new BigDecimal("10.00");

        // Act
        calculatorService.updateBalances(transaction, null, amountMinusFee);

        // Assert
        verify(userService).update(sender);
        BigDecimal expectedBalance = new BigDecimal("110.00");
        assertEquals(expectedBalance, sender.getBalance());
    }

    @Test
    public void updateBalances_WhenTransfer() {
        // Arrange
        BuddyTransaction transaction = new BuddyTransaction();
        transaction.setType(TransactionType.TRANSFER);
        User sender = new User();
        User recipient = new User();
        sender.setBalance(new BigDecimal("100.00"));
        recipient.setBalance(new BigDecimal("10.00"));
        transaction.setSender(sender);
        transaction.setRecipientUser(recipient);
        BigDecimal amountPlusFee = new BigDecimal("20.00");
        BigDecimal amountMinusFee = new BigDecimal("20.00");

        // Act
        calculatorService.updateBalances(transaction, amountPlusFee, amountMinusFee);

        // Assert
        verify(userService).update(sender);
        verify(userService).update(recipient);
        BigDecimal expectedSenderBalance = new BigDecimal("80.00");
        BigDecimal expectedRecipientBalance = new BigDecimal("30.00");
        assertEquals(expectedSenderBalance, sender.getBalance());
        assertEquals(expectedRecipientBalance, recipient.getBalance());
    }
}
