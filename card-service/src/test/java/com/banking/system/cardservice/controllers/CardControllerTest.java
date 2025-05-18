package com.banking.system.cardservice.controllers;

import com.banking.system.cardservice.dtos.CardDto;
import com.banking.system.cardservice.dtos.UpdateCardDto;
import com.banking.system.cardservice.enums.CardType;
import com.banking.system.cardservice.services.CardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    @Test
    void saveCard_ValidInput_ShouldReturnCreated() {
        // Arrange
        CardDto inputDto = new CardDto();
        inputDto.setPan("4111111111111111");
        inputDto.setCvv("123");
        inputDto.setTypeOfCard(CardType.PHYSICAL);

        CardDto savedDto = new CardDto();
        savedDto.setCardId(1L);
        savedDto.setPan("4111111111111111");
        savedDto.setTypeOfCard(CardType.PHYSICAL);

        when(cardService.saveCard(inputDto)).thenReturn(savedDto);

        // Act
        ResponseEntity<CardDto> response = cardController.save(inputDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedDto, response.getBody());
        verify(cardService).saveCard(inputDto);
    }

    @Test
    void findAllCards_NoFilters_ShouldReturnPageOfCards() {
        // Arrange
        int page = 0;
        int size = 10;

        CardDto cardDto = new CardDto();
        cardDto.setCardId(1L);
        cardDto.setTypeOfCard(CardType.VIRTUAL);

        Page<CardDto> expectedPage = new PageImpl<>(List.of(cardDto));
        when(cardService.getAllCards(page, size, null, null, null, null, false))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<CardDto>> response = cardController.findAll(page, size, null, null, null, null, false);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(cardDto, response.getBody().getContent().get(0));
    }

    @Test
    void findAllCards_WithFilters_ShouldReturnFilteredCards() {
        // Arrange
        int page = 0;
        int size = 10;
        Long accountId = 1L;
        String cardAlias = "My Card";
        String pan = "4111";
        CardType cardType = CardType.PHYSICAL;
        boolean showSensitiveData = true;

        CardDto cardDto = new CardDto();
        cardDto.setCardId(1L);
        cardDto.setTypeOfCard(cardType);
        cardDto.setCardAlias(cardAlias);

        Page<CardDto> expectedPage = new PageImpl<>(List.of(cardDto));
        when(cardService.getAllCards(page, size, accountId, cardAlias, pan, cardType, showSensitiveData))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<CardDto>> response = cardController.findAll(
                page, size, accountId, cardAlias, pan, cardType, showSensitiveData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(cardType, response.getBody().getContent().get(0).getTypeOfCard());
        assertEquals(cardAlias, response.getBody().getContent().get(0).getCardAlias());
    }

    @Test
    void getCardById_ExistingId_ShouldReturnCard() {
        // Arrange
        Long cardId = 1L;
        boolean showSensitiveData = true;

        CardDto expectedDto = new CardDto();
        expectedDto.setCardId(cardId);
        expectedDto.setPan("4111111111111111");

        when(cardService.getCardById(cardId, showSensitiveData)).thenReturn(expectedDto);

        // Act
        ResponseEntity<CardDto> response = cardController.getCardById(cardId, showSensitiveData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(cardService).getCardById(cardId, showSensitiveData);
    }

    @Test
    void updateCard_ValidInput_ShouldReturnUpdatedCard() {
        // Arrange
        Long cardId = 1L;
        UpdateCardDto updateDto = new UpdateCardDto();
        updateDto.setCardAlias("Updated Alias");

        CardDto updatedDto = new CardDto();
        updatedDto.setCardId(cardId);
        updatedDto.setCardAlias("Updated Alias");

        when(cardService.updateCardDetails(cardId, updateDto)).thenReturn(updatedDto);

        // Act
        ResponseEntity<CardDto> response = cardController.update(updateDto, cardId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDto, response.getBody());
        assertEquals("Updated Alias", response.getBody().getCardAlias());
        verify(cardService).updateCardDetails(cardId, updateDto);
    }

    @Test
    void deleteCard_ExistingId_ShouldReturnSuccessMessage() {
        // Arrange
        Long cardId = 1L;
        String successMessage = "Card deleted successfully";

        when(cardService.deleteCard(cardId)).thenReturn(successMessage);

        // Act
        ResponseEntity<String> response = cardController.delete(cardId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(successMessage, response.getBody());
        verify(cardService).deleteCard(cardId);
    }

    @Test
    void findAllCards_EmptyResult_ShouldReturnEmptyPage() {
        // Arrange
        int page = 0;
        int size = 10;

        Page<CardDto> expectedPage = new PageImpl<>(Collections.emptyList());
        when(cardService.getAllCards(page, size, null, null, null, null, false))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<CardDto>> response = cardController.findAll(page, size, null, null, null, null, false);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}