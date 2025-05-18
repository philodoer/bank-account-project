package com.banking.system.accountservice.feign;

import com.banking.system.accountservice.dtos.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service", url = "127.0.0.1:1500")
public interface CustomerServiceFeign {

    @GetMapping("/customer/{customerId}")
    CustomerDto getCustomerById(@PathVariable("customerId") Long customerId);
}
