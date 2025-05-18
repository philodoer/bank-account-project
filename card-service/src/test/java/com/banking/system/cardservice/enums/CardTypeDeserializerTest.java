package com.banking.system.cardservice.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardTypeDeserializerTest {
    private final CardTypeDeserializer deserializer = new CardTypeDeserializer();

    @Test
    public void testDeserialize_ValidValue() throws Exception {
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        when(parser.getText()).thenReturn("VIRTUAL");

        CardType result = deserializer.deserialize(parser, context);

        assertEquals(CardType.VIRTUAL, result);
    }

    @Test
    public void testDeserialize_InvalidValue() throws Exception {
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        when(parser.getText()).thenReturn("VIRTUALS");

        CardType result = deserializer.deserialize(parser, context);

        assertNull(result);
    }
}