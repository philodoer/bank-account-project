package com.banking.system.cardservice.controllers;

import com.banking.system.cardservice.dtos.CardDto;
import com.banking.system.cardservice.dtos.UpdateCardDto;
import com.banking.system.cardservice.enums.CardType;
import com.banking.system.cardservice.models.Card;
import com.banking.system.cardservice.services.CardService;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    private CardDto createCardDto;
    private CardDto fetchCardDto;
    int page = 0;
    int size = 10;

    @BeforeEach
    void setUp() {
        createCardDto = new CardDto();
            createCardDto.setAccountId(1L);
            createCardDto.setPan("4646557784849383");
            createCardDto.setCvv("123");
            createCardDto.setTypeOfCard(CardType.PHYSICAL);

        fetchCardDto = new CardDto();
            fetchCardDto.setAccountId(1L);
            fetchCardDto.setPan("4646557784849383");
            fetchCardDto.setCvv("123");
            fetchCardDto.setTypeOfCard(CardType.PHYSICAL);
    }

    @Test
    void saveCard_ValidInput() {
               when(cardService.saveCard(createCardDto)).thenReturn(fetchCardDto);

        // Act
        ResponseEntity<CardDto> response = cardController.save(createCardDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(fetchCardDto, response.getBody());
        verify(cardService).saveCard(createCardDto);
    }

    @Test
    void findAllCards_NoFilters() {
        Page<CardDto> expectedPage = new PageImpl<>(List.of(fetchCardDto));
        when(cardService.getAllCards(page, size, null, null, null, null, false))
                .thenReturn(expectedPage);

        ResponseEntity<Page<CardDto>> response = cardController.findAll(page, size, null, null, null, null, false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getTotalElements());
        assertEquals(fetchCardDto, response.getBody().getContent().get(0));
    }

    @Test
    void findAllCards_WithFilters() {
        Long accountId = 1L;
        String cardAlias = "My Card";
        String pan = "4646";
        CardType cardType = CardType.PHYSICAL;
        boolean showSensitiveData = true;

        fetchCardDto.setCardAlias(cardAlias);

        Page<CardDto> expectedPage = new PageImpl<>(List.of(fetchCardDto));
        when(cardService.getAllCards(page, size, accountId, cardAlias, pan, cardType, showSensitiveData))
                .thenReturn(expectedPage);

        ResponseEntity<Page<CardDto>> response = cardController.findAll(
                page, size, accountId, cardAlias, pan, cardType, showSensitiveData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(cardType, response.getBody().getContent().get(0).getTypeOfCard());
        assertEquals(cardAlias, response.getBody().getContent().get(0).getCardAlias());
    }

    @Test
    void getCardById_ExistingId() {
        Long cardId = 1L;
        boolean showSensitiveData = true;

        when(cardService.getCardById(cardId, showSensitiveData)).thenReturn(fetchCardDto);

        ResponseEntity<CardDto> response = cardController.getCardById(cardId, showSensitiveData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fetchCardDto, response.getBody());
        verify(cardService).getCardById(cardId, showSensitiveData);
    }

    @Test
    void updateCard_ValidInput() {
        Long cardId = 1L;
        UpdateCardDto updateDto = new UpdateCardDto();
        updateDto.setCardAlias("Learning card");

        CardDto updatedDto = new CardDto();
        updatedDto.setCardId(cardId);
        updatedDto.setCardAlias("Learning card");

        when(cardService.updateCardDetails(cardId, updateDto)).thenReturn(updatedDto);

        ResponseEntity<CardDto> response = cardController.update(updateDto, cardId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDto, response.getBody());
        assertEquals("Learning card", response.getBody().getCardAlias());
        verify(cardService).updateCardDetails(cardId, updateDto);
    }

    @Test
    void deleteCard_ExistingId() {
        Long cardId = 1L;
        String successMessage = "Card deleted successfully";

        when(cardService.deleteCard(cardId)).thenReturn(successMessage);

        ResponseEntity<String> response = cardController.delete(cardId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(successMessage, response.getBody());
        verify(cardService).deleteCard(cardId);
    }

    @Test
    void findAllCards_EmptyResult() {
        Page<CardDto> expectedPage = new PageImpl<>(Collections.emptyList());
        when(cardService.getAllCards(page, size, null, null, null, null, false))
                .thenReturn(expectedPage);

        ResponseEntity<Page<CardDto>> response = cardController.findAll(page, size, null, null, null, null, false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}