package com.banking.system.cardservice.services;

import com.banking.system.cardservice.dtos.CardDto;
import com.banking.system.cardservice.dtos.UpdateCardDto;
import com.banking.system.cardservice.enums.CardType;
import org.springframework.data.domain.Page;

public interface CardService {
    CardDto saveCard(CardDto cardDto);

//    Page<CardDto> getAllCards(int page, int size, boolean hideSensitiveData,
//                                     String pan, String cardType,String cardAlias);

    CardDto updateCardDetails(Long cardId, UpdateCardDto dto);

    String deleteCard(Long cardId);

    Page<CardDto> getAllCards(int page, int size, Long accountId, String cardAlias, String pan, CardType cardType, Boolean showSensitiveData);

    CardDto getCardById(Long id, Boolean showSensitiveData);
}
