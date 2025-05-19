package com.banking.system.accountservice.specifications;

import com.banking.system.accountservice.models.Account;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Account specification to create a query filter to be used on fetching accounts
 * support iban and customer id
 */
public class AccountSpecifications {

    public static Specification<Account> accountWithFilter(Long customerId, String iban) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (customerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("customerId"), customerId));
            }

            if (iban != null) {
                predicates.add(criteriaBuilder.like(root.get("iban"), "%" + iban + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
