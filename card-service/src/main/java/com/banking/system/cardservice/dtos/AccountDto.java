package com.banking.system.cardservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "accountDto response", description = "Hold account details from account service")
public class AccountDto {
    @Schema(description = "Id of the account fetched")
    private Long accountId;
}
