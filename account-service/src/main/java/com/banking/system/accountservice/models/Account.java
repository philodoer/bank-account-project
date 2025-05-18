package com.banking.system.accountservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Show all the accounts and relevant details relating to the account
 */

@Getter
@Setter
@Entity
@Table(name = "ACCOUNTS", uniqueConstraints = {
        @UniqueConstraint(columnNames = "iban")
})
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    /**
     * A unique system generated identifier
     * Account table primary key
      */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACC_ID", nullable = false)
    private Long accountId;

    /**
     * Iban - international Bank Account Number
     * A unique key used to identify account Number globally
     */
    @Column(name = "ACC_IBAN", nullable = false, unique = true)
    private String iban;

    /**
     * Bank identification code
     */
    @Column(name = "ACC_BICSWIFT", nullable = false)
    private String bicSwift;

    /**
     * Id of the customer using the account.
     * Cannot be null as it attach account to a customer.
     */
    @Column(name = "ACC_CUST_ID", nullable = false)
    private Long customerId;

    /**
     * Date the account was created
     */
    @CreationTimestamp
    @Column(name = "ACC_CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
