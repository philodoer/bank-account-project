package com.banking.system.accountservice.services;

import com.banking.system.accountservice.dtos.AccountDto;
import com.banking.system.accountservice.dtos.AccountUpdateDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface AccountService {
    AccountDto saveAccount(AccountDto dto);

    Page<AccountDto> findAllAccounts(int page, int size, Long customerId, String iban, String cardAlias);

    AccountDto updateAccount(Long accountId, @Valid AccountUpdateDto dto);

    String deleteAccount(Long accountId);

    AccountDto getAccountById(Long accountId);
}
