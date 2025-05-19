package com.banking.system.accountservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CardResponse",description = "Response object containing a paginated list of cards details.")
public class CardResponse {
    @Schema(description = "List of card data")
    private List<CardDto> cards;

    @Schema(description = "Total number of card records available", example = "2")
    private long totalElements;

    @Schema(description = "Total number of pages available", example = "2")
    private int totalPages;

    @Schema(description = "Current page number (zero-based index)", example = "2")
    private int page;

    @Schema(description = "Number of records per page", example = "10")
    private int size;
}
