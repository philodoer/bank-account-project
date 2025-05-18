package com.banking.system.cardservice.services;

import com.banking.system.cardservice.configs.CardFormatConfig;
import com.banking.system.cardservice.dtos.AccountDto;
import com.banking.system.cardservice.dtos.CardDto;
import com.banking.system.cardservice.dtos.UpdateCardDto;
import com.banking.system.cardservice.enums.CardType;
import com.banking.system.cardservice.feign.AccountServiceFeign;
import com.banking.system.cardservice.mappers.CardMapper;
import com.banking.system.cardservice.models.Card;
import com.banking.system.cardservice.repositories.CardRepository;
import com.banking.system.cardservice.specifications.CardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService{
    private final CardRepository cardRepository;
    private final MessageSource messageSource;
    private final AccountServiceFeign accountServiceFeign;
    private final CardFormatConfig cardFormatConfig;

//    @Value("${card.validation.pan-format}")
//    private String panFormat;
//
//    @Value("${card.validation.cvv-format}")
//    private String cvvFormat;

    @Override
    public CardDto saveCard(CardDto dto) {
        validateAccountExist(dto);
        validateCardDetails(dto);
        validateDataUniqueness(dto);

        Card card = CardMapper.toEntity(dto);
        card = cardRepository.save(card);
        return CardMapper.toEntityDataHidden(card,false);
    }

    @Override
    public Page<CardDto> getAllCards(int page, int size, Long accountId, String cardAlias,
                              String pan, CardType cardType, Boolean showSensitiveData) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Card> spec = CardSpecification.filterCardDetails(pan, cardType, cardAlias,accountId);
        Page<Card> card = cardRepository.findAll(spec, pageable);
        return card.map(cards -> CardMapper.toEntityDataHidden(cards, showSensitiveData));
    }

    @Override
    public CardDto getCardById(Long cardId, Boolean showSensitiveData) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException(
                        messageSource.getMessage("card.not.found", new Object[]{cardId},null)));
        return CardMapper.toEntityDataHidden(card,showSensitiveData);
    }

    @Override
    public CardDto updateCardDetails(Long cardId, UpdateCardDto dto) {
        Card existingCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException(
                        messageSource.getMessage("card.not.found", new Object[]{cardId},null)));

        if (dto.getCardAlias() != null && !dto.getCardAlias().trim().isEmpty()) {
            existingCard.setCardAlias(dto.getCardAlias());
        }

        Card updatedCard = cardRepository.save(existingCard);

        return CardMapper.toEntityDataHidden(updatedCard, false);
    }

    @Override
    public String deleteCard(Long cardId) {
        Card existingAccount = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException(
                        messageSource.getMessage("card.not.found", new Object[]{cardId},null)));

        cardRepository.delete(existingAccount);
        return messageSource.getMessage("card.deletion.successful", new Object[]{cardId},null);
    }

    public void validateCardDetails(CardDto dto) {
        if (dto.getCvv() == null) {
            throw new IllegalArgumentException(messageSource.getMessage("cvv.mandatory", null, null));
        } else if (!dto.getCvv().matches(cardFormatConfig.getCvvFormat())) {
            throw new IllegalArgumentException(messageSource.getMessage("invalid.card.cvvformat", null, null));
        }

        if (dto.getTypeOfCard() == null ||
                (!dto.getTypeOfCard().equals(CardType.VIRTUAL) && !dto.getTypeOfCard().equals(CardType.PHYSICAL))) {
            throw new IllegalArgumentException(messageSource.getMessage("invalid.card.type", null, null));
        }

        if (dto.getPan() == null) {
            throw  new IllegalArgumentException(messageSource.getMessage("pan.mandatory", null, null));
        }else if (!dto.getPan().matches(cardFormatConfig.getPanFormat())){
            throw new IllegalArgumentException(messageSource.getMessage("invalid.card.panformat", null, null));
        }
    }

    private void validateDataUniqueness(CardDto dto) {
        if (cardRepository.existsByAccountIdAndCardType(dto.getAccountId(), dto.getTypeOfCard())){
            throw new IllegalArgumentException(messageSource.getMessage("account.type.exist", new Object[]{dto.getAccountId()},null));
        }
        if (cardRepository.existsByPan(dto.getPan())){
            throw new IllegalArgumentException(messageSource.getMessage("card.pan.exist", new Object[]{dto.getAccountId()},null));
        }
    }

    private void validateAccountExist(CardDto dto) {
        if (dto.getAccountId() == null|| dto.getAccountId() ==0L) {
            throw new IllegalArgumentException(messageSource.getMessage("account.detail.missing", null, null));
        }
        try {
            accountServiceFeign.getAccountById(dto.getAccountId());
        }catch (Exception e){
            throw new IllegalArgumentException(messageSource.getMessage("account.not.found", new Object[]{dto.getAccountId()}, null));
        }
    }
}
