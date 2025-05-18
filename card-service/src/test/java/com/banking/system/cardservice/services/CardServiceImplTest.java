package com.banking.system.cardservice.services;

import com.banking.system.cardservice.configs.CardFormatConfig;
import com.banking.system.cardservice.dtos.AccountDto;
import com.banking.system.cardservice.dtos.CardDto;
import com.banking.system.cardservice.dtos.UpdateCardDto;
import com.banking.system.cardservice.enums.CardType;
import com.banking.system.cardservice.feign.AccountServiceFeign;
import com.banking.system.cardservice.models.Card;
import com.banking.system.cardservice.repositories.CardRepository;
import org.aspectj.util.Reflection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private AccountServiceFeign accountServiceFeign;

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardFormatConfig cardFormatConfig;

    private CardDto createCardDto;
    private CardDto fetchCardDto;
    private Card savedCard;
    int page = 0;
    int size = 10;

    @BeforeEach
    void setUp() {
        createCardDto = new CardDto();
            createCardDto.setAccountId(1L);
            createCardDto.setPan("4646557784849383");
            createCardDto.setCvv("123");
            createCardDto.setTypeOfCard(CardType.PHYSICAL);

        savedCard = new Card();
            savedCard.setCardId(1L);
            savedCard.setPan("4646557784849383");
            savedCard.setCardType(CardType.PHYSICAL);

        lenient().when(cardFormatConfig.getPanFormat()).thenReturn("\\d{16}");
        lenient().when(cardFormatConfig.getCvvFormat()).thenReturn("\\d{3}");
    }

    @Test
    void saveCard_ValidInput() {
        when(accountServiceFeign.getAccountById(1L)).thenReturn(new AccountDto());
        when(cardRepository.existsByAccountIdAndCardType(anyLong(), any())).thenReturn(false);
        when(cardRepository.existsByPan(anyString())).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        CardDto result = cardService.saveCard(createCardDto);

        assertNotNull(result);
        assertEquals(1L, result.getCardId());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void saveCard_InvalidPan() {
        createCardDto.setPan("invalid");

        when(messageSource.getMessage(eq("invalid.card.panformat"), isNull(), any()))
                .thenReturn("Invalid PAN format");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.saveCard(createCardDto));
        assertEquals("Invalid PAN format", exception.getMessage());
    }

    @Test
    void saveCard_AccountDoesNotExist() {
        createCardDto.setAccountId(999L);

        when(accountServiceFeign.getAccountById(999L)).thenThrow(new RuntimeException());
        when(messageSource.getMessage(eq("account.not.found"), any(), any()))
                .thenReturn("Account not found");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.saveCard(createCardDto));
        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    void getAllCards_WithFilters() {
        Pageable pageable = PageRequest.of(page, size);

        Page<Card> cardPage = new PageImpl<>(List.of(savedCard), pageable, 1);

        when(cardRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(cardPage);

        Page<CardDto> result = cardService.getAllCards(
                page, size, 1L, "My Card", "4111", CardType.PHYSICAL, true);

        assertEquals(1, result.getTotalElements());
        assertEquals(CardType.PHYSICAL, result.getContent().get(0).getTypeOfCard());
        verify(cardRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getAllCards_EmptyResult() {
        Pageable pageable = PageRequest.of(page, size);

        Page<Card> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(cardRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(emptyPage);

        Page<CardDto> result = cardService.getAllCards(
                page, size, null, null, null, null, false);

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void getCardById_ExistingId() {
        Long cardId = 1L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(savedCard));

        CardDto result = cardService.getCardById(cardId, false);

        assertNotNull(result);
        assertEquals(cardId, result.getCardId());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getCardById_NonExistingId() {
        Long cardId = 999L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("card.not.found"), any(), any()))
                .thenReturn("Card not found");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.getCardById(cardId, false));
        assertEquals("Card not found", exception.getMessage());
    }

    @Test
    void updateCardDetails_ValidInput() {
        Long cardId = 1L;
        UpdateCardDto updateDto = new UpdateCardDto();
        updateDto.setCardAlias("Learning card");

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(savedCard));
        when(cardRepository.save(savedCard)).thenReturn(savedCard);

        CardDto result = cardService.updateCardDetails(cardId, updateDto);

        assertNotNull(result);
        assertEquals(cardId, result.getCardId());
        verify(cardRepository).findById(cardId);
        verify(cardRepository).save(savedCard);
    }

    @Test
    void deleteCard_ExistingId() {
        Long cardId = 1L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(savedCard));
        when(messageSource.getMessage(eq("card.deletion.successful"), any(), any()))
                .thenReturn("Card deleted successfully");

        String result = cardService.deleteCard(cardId);

        assertEquals("Card deleted successfully", result);
        verify(cardRepository).delete(savedCard);
    }

    @Test
    void validateCardDetails_InvalidCVV() {
        createCardDto.setCvv("12"); // set Invalid CVV

        lenient().when(messageSource.getMessage("invalid.card.cvvformat", null, null))
                .thenReturn("Invalid cvv format provided");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.validateCardDetails(createCardDto));
        assertEquals("Invalid cvv format provided", exception.getMessage());
    }

    @Test
    void validateDataUniqueness_DuplicatePan_ShouldThrowException() {
        when(accountServiceFeign.getAccountById(1L)).thenReturn(new AccountDto());
        when(cardRepository.existsByPan("4646557784849383")).thenReturn(true);
        when(messageSource.getMessage(eq("card.pan.exist"), any(), any()))
                .thenReturn("PAN already exists");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.saveCard(createCardDto));
        assertEquals("PAN already exists", exception.getMessage());
    }
}