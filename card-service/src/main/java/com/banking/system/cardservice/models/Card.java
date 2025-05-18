package com.banking.system.cardservice.models;

import com.banking.system.cardservice.enums.CardType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "BANK_CARDS", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"CARD_ACC_ID", "CARD_TYPE"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CARD_ID", nullable = false, updatable = false)
    private Long cardId;

    @Column(name = "CARD_ACC_ID", nullable = false, updatable = false)
    private Long accountId;

    @Column(name = "CARD_TYPE", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Column(name = "CARD_PAN_CODE", nullable = false, unique = true)
    private String pan;

    @Column(name = "CARD_CVV_NUMBER", nullable = false)
    private String cvv;

    @Column(name = "CARD_ALIAS")
    private String cardAlias;

    @CreationTimestamp
    @Column(name = "CARD_CREATED_AT")
    private LocalDateTime createdAt;
}
