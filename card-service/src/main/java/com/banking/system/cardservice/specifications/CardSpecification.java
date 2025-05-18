package com.banking.system.cardservice.specifications;

import com.banking.system.cardservice.enums.CardType;
import com.banking.system.cardservice.models.Card;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CardSpecification {
    public static Specification<Card> filterCardDetails(String pan, CardType cardType, String cardAlias, Long accountId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (pan != null && !pan.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("pan"), "%" + pan + "%"));
            }

            if (cardAlias != null && !cardAlias.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("cardAlias"), "%" + cardAlias + "%"));
            }

            if (cardType != null) {
                predicates.add(criteriaBuilder.equal(root.get("cardType"), cardType ));
            }

            if (accountId != null ) {
                predicates.add(criteriaBuilder.equal(root.get("accountId"), accountId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
