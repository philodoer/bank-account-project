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
    @Schema(description = "Account Id, Link an account to a card.")
    private Long accountId;

    @NotNull(message = "{invalid.card.type}")
    @JsonDeserialize(using = CardTypeDeserializer.class)
    private CardType typeOfCard;

    @NotBlank(message = "{pan.mandatory}")
    private String pan;

    @NotBlank(message = "{cvv.mandatory}")
    private String cvv;

    @Schema(description = "An optional name given to the card for easy differentiation, user-defined")
    private String cardAlias;
}
