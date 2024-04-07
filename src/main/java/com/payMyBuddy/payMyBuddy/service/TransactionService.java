package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.dto.BankTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.dto.BuddyTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.dto.TransferDTO;
import com.payMyBuddy.payMyBuddy.enums.TransactionType;
import com.payMyBuddy.payMyBuddy.exceptions.InvalidTransactionException;
import com.payMyBuddy.payMyBuddy.exceptions.TransactionNotFoundException;
import com.payMyBuddy.payMyBuddy.model.*;
import com.payMyBuddy.payMyBuddy.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service class responsible for handling transaction operations, including transfers between users
 * and bank transactions.
 */
@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionValidatorService validatorService;
    private final TransactionCalculatorService calculatorService;
    private final UserService userService;
    private final BankAccountService bankAccountService;

    /**
     * Constructs a new TransactionService instance with the specified dependencies.
     *
     * @param transactionRepository the repository for transaction data access
     * @param transactionValidator  the service for validating transactions
     * @param calculatorService     the service for calculating transaction fees and updating balances
     * @param userService           the service for user-related operations
     * @param bankAccountService    the service for bank account-related operations
     */
    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              TransactionValidatorService transactionValidator,
                              TransactionCalculatorService calculatorService,
                              UserService userService,
                              BankAccountService bankAccountService) {
        this.transactionRepository = transactionRepository;
        this.validatorService = transactionValidator;
        this.calculatorService = calculatorService;
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    /**
     * Retrieves a page of TransferDTO objects representing transfer transactions made by the current user.
     *
     * @param pageable the pagination information
     * @return a page of transfer DTOs
     */
    public Page<TransferDTO> getTransferDTOs(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        Page<Transaction> transactionPage = transactionRepository.findBySenderAndType(
                currentUser,
                TransactionType.TRANSFER,
                pageable);

        return transactionPage.map(transaction -> {
            User recipient = userService.getUser(transaction.getRecipientId());
            String connection = recipient.getFirstName() + " " + recipient.getLastName();
            String description = transaction.getDescription();
            BigDecimal amount = transaction.getAmount();
            return new TransferDTO(connection, description, amount);
        });
    }

    /**
     * Initiates a buddy transaction between the current user and another user.
     *
     * @param currentUser              the current user initiating the transaction
     * @param buddyTransactionRequestDTO the DTO containing transaction details
     * @return the created transaction
     * @throws InvalidTransactionException if the transaction is invalid
     */
    public Transaction transfer(User currentUser, BuddyTransactionRequestDTO buddyTransactionRequestDTO) {
        String recipientEmail = buddyTransactionRequestDTO.recipientEmail();
        User recipientUser = userService.getUser(recipientEmail);

        if (!userService.isRecipientBuddyOfCurrentUser(currentUser, recipientUser)) {
            throw new InvalidTransactionException("Recipient user is not your buddy");
        }

        BuddyTransaction buddyTransaction = new BuddyTransaction();
        buddyTransaction.setDescription(buddyTransactionRequestDTO.description());
        buddyTransaction.setType(TransactionType.TRANSFER);
        buddyTransaction.setSender(currentUser);
        buddyTransaction.setRecipientUser(recipientUser);
        buddyTransaction.setAmount(buddyTransactionRequestDTO.amount());
        buddyTransaction.setFeePaidBySender(buddyTransactionRequestDTO.feePaidBySender());

        return performTransaction(buddyTransaction);
    }

    /**
     * Initiates a bank transaction for the current user.
     *
     * @param bankTransactionRequestDTO the DTO containing transaction details
     * @return the created transaction
     * @throws InvalidTransactionException if the transaction is invalid
     */
    public Transaction makeBankTransaction(BankTransactionRequestDTO bankTransactionRequestDTO) {
        User currentUser = userService.getCurrentUser();
        BankAccount bankAccount = bankAccountService.getBankAccount(bankTransactionRequestDTO.getId());

        if (!bankAccountService.isBankAccountOwnedByUser(currentUser, bankAccount)) {
            throw new InvalidTransactionException("Recipient is not your bank account");
        }

        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setDescription(bankTransactionRequestDTO.getDescription());

        if (bankTransactionRequestDTO.getType().equals(TransactionType.DEPOSIT.name())) {
            bankTransaction.setType(TransactionType.DEPOSIT);
        } else {
            bankTransaction.setType(TransactionType.WITHDRAWAL);
        }

        bankTransaction.setSender(currentUser);
        bankTransaction.setRecipientBank(bankAccount);
        bankTransaction.setAmount(bankTransactionRequestDTO.getAmount());
        bankTransaction.setFeePaidBySender(bankTransactionRequestDTO.isFeePaidBySender());

        return performTransaction(bankTransaction);
    }

    /**
     * Performs the specified transaction, including fee calculation and balance updates.
     *
     * @param transaction the transaction to be performed
     * @return the saved transaction
     */
    Transaction performTransaction(Transaction transaction) {
        // First we calculate the fee and adjust the amount in the case where it is the user who pays the fee.
        BigDecimal amount = transaction.getAmount();
        BigDecimal fee = calculatorService.calculateFee(amount);
        BigDecimal amountPlusFee = transaction.isFeePaidBySender() ? amount.add(fee) : amount;
        BigDecimal amountMinusFee = transaction.isFeePaidBySender() ? amount : amount.subtract(fee);
        transaction.setFee(fee);

        // Then we check if the transaction is valid by throwing an exception if it is not.
        if (!validatorService.isValidTransaction(transaction, amountPlusFee)) {
            throw new InvalidTransactionException("Invalid transaction");
        }

        // Finally we perform calculations on user balances
        calculatorService.updateBalances(transaction, amountPlusFee, amountMinusFee);

        return saveTransaction(transaction);
    }


    /**
     * Saves the given transaction.
     *
     * @param transaction the transaction to be saved
     * @return the saved transaction
     */
    public Transaction saveTransaction(Transaction transaction) { return transactionRepository.save(transaction); }

    /**
     * Retrieves the transaction with the specified ID.
     *
     * @param id the ID of the transaction to retrieve
     * @return the transaction with the specified ID
     * @throws TransactionNotFoundException if the transaction is not found
     */
    public Transaction getTransaction(long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found (id provided : " + id + ")"));
    }

    /**
     * Retrieves the list of transactions initiated by the specified user.
     *
     * @param user the user whose transactions to retrieve
     * @return the list of transactions initiated by the user
     */
    public List<Transaction> getUserTransactions(User user) { return transactionRepository.findBySender(user); }

    /**
     * Retrieves all transactions.
     *
     * @return an iterable collection of all transactions
     */
    public Iterable<Transaction> getTransactions() { return transactionRepository.findAll(); }

    /**
     * Deletes the transaction with the specified ID.
     *
     * @param id the ID of the transaction to delete
     * @throws TransactionNotFoundException if the transaction is not found
     */
    public void deleteTransaction(long id) {
        Transaction transaction = getTransaction(id);
        transactionRepository.delete(transaction);
    }
}
