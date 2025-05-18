package com.banking.system.cardservice.feign;

import com.banking.system.cardservice.dtos.AccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-service", url = "127.0.0.1:1501")
public interface AccountServiceFeign {

    @GetMapping("/accounts/{accountId}")
    AccountDto getAccountById(@PathVariable("accountId") Long accountId);
}
