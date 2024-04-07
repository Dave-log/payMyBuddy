package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.dto.BankAccountRegisterDTO;
import com.payMyBuddy.payMyBuddy.exceptions.BankAccountNotFoundException;
import com.payMyBuddy.payMyBuddy.model.BankAccount;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.BankAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class responsible for bank account-related operations.
 */
@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserService userService;

    /**
     * Constructs a new BankAccountService instance with the specified dependencies.
     *
     * @param bankAccountRepository the repository for bank account-related operations
     * @param userService           the service for user-related operations
     */
    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository, UserService userService) {
        this.bankAccountRepository = bankAccountRepository;
        this.userService = userService;
    }

    /**
     * Retrieves a bank account by its ID.
     *
     * @param id the ID of the bank account to retrieve
     * @return the bank account with the specified ID
     * @throws BankAccountNotFoundException if no bank account exists with the provided ID
     */
    public BankAccount getBankAccount(long id) {
        return bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException(" Bank account does not exist (id provided: " + id + ")"));
    }

    /**
     * Retrieves a bank account by its IBAN.
     *
     * @param iban the IBAN of the bank account to retrieve
     * @return the bank account with the specified IBAN
     * @throws BankAccountNotFoundException if no bank account exists with the provided IBAN
     */
    public BankAccount getBankAccount(String iban) {
        return bankAccountRepository.findByIban(iban)
                .orElseThrow(() -> new BankAccountNotFoundException(" Bank account does not exist (iban provided: " + iban + ")"));
    }

    /**
     * Retrieves all bank accounts associated with a user.
     *
     * @param user the user for whom to retrieve bank accounts
     * @return a list of bank accounts owned by the user
     */
    public List<BankAccount> getUserBankAccounts(User user) {
        return bankAccountRepository.findByUser(user);
    }

    /**
     * Retrieves all bank accounts.
     *
     * @return a list of all bank accounts
     */
    public List<BankAccount> getBankAccounts() {
        return bankAccountRepository.findAll();
    }

    /**
     * Checks if a bank account is owned by a specific user.
     *
     * @param user         the user to check
     * @param bankAccount the bank account to check ownership
     * @return true if the user owns the bank account; otherwise, false
     */
    public boolean isBankAccountOwnedByUser(User user, BankAccount bankAccount) {
        List<BankAccount> userBankAccounts = getUserBankAccounts(user);
        boolean isBankAccountOwnedByUser = false;
        for (BankAccount account : userBankAccounts) {
            if (account.equals(bankAccount)) {
                isBankAccountOwnedByUser = true;
                break;
            }
        }
        return isBankAccountOwnedByUser;
    }

    /**
     * Adds a new bank account for the current user.
     *
     * @param bankAccountRegisterDTO the DTO containing bank account registration information
     */
    @Transactional
    public void addBankAccount(BankAccountRegisterDTO bankAccountRegisterDTO) {
        User currentUser = userService.getCurrentUser();

        BankAccount newBankAccount = new BankAccount();
        newBankAccount.setUser(currentUser);
        newBankAccount.setAccountNumber(bankAccountRegisterDTO.accountNumber());
        newBankAccount.setAccountType(bankAccountRegisterDTO.accountType());
        newBankAccount.setIban(bankAccountRegisterDTO.iban());
        newBankAccount.setBic(bankAccountRegisterDTO.bic());
        newBankAccount.setBankName(bankAccountRegisterDTO.bankName());

        bankAccountRepository.save(newBankAccount);
    }

    /**
     * Removes a bank account by its IBAN.
     *
     * @param iban the IBAN of the bank account to remove
     */
    public void removeBankAccount(String iban) {
        bankAccountRepository.delete(getBankAccount(iban));
    }
}
