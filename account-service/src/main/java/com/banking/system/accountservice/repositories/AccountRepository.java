package com.banking.system.accountservice.repositories;

import com.banking.system.accountservice.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    /**
     *
     * @param iban
     * @return true or false, validate if the Iban number is used by another account
     */
    boolean existsByIban(String iban);
}
