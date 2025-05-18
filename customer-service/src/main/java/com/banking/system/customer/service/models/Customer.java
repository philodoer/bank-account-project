package com.banking.system.customer.service.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "CUSTOMERS")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    /**
     * A unique system generated identifier
     * Customer table primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUST_ID", nullable = false, updatable = false)
    private Long customerId;

    @Column(name = "CUST_FIRST_NAME", nullable = false, length = 100)
    private String firstName;

    @Column(name = "CUST_LAST_NAME", nullable = false, length = 100)
    private String lastName;

    @Column(name = "CUST_OTHER_NAME", length = 100)
    private String otherName;

    /**
     * Date the account was created
     */
    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
