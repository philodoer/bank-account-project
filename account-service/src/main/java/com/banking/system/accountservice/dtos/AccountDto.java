package com.banking.system.accountservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Account dto, used for account creation and fetching account details")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    @Schema(
            description = "System-generated unique identifier for the account",
            example = "1001",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long accountId;

    @Schema(
            description = "International Bank Account Number - globally unique",
            example = "2344 2343 3534 4576 4565"
    )
    private String iban;

    @Schema(
            description = "Bank Identifier Code (BIC/SWIFT)",
            example = "DEUTDEFF"
    )
    private String bicSwift;

    @Schema(
            description = "Customer ID who owns the account",
            example = "501"
    )
    private Long customerId;

    @Schema(
            description = "Date and time when the account was created",
            example = "2024-05-16T12:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime createdAt;
}
