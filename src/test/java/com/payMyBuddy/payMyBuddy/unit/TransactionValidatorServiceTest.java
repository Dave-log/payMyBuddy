package com.payMyBuddy.payMyBuddy.unit;

import com.payMyBuddy.payMyBuddy.enums.TransactionType;
import com.payMyBuddy.payMyBuddy.model.*;
import com.payMyBuddy.payMyBuddy.service.BankAccountService;
import com.payMyBuddy.payMyBuddy.service.TransactionValidatorService;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
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
        user.setBankAccounts(List.of(bankAccount));

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

        BuddyTransaction transfer = new BuddyTransaction();
        transfer.setType(TransactionType.TRANSFER);
        transfer.setSender(user);
        transfer.setRecipientUser(buddy);

        BigDecimal amount = new BigDecimal("50.00");

        // Act
        when(userService.getUser(user.getId())).thenReturn(user);
        when(userService.getUser(buddy.getId())).thenReturn(buddy);
        when(bankAccountService.getBankAccount(anyLong())).thenReturn(bankAccount);

        // Assert
        assertFalse(validatorService.isValidTransaction(deposit, amount));
        assertFalse(validatorService.isValidTransaction(withdrawal, amount));
        assertFalse(validatorService.isValidTransaction(transfer, amount));
    }

    @Test
    public void testIsValidTransaction_InsufficientBalance() {
        // Arrange
        User user = createTestUser();
        BankAccount bankAccount = createTestBankAccount();
        User buddy = createTestBuddy();

        user.setBuddies(Set.of(buddy));
        user.setBankAccounts(List.of(bankAccount));

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

    @Test
    public void testIsBuddyInUserFriendList() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        User user = new User();
        User realBuddy = new User();
        User unknownBuddy = new User();
        realBuddy.setEmail("realbuddy@example.com");
        unknownBuddy.setEmail("unknownbuddy@example.com");
        user.setBuddies(Set.of(realBuddy));

        // Act
        boolean result1 = (boolean) getIsBuddyInUserFriendListMethod().invoke(validatorService, user, realBuddy);
        boolean result2 = (boolean) getIsBuddyInUserFriendListMethod().invoke(validatorService, user, unknownBuddy);

        // Assert
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    public void testIsBankAccountOwnedByUser() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        User user = new User();
        BankAccount realBankAccount = new BankAccount();
        BankAccount fakeBankAccount = new BankAccount();
        realBankAccount.setBankName("A");
        fakeBankAccount.setBankName("B");
        user.setBankAccounts(List.of(realBankAccount));

        // Act
        boolean result1 = (boolean) getIsBankAccountOwnedByUserMethod().invoke(validatorService, user, realBankAccount);
        boolean result2 = (boolean) getIsBankAccountOwnedByUserMethod().invoke(validatorService, user, fakeBankAccount);

        // Assert
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    public void testIsSufficientBalance() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        User user = new User();
        user.setBalance(new BigDecimal("100.00"));
        BigDecimal amount1 = new BigDecimal("50.00");
        BigDecimal amount2 = new BigDecimal("150.00");
        BigDecimal amount3 = new BigDecimal("100.00");

        // Act
        boolean result1 = (boolean) getIsSufficientBalanceMethod().invoke(validatorService, user, amount1);
        boolean result2 = (boolean) getIsSufficientBalanceMethod().invoke(validatorService, user, amount2);
        boolean result3 = (boolean) getIsSufficientBalanceMethod().invoke(validatorService, user, amount3);

        // Assert
        assertTrue(result1);
        assertFalse(result2);
        assertTrue(result3);
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

    private Method getIsBuddyInUserFriendListMethod() throws NoSuchMethodException {
        Method method = TransactionValidatorService.class.getDeclaredMethod("isBuddyInUserFriendList", User.class, User.class);
        method.setAccessible(true);
        return method;
    }

    private Method getIsBankAccountOwnedByUserMethod() throws NoSuchMethodException {
        Method method = TransactionValidatorService.class.getDeclaredMethod("isBankAccountOwnedByUser", User.class, BankAccount.class);
        method.setAccessible(true);
        return method;
    }

    private Method getIsSufficientBalanceMethod() throws NoSuchMethodException {
        Method method = TransactionValidatorService.class.getDeclaredMethod("isSufficientBalance", User.class, BigDecimal.class);
        method.setAccessible(true);
        return method;
    }
}
