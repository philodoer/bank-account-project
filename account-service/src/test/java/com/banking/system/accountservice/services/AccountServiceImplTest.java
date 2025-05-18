package com.banking.system.accountservice.services;

import com.banking.system.accountservice.dtos.AccountDto;
import com.banking.system.accountservice.dtos.AccountUpdateDto;
import com.banking.system.accountservice.dtos.CardResponse;
import com.banking.system.accountservice.dtos.CustomerDto;
import com.banking.system.accountservice.feign.CardServiceFeign;
import com.banking.system.accountservice.feign.CustomerServiceFeign;
import com.banking.system.accountservice.models.Account;
import com.banking.system.accountservice.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
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

    private Account account;
    private AccountDto createAccountDto;
    private AccountDto fetchAccountDto;
    int page = 0;
    int size = 10;

    @BeforeEach
    void setUp() {
        createAccountDto = new AccountDto();
            createAccountDto.setIban("3434343234323345");
            createAccountDto.setBicSwift("FDEGHE");
            createAccountDto.setCustomerId(1L);

        fetchAccountDto = new AccountDto();
            fetchAccountDto.setAccountId(1L);
            fetchAccountDto.setIban("3434343234323345");

        account = new Account();
            account.setAccountId(1L);
            account.setIban("3434343234323345");
            account.setBicSwift("FDEGHE");
            account.setCustomerId(1L);
    }

    @Test
    void saveAccount_ValidInput() {
        when(customerServiceFeign.getCustomerById(1L)).thenReturn(new CustomerDto());
        when(accountRepository.existsByIban(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountDto result = accountService.saveAccount(createAccountDto);

        assertNotNull(result);
        assertEquals(1L, result.getAccountId());
        assertEquals("3434343234323345", result.getIban());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void saveAccount_MissingIban() {
        createAccountDto.setCustomerId(1L);
        createAccountDto.setIban("");

        when(messageSource.getMessage(eq("missing.iban.number"), isNull(), any()))
                .thenReturn("IBAN is required");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.saveAccount(createAccountDto));
        assertEquals("IBAN is required", exception.getMessage());
    }

    @Test
    void saveAccount_DuplicateIban() {
        when(customerServiceFeign.getCustomerById(1L)).thenReturn(new CustomerDto());
        when(accountRepository.existsByIban("3434343234323345")).thenReturn(true);
        when(messageSource.getMessage(eq("iban.number.exist"), isNull(), any()))
                .thenReturn("IBAN already exists");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.saveAccount(createAccountDto));
        assertEquals("IBAN already exists", exception.getMessage());
    }

    @Test
    void findAllAccounts_WithFilterCustomerId() {
        Long customerId = 1L;
        Pageable pageable = PageRequest.of(page, size);

        Page<Account> accountPage = new PageImpl<>(List.of(account), pageable, 1);

        when(accountRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(accountPage);

        Page<AccountDto> result = accountService.findAllAccounts(page, size, customerId, null, null);

        assertEquals(1, result.getTotalElements());
        assertEquals(customerId, result.getContent().get(0).getCustomerId());
        verify(accountRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getAccountById_ExistingId() {
        Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        AccountDto result = accountService.getAccountById(accountId);

        assertNotNull(result);
        assertEquals(accountId, result.getAccountId());
        assertEquals("3434343234323345", result.getIban());
    }

    @Test
    void updateAccount_ValidInput() {
        Long accountId = 1L;
        AccountUpdateDto updateDto = new AccountUpdateDto();
        updateDto.setIban("7658548493930240");
        updateDto.setBicSwift("FERHEGS");

        Account existingAccount = new Account();
        existingAccount.setAccountId(accountId);
        existingAccount.setIban("3434343234323345");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(existingAccount)).thenReturn(existingAccount);

        AccountDto result = accountService.updateAccount(accountId, updateDto);

        assertNotNull(result);
        assertEquals(accountId, result.getAccountId());
        assertEquals("7658548493930240", result.getIban());
    }

    @Test
    void deleteAccount_NoCards() {
        Long accountId = 1L;

        CardResponse emptyCardResponse = new CardResponse();
        emptyCardResponse.setTotalElements(0L);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(cardServiceFeign.getCardsByAccountCode(accountId, 0, 1))
                .thenReturn(emptyCardResponse);
        when(messageSource.getMessage(eq("account.deletion.successful"), any(), isNull()))
                .thenReturn("Account deleted successfully");

        String result = accountService.deleteAccount(accountId);

        assertEquals("Account deleted successfully", result);
        verify(accountRepository).delete(account);
    }

    @Test
    void deleteAccount_WithCards() {
        Long accountId = 1L;

        CardResponse cardResponse = new CardResponse();
        cardResponse.setTotalElements(1L);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(cardServiceFeign.getCardsByAccountCode(accountId, 0, 1))
                .thenReturn(cardResponse);
        when(messageSource.getMessage(eq("account.deletion.rejected"), any(), any()))
                .thenReturn("Account has cards and cannot be deleted");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.deleteAccount(accountId));
        assertEquals("Account has cards and cannot be deleted", exception.getMessage());
        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    void validateCustomerExist_CustomerNotFound() {
        createAccountDto.setCustomerId(999L);

        when(customerServiceFeign.getCustomerById(999L)).thenThrow(new RuntimeException());
        when(messageSource.getMessage(eq("customer.not.found"), any(), any()))
                .thenReturn("Customer not found");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.saveAccount(createAccountDto));
        assertEquals("Customer not found", exception.getMessage());
    }
}