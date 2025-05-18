package com.banking.system.cardservice.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class CardTypeDeserializer extends JsonDeserializer<CardType> {

    @Override
    public CardType deserialize(JsonParser jsonParser,
                                DeserializationContext deserializationContext)
            throws IOException {
        String cardType = jsonParser.getText();
        try {
            return CardType.valueOf(cardType.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }
}
