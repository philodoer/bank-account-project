package com.banking.system.accountservice.feign;

import com.banking.system.accountservice.dtos.CardResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "card-service", url = "127.0.0.1:1502")
public interface CardServiceFeign {

    @Operation(summary = "Get cards by account ID", description = "Retrieves a list of cards linked to an account.")
    @GetMapping("/card")
    CardResponse getCardsByAccountCode(
            @RequestParam("accountId") Long accountIdId,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );
}
