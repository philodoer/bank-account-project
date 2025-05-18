package com.banking.system.cardservice.controllers;

import com.banking.system.cardservice.dtos.CardDto;
import com.banking.system.cardservice.dtos.UpdateCardDto;
import com.banking.system.cardservice.enums.CardType;
import com.banking.system.cardservice.services.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(summary = "Create a new card. The account id must reference and existing account" +
            "Card Type has to be either VIRTUAL or PHYSICAL. CVV and PAN are mandatory")
    public ResponseEntity<CardDto> save(
            @Parameter(description = "card details",required = true)
            @Valid @RequestBody CardDto dto
    ) {
        CardDto returnedDto = cardService.saveCard(dto);
        return new ResponseEntity<>(returnedDto, HttpStatus.CREATED);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "fetch all cards")
    public ResponseEntity<Page<CardDto>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) String cardAlias,
            @RequestParam(required = false) String pan,
            @RequestParam(required = false) CardType cardType,
            @RequestParam(required = false, defaultValue = "false")Boolean showSensitiveData
    ) {
        return new ResponseEntity<>(cardService.getAllCards(page, size, accountId,cardAlias, pan, cardType, showSensitiveData ), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Fetch a card by its ID")
    public ResponseEntity<CardDto> getCardById(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") Boolean showSensitiveData
    ) {
        return new ResponseEntity<>(cardService.getCardById(id, showSensitiveData), HttpStatus.OK);
    }

    @PutMapping("/{cardId}")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Update an existing card.")
    public ResponseEntity<CardDto> update(
            @Valid @RequestBody UpdateCardDto dto,
            @PathVariable Long cardId
    ){
        return new ResponseEntity<>(cardService.updateCardDetails(cardId, dto),HttpStatus.OK);
    }

    @DeleteMapping("/{cardId}")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Delete card details")
    public ResponseEntity<String> delete(
            @PathVariable Long cardId){
        return new ResponseEntity<>(cardService.deleteCard(cardId), HttpStatus.OK);
    }
}
