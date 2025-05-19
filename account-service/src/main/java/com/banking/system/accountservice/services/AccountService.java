package com.banking.system.accountservice.services;

import com.banking.system.accountservice.dtos.AccountDto;
import com.banking.system.accountservice.dtos.AccountUpdateDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

/**
 * Account service, key for all CRUD activities on account
 */

public interface AccountService {
    /**
     *
     * @param dto
     * @return accountDto of the saved record
     */
    AccountDto saveAccount(AccountDto dto);

    /**
     *
     * @param page
     * @param size
     * @param customerId
     * @param iban
     * @param cardAlias
     * @return a paginated list of accounts
     */
    Page<AccountDto> findAllAccounts(int page, int size, Long customerId, String iban, String cardAlias);

    /**
     * Used for updating accounts
     * @param accountId
     * @param dto
     * @return a dto of the updated record
     */
    AccountDto updateAccount(Long accountId, @Valid AccountUpdateDto dto);

    /**
     *
     * @param accountId
     * @return response status. A success message on deletion
     */
    String deleteAccount(Long accountId);

    /**
     *
     * @param accountId
     * @return a dto of the account with the specified accountId
     */
    AccountDto getAccountById(Long accountId);
}
