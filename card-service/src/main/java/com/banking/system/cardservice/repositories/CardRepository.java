package com.banking.system.cardservice.repositories;

import com.banking.system.cardservice.enums.CardType;
import com.banking.system.cardservice.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {
    boolean existsByAccountIdAndCardType(Long accountId, CardType cardType);
    boolean existsByPan(String pan);
}
