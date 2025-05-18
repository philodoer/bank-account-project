package com.banking.system.accountservice.services;

import com.banking.system.accountservice.dtos.AccountDto;
import com.banking.system.accountservice.dtos.AccountUpdateDto;
import com.banking.system.accountservice.dtos.CardResponse;
import com.banking.system.accountservice.feign.CardServiceFeign;
import com.banking.system.accountservice.feign.CustomerServiceFeign;
import com.banking.system.accountservice.mappers.AccountMapper;
import com.banking.system.accountservice.models.Account;
import com.banking.system.accountservice.repositories.AccountRepository;
import com.banking.system.accountservice.specifications.AccountSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final MessageSource messageSource;
    private final CardServiceFeign cardServiceFeign;
    private final CustomerServiceFeign customerServiceFeign;

    @Override
    public AccountDto saveAccount(AccountDto dto){
        validateCustomerExist(dto);
        validateAccountDto(dto);
        Account account = AccountMapper.toEntity(dto);
        account = accountRepository.save(account);
        return AccountMapper.toDto(account);
    }

    @Override
    public Page<AccountDto> findAllAccounts(int page, int size, Long customerId, String iban, String cardAlias) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Account> spec = AccountSpecifications.accountWithFilter(customerId,iban);

        if (cardAlias != null) {
            System.out.println(cardAlias + " cardAlias");
            /*
              Card filter implementation to go there.
              Not implemented due to double pagination. Planning on implementation to continue.
              Current plan - > fetch all cards by giving the size as total elements.
              This might affect performance and unnecessary pooling. Thinking of a better implementation plan
             */
        }


        Page<Account> customers = accountRepository.findAll(spec, pageable);
        return customers.map(AccountMapper::toDto);
    }

    @Override
    public AccountDto updateAccount(Long accountId, AccountUpdateDto dto) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        messageSource.getMessage("account.not.found", new Object[]{accountId},null)));

        if (dto.getIban() != null && !dto.getIban().trim().isEmpty()) {
            existingAccount.setIban(dto.getIban());
        }

        if (dto.getBicSwift() != null && !dto.getBicSwift().trim().isEmpty()) {
            existingAccount.setBicSwift(dto.getBicSwift());
        }

        Account updatedAccount = accountRepository.save(existingAccount);
        return AccountMapper.toDto(updatedAccount);
    }

    @Override
    public String deleteAccount(Long accountId) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        messageSource.getMessage("account.not.found", new Object[]{accountId},null)));

        CardResponse cardResponse = cardServiceFeign.getCardsByAccountCode(
                accountId,0,1
        );

        if (cardResponse.getTotalElements() > 0) {
            throw new IllegalArgumentException(
                    messageSource.getMessage("account.deletion.rejected", new Object[]{accountId}, null)
            );
        }
        accountRepository.delete(existingAccount);

        return messageSource.getMessage("account.deletion.successful", new Object[]{accountId},null);
    }

    @Override
    public AccountDto getAccountById(Long accountId) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        messageSource.getMessage("account.not.found", new Object[]{accountId},null)));
        return AccountMapper.toDto(existingAccount);
    }

    private void validateAccountDto(AccountDto dto) {
        if (dto.getIban() == null || dto.getIban().trim().isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage("missing.iban.number", null, null));
        }

        if (accountRepository.existsByIban(dto.getIban())) {
            throw new IllegalArgumentException(messageSource.getMessage("iban.number.exist", null, null));
        }

        if (dto.getBicSwift() == null || dto.getBicSwift().trim().isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage("missing.bicswift.number", null, null));
        }
    }

    private void validateCustomerExist(AccountDto dto) {
        if (dto.getCustomerId() == null || dto.getCustomerId()==0L) {
            throw new IllegalArgumentException(messageSource.getMessage("missing.customer.id", null, null));
        }

        try {
            customerServiceFeign.getCustomerById(dto.getCustomerId());
        }catch (Exception e){
            throw new IllegalArgumentException(messageSource.getMessage("customer.not.found", new Object[]{dto.getCustomerId()}, null));
        }
    }
}
