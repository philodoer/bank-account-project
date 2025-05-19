package com.banking.system.cardservice.dtos;

import com.banking.system.cardservice.enums.CardType;
import com.banking.system.cardservice.enums.CardTypeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Dto used to create card details")
public class CardDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY,
            description = "Card unique identifier, system-generated code")
    private Long cardId;

    @NotNull(message = "{account.detail.missing}")
    @Schema(description = "Account Id, Link an account to a card. The id must reference an actual account in the system")
    private Long accountId;

    @NotNull(message = "{invalid.card.type}")
    @JsonDeserialize(using = CardTypeDeserializer.class)
    @Schema(description = "Type of card to be saved. Either VIRTUAL or PHYSICAL")
    private CardType typeOfCard;

    @NotBlank(message = "{pan.mandatory}")
    @Schema(description = "Primary Account Number - identifies the account with key details about the account. Format to be specified on the property file")
    private String pan;

    @NotBlank(message = "{cvv.mandatory}")
    @Schema(description = "Card Verification Value. Format to be specified on the property file")
    private String cvv;

    @Schema(description = "An optional name given to the card for easy differentiation, user-defined")
    private String cardAlias;
}
