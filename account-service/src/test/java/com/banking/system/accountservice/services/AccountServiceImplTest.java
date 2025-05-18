package com.banking.system.accountservice.services;

import com.banking.system.accountservice.dtos.AccountDto;
import com.banking.system.accountservice.dtos.AccountUpdateDto;
import com.banking.system.accountservice.dtos.CardResponse;
import com.banking.system.accountservice.dtos.CustomerDto;
import com.banking.system.accountservice.feign.CardServiceFeign;
import com.banking.system.accountservice.feign.CustomerServiceFeign;
import com.banking.system.accountservice.models.Account;
import com.banking.system.accountservice.repositories.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private CardServiceFeign cardServiceFeign;

    @Mock
    private CustomerServiceFeign customerServiceFeign;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void saveAccount_ValidInput_ShouldReturnAccountDto() {
        // Arrange
        AccountDto inputDto = new AccountDto();
        inputDto.setCustomerId(1L);
        inputDto.setIban("DE89370400440532013000");
        inputDto.setBicSwift("DEUTDEFF");

        Account savedAccount = new Account();
        savedAccount.setAccountId(1L);
        savedAccount.setIban("DE89370400440532013000");

        when(customerServiceFeign.getCustomerById(1L)).thenReturn(new CustomerDto());
        when(accountRepository.existsByIban(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
//        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Error message");

        // Act
        AccountDto result = accountService.saveAccount(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getAccountId());
        assertEquals("DE89370400440532013000", result.getIban());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void saveAccount_MissingIban_ShouldThrowException() {
        // Arrange
        AccountDto inputDto = new AccountDto();
        inputDto.setCustomerId(1L);
        inputDto.setBicSwift("DEUTDEFF");

        when(messageSource.getMessage(eq("missing.iban.number"), isNull(), any()))
                .thenReturn("IBAN is required");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.saveAccount(inputDto));
        assertEquals("IBAN is required", exception.getMessage());
    }

    @Test
    void saveAccount_DuplicateIban_ShouldThrowException() {
        // Arrange
        AccountDto inputDto = new AccountDto();
        inputDto.setCustomerId(1L);
        inputDto.setIban("DE89370400440532013000");
        inputDto.setBicSwift("DEUTDEFF");

        when(customerServiceFeign.getCustomerById(1L)).thenReturn(new CustomerDto());
        when(accountRepository.existsByIban("DE89370400440532013000")).thenReturn(true);
        when(messageSource.getMessage(eq("iban.number.exist"), isNull(), any()))
                .thenReturn("IBAN already exists");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.saveAccount(inputDto));
        assertEquals("IBAN already exists", exception.getMessage());
    }

    @Test
    void findAllAccounts_WithCustomerIdFilter_ShouldReturnFilteredAccounts() {
        // Arrange
        int page = 0;
        int size = 10;
        Long customerId = 1L;
        Pageable pageable = PageRequest.of(page, size);

        Account account = new Account();
        account.setAccountId(1L);
        account.setCustomerId(customerId);

        Page<Account> accountPage = new PageImpl<>(List.of(account), pageable, 1);

        // Mock the repository to return our test page
        when(accountRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(accountPage);

        // Act
        Page<AccountDto> result = accountService.findAllAccounts(page, size, customerId, null, null);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(customerId, result.getContent().get(0).getCustomerId());
        verify(accountRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getAccountById_ExistingId_ShouldReturnAccountDto() {
        // Arrange
        Long accountId = 1L;
        Account account = new Account();
        account.setAccountId(accountId);
        account.setIban("DE89370400440532013000");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
//        when(messageSource.getMessage(anyString(), any(), any()))
//                .thenReturn("Error message");

        // Act
        AccountDto result = accountService.getAccountById(accountId);

        // Assert
        assertNotNull(result);
        assertEquals(accountId, result.getAccountId());
        assertEquals("DE89370400440532013000", result.getIban());
    }

    @Test
    void updateAccount_ValidInput_ShouldReturnUpdatedAccount() {
        // Arrange
        Long accountId = 1L;
        AccountUpdateDto updateDto = new AccountUpdateDto();
        updateDto.setIban("DE89370400440532013001");
        updateDto.setBicSwift("DEUTDEFF");

        Account existingAccount = new Account();
        existingAccount.setAccountId(accountId);
        existingAccount.setIban("DE89370400440532013000");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(existingAccount)).thenReturn(existingAccount);
//        when(messageSource.getMessage(anyString(), any(), any()))
//                .thenReturn("Error message");

        // Act
        AccountDto result = accountService.updateAccount(accountId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(accountId, result.getAccountId());
        assertEquals("DE89370400440532013001", result.getIban());
    }

    @Test
    void deleteAccount_NoCards_ShouldDeleteSuccessfully() {
        // Arrange
        Long accountId = 1L;
        Account existingAccount = new Account();
        existingAccount.setAccountId(accountId);

        CardResponse emptyCardResponse = new CardResponse();
        emptyCardResponse.setTotalElements(0L);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(cardServiceFeign.getCardsByAccountCode(accountId, 0, 1))
                .thenReturn(emptyCardResponse);
        when(messageSource.getMessage(eq("account.deletion.successful"), any(), any()))
                .thenReturn("Account deleted successfully");

        // Act
        String result = accountService.deleteAccount(accountId);

        // Assert
        assertEquals("Account deleted successfully", result);
        verify(accountRepository).delete(existingAccount);
    }

    @Test
    void deleteAccount_WithCards_ShouldThrowException() {
        // Arrange
        Long accountId = 1L;
        Account existingAccount = new Account();
        existingAccount.setAccountId(accountId);

        CardResponse cardResponse = new CardResponse();
        cardResponse.setTotalElements(1L);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(cardServiceFeign.getCardsByAccountCode(accountId, 0, 1))
                .thenReturn(cardResponse);
        when(messageSource.getMessage(eq("account.deletion.rejected"), any(), any()))
                .thenReturn("Account has cards and cannot be deleted");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.deleteAccount(accountId));
        assertEquals("Account has cards and cannot be deleted", exception.getMessage());
        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    void validateCustomerExist_CustomerNotFound_ShouldThrowException() {
        // Arrange
        AccountDto inputDto = new AccountDto();
        inputDto.setCustomerId(999L);
        inputDto.setIban("DE89370400440532013000");
        inputDto.setBicSwift("DEUTDEFF");

        when(customerServiceFeign.getCustomerById(999L)).thenThrow(new RuntimeException());
        when(messageSource.getMessage(eq("customer.not.found"), any(), any()))
                .thenReturn("Customer not found");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.saveAccount(inputDto));
        assertEquals("Customer not found", exception.getMessage());
    }
}