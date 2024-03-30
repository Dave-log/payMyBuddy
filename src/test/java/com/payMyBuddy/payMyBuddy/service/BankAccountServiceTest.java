package com.payMyBuddy.payMyBuddy.service;

import com.payMyBuddy.payMyBuddy.dto.BankAccountRegisterDTO;
import com.payMyBuddy.payMyBuddy.exceptions.BankAccountNotFoundException;
import com.payMyBuddy.payMyBuddy.model.BankAccount;
import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.BankAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceTest {
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    UserService userService;
    @InjectMocks
    private BankAccountService bankAccountService;

    @Test
    public void getBankAccount_ExistingId() {
        // Arrange
        long accountId = 1L;
        BankAccount expectedBankAccount = new BankAccount();
        expectedBankAccount.setId(accountId);

        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(expectedBankAccount));

        // Act
        BankAccount actualBankAccount = bankAccountService.getBankAccount(accountId);

        // Assert
        assertEquals(expectedBankAccount, actualBankAccount);
    }

    @Test
    public void getBankAccount_NonExistingId() {
        long nonExistingId = 0L;

        when(bankAccountRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.getBankAccount(nonExistingId));
    }

    @Test
    public void getBankAccount_ExistingIban_ReturnsBankAccount() {
        // Arrange
        String iban = "IBAN_1234567890";
        BankAccount expectedBankAccount = new BankAccount();
        expectedBankAccount.setIban(iban);

        when(bankAccountRepository.findByIban(iban)).thenReturn(Optional.of(expectedBankAccount));

        // Act
        BankAccount actualBankAccount = bankAccountService.getBankAccount(iban);

        // Assert
        assertEquals(expectedBankAccount, actualBankAccount);
    }

    @Test
    public void getBankAccount_NonExistingIban_ThrowsBankAccountNotFoundException() {
        // Arrange
        String nonExistingIban = "NON_EXISTING_IBAN";

        when(bankAccountRepository.findByIban(nonExistingIban)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.getBankAccount(nonExistingIban));
    }

    @Test
    public void getBankAccounts() {
        // Arrange
        List<BankAccount> expectedBankAccounts = new ArrayList<>();
        expectedBankAccounts.add(new BankAccount());
        expectedBankAccounts.add(new BankAccount());
        expectedBankAccounts.add(new BankAccount());

        when(bankAccountRepository.findAll()).thenReturn(expectedBankAccounts);

        // Act
        Iterable<BankAccount> actualBankAccounts = bankAccountService.getBankAccounts();

        // Assert
        assertEquals(expectedBankAccounts, actualBankAccounts);
    }

    @Test
    public void addBankAccount_Successful() {
        // Arrange
        BankAccountRegisterDTO bankAccountRegisterDTO = new BankAccountRegisterDTO(
                "123456789",
                "Checking",
                "IBAN_12345",
                "BIC_12345",
                "Bank A"
        );

        User currentUser = new User();
        when(userService.getCurrentUser()).thenReturn(currentUser);

        BankAccount newBankAccount = new BankAccount();
        newBankAccount.setUser(currentUser);
        newBankAccount.setAccountNumber(bankAccountRegisterDTO.accountNumber());
        newBankAccount.setAccountType(bankAccountRegisterDTO.accountType());
        newBankAccount.setIban(bankAccountRegisterDTO.iban());
        newBankAccount.setBic(bankAccountRegisterDTO.bic());
        newBankAccount.setBankName(bankAccountRegisterDTO.bankName());

        newBankAccount.setUser(currentUser);

        // Act
        bankAccountService.addBankAccount(bankAccountRegisterDTO);

        // Assert
        verify(bankAccountRepository).save(newBankAccount);
    }

    @Test
    public void removeBankAccount() {
        // Arrange
        String iban = "IBAN_12345";
        BankAccount bankAccount = new BankAccount();
        when(bankAccountRepository.findByIban(iban)).thenReturn(Optional.of(bankAccount));

        // Act
        bankAccountService.removeBankAccount(iban);

        // Assert
        verify(bankAccountRepository).delete(bankAccount);
    }

}
