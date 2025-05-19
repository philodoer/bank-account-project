package com.banking.system.cardservice.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "card.validation")
@Getter
@Setter
/**
 * A configuration to fetch card validation details from properties
 * Accessible globally
 */
public class CardFormatConfig {
    private String panFormat;
    private String cvvFormat;
}
