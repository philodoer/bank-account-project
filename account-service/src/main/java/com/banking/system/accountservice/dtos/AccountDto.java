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
    @Schema(description = "System-generated unique identifier for the account",
            example = "1001",accessMode = Schema.AccessMode.READ_ONLY)
    private Long accountId;

    @Schema(
            description = "International Bank Account Number - globally unique",
            example = "23442343353445764565")
    private String iban;

    @Schema(description = "Bank Identifier Code (BIC/SWIFT)",example = "FHEGJS")
    private String bicSwift;

    @Schema(description = "Customer ID who owns the account",example = "501")
    private Long customerId;

    /**
     * Pick timestamp when the data is being persisted. Cannot be edited
     */
    @Schema(description = "Date and time when the account was created",example = "2024-05-16T12:30:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
}
