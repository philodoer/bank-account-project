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

    @BeforeEach
    void setUp() {
        lenient().when(cardFormatConfig.getPanFormat()).thenReturn("\\d{16}");
        lenient().when(cardFormatConfig.getCvvFormat()).thenReturn("\\d{3}");
    }


    @Test
    void saveCard_ValidInput_ShouldReturnCardDto() {
        // Arrange
        CardDto inputDto = new CardDto();
        inputDto.setAccountId(1L);
        inputDto.setPan("4111111111111111");
        inputDto.setCvv("123");
        inputDto.setTypeOfCard(CardType.PHYSICAL);

        Card savedCard = new Card();
        savedCard.setCardId(1L);
        savedCard.setPan("4111111111111111");

        when(accountServiceFeign.getAccountById(1L)).thenReturn(new AccountDto());
        when(cardRepository.existsByAccountIdAndCardType(anyLong(), any())).thenReturn(false);
        when(cardRepository.existsByPan(anyString())).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);
//        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Error message");

        // Act
        CardDto result = cardService.saveCard(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getCardId());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void saveCard_InvalidPan_ShouldThrowException() {
        // Arrange
        CardDto inputDto = new CardDto();
        inputDto.setAccountId(1L);
        inputDto.setPan("invalid");
        inputDto.setCvv("123");
        inputDto.setTypeOfCard(CardType.PHYSICAL);

        when(messageSource.getMessage(eq("invalid.card.panformat"), isNull(), any()))
                .thenReturn("Invalid PAN format");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.saveCard(inputDto));
        assertEquals("Invalid PAN format", exception.getMessage());
    }

    @Test
    void saveCard_AccountDoesNotExist_ShouldThrowException() {
        // Arrange
        CardDto inputDto = new CardDto();
        inputDto.setAccountId(999L);
        inputDto.setPan("4111111111111111");
        inputDto.setCvv("123");
        inputDto.setTypeOfCard(CardType.PHYSICAL);

        when(accountServiceFeign.getAccountById(999L)).thenThrow(new RuntimeException());
        when(messageSource.getMessage(eq("account.not.found"), any(), any()))
                .thenReturn("Account not found");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.saveCard(inputDto));
        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    void getAllCards_WithFilters_ShouldReturnPage() {
        // Arrange
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Card card = new Card();
        card.setCardId(1L);
        card.setCardType(CardType.PHYSICAL);

        // Create a real Page implementation
        Page<Card> cardPage = new PageImpl<>(List.of(card), pageable, 1);

        // Mock the repository to return our real Page
        when(cardRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(cardPage);

        // Act
        Page<CardDto> result = cardService.getAllCards(
                page, size, 1L, "My Card", "4111", CardType.PHYSICAL, true);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(CardType.PHYSICAL, result.getContent().get(0).getTypeOfCard());
        verify(cardRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getAllCards_EmptyResult_ShouldReturnEmptyPage() {
        // Arrange
        int page = 0;
        int size = 10;
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
    void getCardById_ExistingId_ShouldReturnCardDto() {
        // Arrange
        Long cardId = 1L;
        Card card = new Card();
        card.setCardId(cardId);
        card.setPan("4111111111111111");

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
//        when(messageSource.getMessage(anyString(), any(), any()))
//                .thenReturn("Error message");

        // Act
        CardDto result = cardService.getCardById(cardId, false);

        // Assert
        assertNotNull(result);
        assertEquals(cardId, result.getCardId());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getCardById_NonExistingId_ShouldThrowException() {
        // Arrange
        Long cardId = 999L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("card.not.found"), any(), any()))
                .thenReturn("Card not found");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.getCardById(cardId, false));
        assertEquals("Card not found", exception.getMessage());
    }

    @Test
    void updateCardDetails_ValidInput_ShouldReturnUpdatedCard() {
        // Arrange
        Long cardId = 1L;
        UpdateCardDto updateDto = new UpdateCardDto();
        updateDto.setCardAlias("Updated Alias");

        Card existingCard = new Card();
        existingCard.setCardId(cardId);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
        when(cardRepository.save(existingCard)).thenReturn(existingCard);
//        when(messageSource.getMessage(anyString(), any(), any()))
//                .thenReturn("Error message");

        // Act
        CardDto result = cardService.updateCardDetails(cardId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(cardId, result.getCardId());
        verify(cardRepository).findById(cardId);
        verify(cardRepository).save(existingCard);
    }

    @Test
    void deleteCard_ExistingId_ShouldReturnSuccessMessage() {
        // Arrange
        Long cardId = 1L;
        Card existingCard = new Card();
        existingCard.setCardId(cardId);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
        when(messageSource.getMessage(eq("card.deletion.successful"), any(), any()))
                .thenReturn("Card deleted successfully");

        // Act
        String result = cardService.deleteCard(cardId);

        // Assert
        assertEquals("Card deleted successfully", result);
        verify(cardRepository).delete(existingCard);
    }

    @Test
    void validateCardDetails_InvalidCVV_ShouldThrowException() {
        CardDto inputDto = new CardDto();
        inputDto.setCvv("12"); // Invalid CVV
        inputDto.setPan("4111111111111111");
        inputDto.setTypeOfCard(CardType.PHYSICAL);

        lenient().when(messageSource.getMessage("invalid.card.cvvformat", null, null))
                .thenReturn("Invalid CVV format");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.validateCardDetails(inputDto));
        assertEquals("Invalid CVV format", exception.getMessage());
    }

    @Test
    void validateDataUniqueness_DuplicatePan_ShouldThrowException() {
        // Arrange
        CardDto inputDto = new CardDto();
        inputDto.setAccountId(1L);
        inputDto.setPan("4111111111111111");
        inputDto.setCvv("123");
        inputDto.setTypeOfCard(CardType.PHYSICAL);

        when(accountServiceFeign.getAccountById(1L)).thenReturn(new AccountDto());
        when(cardRepository.existsByPan("4111111111111111")).thenReturn(true);
        when(messageSource.getMessage(eq("card.pan.exist"), any(), any()))
                .thenReturn("PAN already exists");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.saveCard(inputDto));
        assertEquals("PAN already exists", exception.getMessage());
    }
}