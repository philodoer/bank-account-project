package com.banking.system.cardservice.mappers;

import com.banking.system.cardservice.dtos.CardDto;
import com.banking.system.cardservice.models.Card;
import com.banking.system.cardservice.utils.MaskSensitiveData;

public class CardMapper {
    public  static CardDto toDto(Card entity) {
        if (entity == null) return null;

        CardDto dto = new CardDto();
        dto.setAccountId(entity.getAccountId());
        dto.setTypeOfCard(entity.getCardType());
        dto.setPan(entity.getPan());
        dto.setCvv(entity.getCvv());
        dto.setCardAlias(entity.getCardAlias());

        return dto;
    }

    public static Card toEntity(CardDto dto) {
        if (dto == null) return null;

        Card card = new Card();
        card.setCardId(dto.getCardId());
        card.setAccountId(dto.getAccountId());
        card.setCardType(dto.getTypeOfCard());
        card.setPan(dto.getPan());
        card.setCvv(dto.getCvv());
        card.setCardAlias(dto.getCardAlias());

        return card;
    }

    public static CardDto toEntityDataHidden(Card entity, Boolean showSensitiveData) {

        boolean show = showSensitiveData == null || showSensitiveData;

        CardDto card = new CardDto();
        card.setCardId(entity.getCardId());
        card.setAccountId(entity.getAccountId());
        card.setTypeOfCard(entity.getCardType());
        card.setCardAlias(entity.getCardAlias());

        if (!show) {
            card.setPan(MaskSensitiveData.maskPan(entity.getPan()));
            card.setCvv(MaskSensitiveData.maskCvv(entity.getCvv()));
        } else {
            card.setPan(entity.getPan());
            card.setCvv(entity.getCvv());
        }

        return card;
    }
}
