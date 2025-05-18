package com.banking.system.cardservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Dto used to update card details")
public class UpdateCardDto {

    @Schema(description = "An optional name given to the card for easy differentiation, user-defined")
    private String cardAlias;
}

