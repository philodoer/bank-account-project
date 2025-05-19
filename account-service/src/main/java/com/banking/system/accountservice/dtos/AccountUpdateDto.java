package com.banking.system.accountservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Schema(description = "Account update dto, used for updating account details. Only Iban and bicswift can be updated")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountUpdateDto {

    @Schema(description = "International Bank Account Number - globally unique",example = "43893704004005320130")
    private String iban;

    @Schema(description = "Bank Identifier Code (BIC/SWIFT)",example = "DERAAH")
    private String bicSwift;
}
