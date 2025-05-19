package com.banking.system.accountservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CardDto",description = "Dto used to link a card to an account. Contains essential card details such as card ID, alias, and referenced account ID.")
public class CardDto {
    @Schema(description = "Unique identifier for the card", example = "1001")
    private String cardId;

    @Schema(description = "user defined name/nickname for an card", example = "Shopping card")
    private String cardAlias;

    @Schema(description = "Id used to refer to existing account linked to the card", example = "1002")
    private String accountId;
}
