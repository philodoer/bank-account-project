package com.banking.system.customer.service.feign;

import com.banking.system.customer.service.dtos.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account-service", url = "127.0.0.1:1501")
public interface AccountServiceFeign {

    @GetMapping("/accounts")
    AccountResponse getAccountsByCustomerCode(
            @RequestParam("customerId") Long customerId,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );
}
