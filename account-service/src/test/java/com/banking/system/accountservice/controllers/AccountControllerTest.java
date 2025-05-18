package com.banking.system.accountservice.controllers;

import com.banking.system.accountservice.dtos.AccountDto;
import com.banking.system.accountservice.dtos.AccountUpdateDto;
import com.banking.system.accountservice.models.Account;
import com.banking.system.accountservice.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

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
    }

    @Test
    void saveAccount_ValidInput() {

        when(accountService.saveAccount(createAccountDto)).thenReturn(fetchAccountDto);

        ResponseEntity<AccountDto> response = accountController.save(createAccountDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(fetchAccountDto, response.getBody());
        verify(accountService).saveAccount(createAccountDto);
    }

    @Test
    void findAllAccounts_NoFilters() {
        Page<AccountDto> expectedPage = new PageImpl<>(List.of(createAccountDto));
        when(accountService.findAllAccounts(page, size, null, null, null))
                .thenReturn(expectedPage);

        ResponseEntity<Page<AccountDto>> response = accountController.findAll(page, size, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(createAccountDto, response.getBody().getContent().get(0));
    }

    @Test
    void findAllAccounts_WithFilters() {
        Long customerId = 1L;
        String iban = "3434343234323345";
        String cardAlias = "Premium";

        AccountDto accountDto = new AccountDto();
        accountDto.setAccountId(1L);
        accountDto.setCustomerId(customerId);
        accountDto.setIban(iban);

        Page<AccountDto> expectedPage = new PageImpl<>(List.of(accountDto));
        when(accountService.findAllAccounts(page, size, customerId, iban, cardAlias))
                .thenReturn(expectedPage);

        ResponseEntity<Page<AccountDto>> response = accountController.findAll(
                page, size, iban, cardAlias, customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(customerId, response.getBody().getContent().get(0).getCustomerId());
        assertEquals(iban, response.getBody().getContent().get(0).getIban());
    }

    @Test
    void getAccountById_ExistingId() {
        Long accountId = 1L;

        when(accountService.getAccountById(accountId)).thenReturn(createAccountDto);

        ResponseEntity<AccountDto> response = accountController.getAccountById(accountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createAccountDto, response.getBody());
        verify(accountService).getAccountById(accountId);
    }

    @Test
    void updateAccount_ValidInput() {
        Long accountId = 1L;
        AccountUpdateDto toUpdateDto = new AccountUpdateDto();
        toUpdateDto.setIban("3543343454453466");

        AccountDto updatedDto = new AccountDto();
        updatedDto.setAccountId(accountId);
        updatedDto.setIban("3543343454453466");

        when(accountService.updateAccount(accountId, toUpdateDto)).thenReturn(updatedDto);

        ResponseEntity<AccountDto> response = accountController.update(toUpdateDto, accountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDto, response.getBody());
        assertEquals("3543343454453466", Objects.requireNonNull(response.getBody()).getIban());
        verify(accountService).updateAccount(accountId, toUpdateDto);
    }

    @Test
    void deleteAccount_ExistingId() {
        Long accountId = 1L;
        String successMessage = "Account deleted successfully";

        when(accountService.deleteAccount(accountId)).thenReturn(successMessage);

        ResponseEntity<String> response = accountController.delete(accountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(successMessage, response.getBody());
        verify(accountService).deleteAccount(accountId);
    }

    @Test
    void findAllAccounts_EmptyResult() {
        Page<AccountDto> expectedPage = new PageImpl<>(Collections.emptyList());
        when(accountService.findAllAccounts(page, size, null, null, null))
                .thenReturn(expectedPage);

        ResponseEntity<Page<AccountDto>> response = accountController.findAll(page, size, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}