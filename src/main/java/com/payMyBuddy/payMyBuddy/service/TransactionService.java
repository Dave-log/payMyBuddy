package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.dto.BankTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.dto.BuddyTransactionRequestDTO;
import com.payMyBuddy.payMyBuddy.enums.TransactionType;
import com.payMyBuddy.payMyBuddy.exceptions.BankAccountNotFoundException;
import com.payMyBuddy.payMyBuddy.exceptions.InvalidTransactionException;
import com.payMyBuddy.payMyBuddy.exceptions.InvalidTransactionStatusException;
import com.payMyBuddy.payMyBuddy.exceptions.TransactionNotFoundException;
import com.payMyBuddy.payMyBuddy.model.*;
import com.payMyBuddy.payMyBuddy.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionValidatorService validatorService;
    private final TransactionCalculatorService calculatorService;
    private final UserService userService;
    private final BankAccountService bankAccountService;

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

    public Transaction transfer(BuddyTransactionRequestDTO buddyTransactionRequestDTO) {
        User currentUser = userService.getCurrentUser();
        String recipientEmail = buddyTransactionRequestDTO.recipientEmail();
        User recipientUser = userService.getUser(recipientEmail);

        if (!currentUser.getBuddies().contains(recipientUser)) {
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

    public Transaction makeBankTransaction(BankTransactionRequestDTO bankTransactionRequestDTO) {
        User currentUser = userService.getCurrentUser();
        BankAccount bankAccount = bankAccountService.getBankAccount(bankTransactionRequestDTO.id());

        if (!currentUser.getBankAccounts().contains(bankAccount)) {
            throw new BankAccountNotFoundException("Recipient is not your bank account");
        }

        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setDescription(bankTransactionRequestDTO.description());
        bankTransaction.setType(bankTransactionRequestDTO.type());
        bankTransaction.setSender(currentUser);
        bankTransaction.setRecipientBank(bankAccount);
        bankTransaction.setAmount(bankTransactionRequestDTO.amount());
        bankTransaction.setFeePaidBySender(bankTransactionRequestDTO.feePaidBySender());

        return performTransaction(bankTransaction);
    }

    private Transaction performTransaction(Transaction transaction) {
        // First we calculate the fee and adjust the amount in the case where it is the user who pays the fee.
        calculatorService.calculateFee(transaction);
        BigDecimal amount = transaction.getAmount();
        BigDecimal amountPlusFee = transaction.isFeePaidBySender() ? amount.add(transaction.getFee()) : amount;
        BigDecimal amountMinusFee = transaction.isFeePaidBySender() ? amount : amount.subtract(transaction.getFee());

        // Then we check if the transaction is valid by throwing an exception if it is not.
        if (!validatorService.isValidTransaction(transaction, amountPlusFee)) {
            throw new InvalidTransactionException("Invalid transaction");
        }

        // Finally we perform calculations on user balances
        calculatorService.updateBalances(transaction, amountPlusFee, amountMinusFee);

        return saveTransaction(transaction);
    }

    public Transaction saveTransaction(Transaction transaction) { return transactionRepository.save(transaction); }

    public Transaction getTransaction(long id) { return transactionRepository.findById(id).orElse(null); }

    public Iterable<Transaction> getTransactions() { return transactionRepository.findAll(); }

    public void deleteTransaction(Transaction transaction) { transactionRepository.delete(transaction); }
}
