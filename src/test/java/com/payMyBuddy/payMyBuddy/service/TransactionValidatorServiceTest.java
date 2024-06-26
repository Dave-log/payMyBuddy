package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.enums.TransactionType;
import com.payMyBuddy.payMyBuddy.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionValidatorServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private TransactionValidatorService validatorService;

    @Test
    public void testIsValidTransaction_ValidBankTransaction() {
        // Arrange
        User user = createTestUser();
        BankAccount bankAccount = createTestBankAccount();
        bankAccount.setUser(user);

        BankTransaction deposit = new BankTransaction();
        deposit.setType(TransactionType.DEPOSIT);
        deposit.setSender(user);
        deposit.setRecipientBank(bankAccount);

        BankTransaction withdrawal = new BankTransaction();
        withdrawal.setType(TransactionType.WITHDRAWAL);
        withdrawal.setSender(user);
        withdrawal.setRecipientBank(bankAccount);

        BigDecimal amount = new BigDecimal("50.00");

        // Act
        when(userService.getUser(anyLong())).thenReturn(user);
        when(bankAccountService.getBankAccount(anyLong())).thenReturn(bankAccount);
        when(bankAccountService.isBankAccountOwnedByUser(user, bankAccount)).thenReturn(true);
        // Assert
        assertTrue(validatorService.isValidTransaction(deposit, amount));
        assertTrue(validatorService.isValidTransaction(withdrawal, amount));
    }

    @Test
    public void testIsValidTransaction_SenderIsNull() {
        // Arrange
        User user = createTestUser();
        BankAccount bankAccount = createTestBankAccount();
        User buddy = createTestBuddy();

        BankTransaction deposit = new BankTransaction();
        deposit.setType(TransactionType.DEPOSIT);
        deposit.setSender(user);
        deposit.setRecipientBank(bankAccount);

        BankTransaction withdrawal = new BankTransaction();
        withdrawal.setType(TransactionType.WITHDRAWAL);
        withdrawal.setSender(user);
        withdrawal.setRecipientBank(bankAccount);

        BuddyTransaction transfer = new BuddyTransaction();
        transfer.setType(TransactionType.TRANSFER);
        transfer.setSender(user);
        transfer.setRecipientUser(buddy);

        BigDecimal amount = new BigDecimal("50.00");

        // Act
        when(userService.getUser(anyLong())).thenReturn(null);

        // Assert
        assertFalse(validatorService.isValidTransaction(deposit, amount));
        assertFalse(validatorService.isValidTransaction(withdrawal, amount));
        assertFalse(validatorService.isValidTransaction(transfer, amount));
    }

    @Test
    public void testIsValidTransaction_RecipientIsNull() {
        // Arrange
        User user = createTestUser();
        BankAccount bankAccount = createTestBankAccount();
        User buddy = createTestBuddy();

        BankTransaction deposit = new BankTransaction();
        deposit.setType(TransactionType.DEPOSIT);
        deposit.setSender(user);
        deposit.setRecipientBank(bankAccount);

        BankTransaction withdrawal = new BankTransaction();
        withdrawal.setType(TransactionType.WITHDRAWAL);
        withdrawal.setSender(user);
        withdrawal.setRecipientBank(bankAccount);

        BuddyTransaction transfer = new BuddyTransaction();
        transfer.setType(TransactionType.TRANSFER);
        transfer.setSender(user);
        transfer.setRecipientUser(buddy);

        BigDecimal amount = new BigDecimal("50.00");

        // Act
        when(userService.getUser(user.getId())).thenReturn(user);
        when(userService.getUser(buddy.getId())).thenReturn(null);
        when(bankAccountService.getBankAccount(anyLong())).thenReturn(null);

        // Assert
        assertFalse(validatorService.isValidTransaction(deposit, amount));
        assertFalse(validatorService.isValidTransaction(withdrawal, amount));
        assertFalse(validatorService.isValidTransaction(transfer, amount));
    }

    @Test
    public void testIsValidTransaction_IncorrectRecipient() {
        // Arrange
        User user = createTestUser();
        BankAccount bankAccount = createTestBankAccount();
        User buddy = createTestBuddy();

        BankTransaction deposit = new BankTransaction();
        deposit.setType(TransactionType.DEPOSIT);
        deposit.setSender(user);
        deposit.setRecipientBank(bankAccount);

        BankTransaction withdrawal = new BankTransaction();
        withdrawal.setType(TransactionType.WITHDRAWAL);
        withdrawal.setSender(user);
        withdrawal.setRecipientBank(bankAccount);

        BigDecimal amount = new BigDecimal("50.00");

        // Act
        when(userService.getUser(user.getId())).thenReturn(user);
        when(bankAccountService.getBankAccount(anyLong())).thenReturn(bankAccount);

        // Assert
        assertFalse(validatorService.isValidTransaction(deposit, amount));
        assertFalse(validatorService.isValidTransaction(withdrawal, amount));
    }

    @Test
    public void testIsValidTransaction_InsufficientBalance() {
        // Arrange
        User user = createTestUser();
        BankAccount bankAccount = createTestBankAccount();
        User buddy = createTestBuddy();

        user.setBuddies(Set.of(buddy));
        bankAccount.setUser(user);

        BankTransaction deposit = new BankTransaction();
        deposit.setType(TransactionType.DEPOSIT);
        deposit.setSender(user);
        deposit.setRecipientBank(bankAccount);

        BuddyTransaction transfer = new BuddyTransaction();
        transfer.setType(TransactionType.TRANSFER);
        transfer.setSender(user);
        transfer.setRecipientUser(buddy);

        BigDecimal amount = new BigDecimal("200.00");

        // Act
        when(userService.getUser(user.getId())).thenReturn(user);
        when(userService.getUser(buddy.getId())).thenReturn(buddy);
        when(bankAccountService.getBankAccount(anyLong())).thenReturn(bankAccount);

        // Assert
        assertFalse(validatorService.isValidTransaction(deposit, amount));
        assertFalse(validatorService.isValidTransaction(transfer, amount));
    }

    @Test
    public void testIsValidTransaction_ValidBuddyTransaction() {
        // Arrange
        User user = createTestUser();
        User buddy = createTestBuddy();
        user.setBuddies(Set.of(buddy));

        BuddyTransaction transfer = new BuddyTransaction();
        transfer.setType(TransactionType.TRANSFER);
        transfer.setSender(user);
        transfer.setRecipientUser(buddy);

        BigDecimal amount = new BigDecimal("50.00");

        // Act
        when(userService.getUser(user.getId())).thenReturn(user);
        when(userService.getUser(buddy.getId())).thenReturn(buddy);

        // Assert
        assertTrue(validatorService.isValidTransaction(transfer, amount));
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setBalance(new BigDecimal("100.00"));
        return user;
    }

    private BankAccount createTestBankAccount() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(1L);
        return bankAccount;
    }

    private User createTestBuddy() {
        User buddy = new User();
        buddy.setId(2L);
        buddy.setBalance(new BigDecimal("0.00"));
        return buddy;
    }
}
