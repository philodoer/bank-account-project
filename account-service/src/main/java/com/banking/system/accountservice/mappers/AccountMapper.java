package com.banking.system.accountservice.mappers;

import com.banking.system.accountservice.dtos.AccountDto;
import com.banking.system.accountservice.models.Account;

/**
 * Mapper class to convert between Account entity and AccountDto.
 */

public class AccountMapper {
    /**
     * Converts an Account entity to an AccountDto.
     *
     * @param account the Account entity
     * @return AccountDto representation
     */
    public static AccountDto toDto(Account account) {
        if (account == null) {
            return null;
        }

        return AccountDto.builder()
                .accountId(account.getAccountId())
                .iban(account.getIban())
                .bicSwift(account.getBicSwift())
                .customerId(account.getCustomerId())
                .createdAt(account.getCreatedAt())
                .build();
    }

    /**
     * Converts an AccountDto to an Account entity.
     *
     * @param dto the AccountDto
     * @return Account entity
     */
    public static Account toEntity(AccountDto dto) {
        if (dto == null) {
            return null;
        }

        Account account = new Account();
        account.setAccountId(dto.getAccountId());
        account.setIban(dto.getIban());
        account.setBicSwift(dto.getBicSwift());
        account.setCustomerId(dto.getCustomerId());
        account.setCreatedAt(dto.getCreatedAt());
        return account;
    }
}
