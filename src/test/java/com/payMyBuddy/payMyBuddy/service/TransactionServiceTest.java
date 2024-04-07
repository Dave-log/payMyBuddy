package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.dto.BankTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.dto.BuddyTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.dto.TransferDTO;
import com.payMyBuddy.payMyBuddy.enums.TransactionType;
import com.payMyBuddy.payMyBuddy.exceptions.InvalidTransactionException;
import com.payMyBuddy.payMyBuddy.exceptions.TransactionNotFoundException;
import com.payMyBuddy.payMyBuddy.model.*;
import com.payMyBuddy.payMyBuddy.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionValidatorService validatorService;
    @Mock
    private TransactionCalculatorService calculatorService;
    @Mock
    private UserService userService;
    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void testGetTransferDTOs() {
        // Arrange
        User currentUser = new User();
        currentUser.setId(1L);
        when(userService.getCurrentUser()).thenReturn(currentUser);

        User recipient = new User();
        recipient.setFirstName("John");
        recipient.setLastName("Doe");

        Pageable pageable = PageRequest.of(0, 10);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(createTransaction("Transaction 1", BigDecimal.valueOf(50)));
        transactions.add(createTransaction("Transaction 2", BigDecimal.valueOf(100)));

        Page<Transaction> page = new PageImpl<>(transactions, pageable, transactions.size());
        when(transactionRepository.findBySenderAndType(currentUser, TransactionType.TRANSFER, pageable)).thenReturn(page);
        when(userService.getUser(anyLong())).thenReturn(recipient);

        // Act
        Page<TransferDTO> transferDTOPage = transactionService.getTransferDTOs(pageable);

        // Assert
        assertEquals(2, transferDTOPage.getContent().size());
        // You can add more assertions to verify the content of each TransferDTO if needed
    }

    @Test
    public void transfer_ValidTransaction() {
        // Arrange
        BuddyTransactionRequestDTO requestDTO = new BuddyTransactionRequestDTO(
                "Description",
                "buddy@example.com",
                new BigDecimal("100.00"),
                true);
        User currentUser = new User();
        User buddy = new User();
        currentUser.setBuddies(Set.of(buddy));

        when(userService.getUser("buddy@example.com")).thenReturn(buddy);
        when(userService.isRecipientBuddyOfCurrentUser(currentUser, buddy)).thenReturn(true);
        when(calculatorService.calculateFee(any(BigDecimal.class))).thenReturn(BigDecimal.ZERO);
        when(validatorService.isValidTransaction(any(BuddyTransaction.class), any(BigDecimal.class))).thenReturn(true);

        // Act
        transactionService.transfer(currentUser, requestDTO);

        // Assert
        verify(userService, times(1)).getUser("buddy@example.com");
        verify(validatorService, times(1)).isValidTransaction(any(BuddyTransaction.class), any(BigDecimal.class));
        verify(calculatorService, times(1)).calculateFee(any(BigDecimal.class));
        verify(transactionRepository, times(1)).save(any(BuddyTransaction.class));
    }

    @Test
    void transfer_RecipientNotBuddy() {
        // Arrange
        BuddyTransactionRequestDTO requestDTO = new BuddyTransactionRequestDTO(
                "Description",
                "buddy@example.com",
                new BigDecimal("100.00"),
                true);
        User currentUser = new User();
        User buddy = new User();

        when(userService.getUser("buddy@example.com")).thenReturn(buddy);

        // Act & Assert
        assertThrows(InvalidTransactionException.class, () -> transactionService.transfer(currentUser, requestDTO));

        // Verify
        verify(userService, times(1)).getUser("buddy@example.com");
        verifyNoInteractions(validatorService, calculatorService, transactionRepository);
    }

    @Test
    void transfer_NegativeAmount() {
        // Arrange & Assert
        assertThrows(IllegalArgumentException.class, () -> new BuddyTransactionRequestDTO(
                "Description",
                "buddy@example.com",
                new BigDecimal("-100.00"),
                true));

        verifyNoInteractions(validatorService, calculatorService, transactionRepository);
    }

    @Test
    public void transfer_ZeroAmount() {
        // Arrange & Assert
        assertThrows(IllegalArgumentException.class, () -> new BuddyTransactionRequestDTO(
                "Description",
                "buddy@example.com",
                new BigDecimal("0.00"),
                true));

        verifyNoInteractions(validatorService, calculatorService, transactionRepository);
    }

    @Test
    public void makeBankTransaction_ValidTransaction() {
        // Arrange
        BankTransactionRequestDTO requestDTO = new BankTransactionRequestDTO(
                1L,
                "Description",
                "DEPOSIT",
                new BigDecimal("100.00"),
                true);
        User currentUser = new User();
        BankAccount bankAccount = new BankAccount();
        bankAccount.setUser(currentUser);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(bankAccountService.getBankAccount(1L)).thenReturn(bankAccount);
        when(bankAccountService.isBankAccountOwnedByUser(currentUser, bankAccount)).thenReturn(true);
        when(calculatorService.calculateFee(any(BigDecimal.class))).thenReturn(BigDecimal.ZERO);
        when(validatorService.isValidTransaction(any(BankTransaction.class), any(BigDecimal.class))).thenReturn(true);

        // Act
        transactionService.makeBankTransaction(requestDTO);

        // Assert
        verify(userService, times(1)).getCurrentUser();
        verify(bankAccountService, times(1)).getBankAccount(1L);
        verify(validatorService, times(1)).isValidTransaction(any(BankTransaction.class), any(BigDecimal.class));
        verify(calculatorService, times(1)).calculateFee(any(BigDecimal.class));
        verify(transactionRepository, times(1)).save(any(BankTransaction.class));
    }

    @Test
    public void makeBankTransaction_InvalidRecipient() {
        // Arrange
        BankTransactionRequestDTO requestDTO = new BankTransactionRequestDTO(
                1L,
                "Description",
                "DEPOSIT",
                new BigDecimal("100.00"),
                true);
        User currentUser = new User();
        BankAccount bankAccount = new BankAccount();

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(bankAccountService.getBankAccount(1L)).thenReturn(bankAccount);

        // Act & Assert
        assertThrows(InvalidTransactionException.class, () -> transactionService.makeBankTransaction(requestDTO));
        verify(userService, times(1)).getCurrentUser();
        verify(bankAccountService, times(1)).getBankAccount(1L);
        verifyNoInteractions(validatorService, calculatorService, transactionRepository);
    }

    @Test
    public void makeBankTransaction_NonPositiveAmount() {
        // Arrange
        BankTransactionRequestDTO requestDTO = new BankTransactionRequestDTO(
                1L,
                "Description",
                "DEPOSIT",
                BigDecimal.ZERO,
                true);
        User currentUser = new User();
        BankAccount bankAccount = new BankAccount();
        bankAccount.setUser(currentUser);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(bankAccountService.getBankAccount(1L)).thenReturn(bankAccount);

        // Act & Assert
        assertThrows(InvalidTransactionException.class, () -> transactionService.makeBankTransaction(requestDTO));

        // Verify
        verify(userService, times(1)).getCurrentUser();
        verify(bankAccountService, times(1)).getBankAccount(1L);
        verifyNoInteractions(calculatorService, transactionRepository);
    }

    @Test
    public void performTransaction_ValidTransaction() {
        // Arrange
        Transaction transaction = new BankTransaction();
        BigDecimal amount = new BigDecimal("100.00");
        transaction.setAmount(amount);
        transaction.setFeePaidBySender(true);
        BigDecimal fee = new BigDecimal("0.50");
        BigDecimal amountPlusFee = amount.add(fee);

        when(calculatorService.calculateFee(amount)).thenReturn(fee);
        when(validatorService.isValidTransaction(transaction, amountPlusFee)).thenReturn(true);

        // Act
        transactionService.performTransaction(transaction);

        // Assert
        verify(calculatorService, times(1)).calculateFee(amount);
        verify(validatorService, times(1)).isValidTransaction(transaction, amountPlusFee);
        verify(calculatorService, times(1)).updateBalances(transaction, amountPlusFee, amount);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    public void performTransaction_ValidTransactionWhenRecipientPaysFee() {
        // Arrange
        Transaction transaction = new BankTransaction();
        BigDecimal amount = new BigDecimal("100.00");
        transaction.setAmount(amount);
        transaction.setFeePaidBySender(false);
        BigDecimal fee = new BigDecimal("0.50");
        BigDecimal amountMinusFee = amount.subtract(fee);

        when(calculatorService.calculateFee(amount)).thenReturn(fee);
        when(validatorService.isValidTransaction(transaction, amount)).thenReturn(true);

        // Act
        transactionService.performTransaction(transaction);

        // Assert
        verify(calculatorService, times(1)).calculateFee(amount);
        verify(validatorService, times(1)).isValidTransaction(transaction, amount);
        verify(calculatorService, times(1)).updateBalances(transaction, amount, amountMinusFee);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    public void performTransaction_InvalidTransaction() {
        // Arrange
        Transaction transaction = new BankTransaction();
        BigDecimal amount = new BigDecimal("100.00");
        transaction.setAmount(amount);
        transaction.setFeePaidBySender(true);
        BigDecimal fee = new BigDecimal("0.50");
        BigDecimal amountPlusFee = amount.add(fee);

        when(calculatorService.calculateFee(amount)).thenReturn(fee);
        when(validatorService.isValidTransaction(transaction, amountPlusFee)).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidTransactionException.class, () -> transactionService.performTransaction(transaction));

        verify(calculatorService, times(1)).calculateFee(amount);
        verify(validatorService, times(1)).isValidTransaction(transaction, amountPlusFee);
        verify(calculatorService, never()).updateBalances(transaction, amountPlusFee, amount);
        verify(transactionRepository, never()).save(transaction);
    }

    @Test
    public void saveTransaction() {
        // Arrange
        Transaction transaction = new BankTransaction();
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // Act
        Transaction savedTransaction = transactionService.saveTransaction(transaction);

        // Assert
        assertNotNull(savedTransaction);
        assertEquals(transaction, savedTransaction);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    public void getTransaction_ExistingId() {
        // Arrange
        long id = 1L;
        Transaction expectedTransaction = new BankTransaction();
        when(transactionRepository.findById(id)).thenReturn(Optional.of(expectedTransaction));

        // Act
        Transaction transaction = transactionService.getTransaction(id);

        // Assert
        assertNotNull(transaction);
        assertEquals(expectedTransaction, transaction);
        verify(transactionRepository, times(1)).findById(id);
    }

    @Test
    public void getTransaction_NonExistingId() {
        long id = 1L;
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionService.getTransaction(id));
        verify(transactionRepository, times(1)).findById(id);
    }

    @Test
    public void testGetUserTransactions() {
        // Arrange
        User currentUser = new User();
        List<Transaction> transactions = new ArrayList<>();

        transactions.add(new BankTransaction());

        when(transactionRepository.findBySender(currentUser)).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getUserTransactions(currentUser);

        // Assert
        assertEquals(transactions, result);
    }

    @Test
    public void getTransactions_Successful() {
        // Arrange
        List<Transaction> expectedTransactions = new ArrayList<>();
        expectedTransactions.add(new BankTransaction());
        expectedTransactions.add(new BuddyTransaction());
        when(transactionRepository.findAll()).thenReturn(expectedTransactions);

        // Act
        Iterable<Transaction> transactions = transactionService.getTransactions();

        // Assert
        assertNotNull(transactions);
        assertEquals(expectedTransactions.size(), ((List<Transaction>) transactions).size());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void getTransactions_EmptyList() {
        when(transactionRepository.findAll()).thenReturn(new ArrayList<>());

        Iterable<Transaction> transactions = transactionService.getTransactions();

        assertNotNull(transactions);
        assertFalse(transactions.iterator().hasNext());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void deleteTransaction_ExistingTransaction() {
        long id = 1L;
        Transaction transaction = new BankTransaction();
        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(id);

        verify(transactionRepository, times(1)).delete(transaction);
    }

    @Test
    public void deleteTransaction_NonExistingTransaction() {
        long id = 1L;
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionService.deleteTransaction(id));

        verify(transactionRepository, never()).delete(any());
    }

    private BuddyTransaction createTransaction(String description, BigDecimal amount) {
        BuddyTransaction transaction = new BuddyTransaction();
        User sender = new User();
        sender.setId(1L);
        User recipient = new User();
        recipient.setId(2L);
        recipient.setFirstName("John");
        recipient.setLastName("Doe");
        transaction.setSender(sender);
        transaction.setRecipientUser(recipient);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.TRANSFER);
        return transaction;
    }
}
