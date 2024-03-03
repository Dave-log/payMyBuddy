package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.constants.TransactionConstants;
import com.payMyBuddy.payMyBuddy.enums.TransactionStatus;
import com.payMyBuddy.payMyBuddy.exceptions.InvalidTransactionException;
import com.payMyBuddy.payMyBuddy.exceptions.InvalidTransactionStatusException;
import com.payMyBuddy.payMyBuddy.exceptions.TransactionNotFoundException;
import com.payMyBuddy.payMyBuddy.model.BankAccount;
import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionValidatorService validatorService;
    private final TransactionCalculatorService calculatorService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              TransactionValidatorService transactionValidator,
                              TransactionCalculatorService calculatorService) {
        this.transactionRepository = transactionRepository;
        this.validatorService = transactionValidator;
        this.calculatorService = calculatorService;
    }

    @Transactional
    public void performTransaction(Transaction transaction) {
        // First we need to check if recipient is a BankAccount or a User in order to get the id.
        Object recipient = transaction.getRecipient();
        long recipientId = recipient instanceof User ? ((User) recipient).getId() : ((BankAccount) recipient).getId();

        // Next we calculate the fee and adjust the amount in the case where it is the user who pays the fee.
        calculatorService.calculateFee(transaction);
        BigDecimal amount = transaction.getAmount();
        BigDecimal amountPlusFee = transaction.isFeePaidBySender() ? amount.add(transaction.getFee()) : amount;
        BigDecimal amountMinusFee = transaction.isFeePaidBySender() ? amount : amount.subtract(transaction.getFee());

        // Then we check if the transaction is valid by throwing an exception if it is not.
        if (!validatorService.isValidTransaction(transaction, recipientId, amountPlusFee)) {
            changeTransactionStatus(transaction, TransactionStatus.FAILED);
            throw new InvalidTransactionException("Invalid transaction");
        }

        // Finally we perform calculations on user balances
        calculatorService.updateBalances(transaction, amountPlusFee, amountMinusFee);

        // TODO : The status of the transaction has to be PENDING or PROCESSING in some use cases instead of COMPLETED
        changeTransactionStatus(transaction, TransactionStatus.COMPLETED);
    }

    public void cancelTransaction(Transaction transaction) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(transaction.getId());
        if (transactionOptional.isEmpty()) {
            throw new TransactionNotFoundException("Transaction not found with id: " + transaction.getId());
        }

        Transaction existingTransaction = transactionOptional.get();
        TransactionStatus currentStatus = existingTransaction.getStatus();
        if (currentStatus != TransactionStatus.PENDING && currentStatus != TransactionStatus.PROCESSING) {
            throw new InvalidTransactionStatusException("Transaction cannot be cancelled as it is not in PENDING or PROCESSING state");
        }

        changeTransactionStatus(transaction, TransactionStatus.CANCELLED);
    }

    private void changeTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }

    public Transaction saveTransaction(Transaction transaction) { return transactionRepository.save(transaction); }

    public Optional<Transaction> getTransaction(long id) { return transactionRepository.findById(id); }

    public Iterable<Transaction> getTransactions() { return transactionRepository.findAll(); }

    public void deleteTransaction(Transaction transaction) { transactionRepository.delete(transaction); }
}
