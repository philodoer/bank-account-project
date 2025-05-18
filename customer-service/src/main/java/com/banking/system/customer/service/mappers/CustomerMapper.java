package com.banking.system.customer.service.mappers;

import com.banking.system.customer.service.dtos.CustomerDto;
import com.banking.system.customer.service.models.Customer;

public class CustomerMapper {
    /**
     * Converts a Customer entity to a CustomerDto.
     *
     * @param customer the entity to convert
     * @return the corresponding DTO
     */
    public static CustomerDto toDto(Customer customer) {
        if (customer == null) return null;

        CustomerDto dto = new CustomerDto();
        dto.setCustomerId(customer.getCustomerId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setOtherName(customer.getOtherName());
        dto.setCreatedAt(customer.getCreatedAt());
        return dto;
    }

    /**
     * Converts a CustomerDto to a Customer entity.
     *
     * @param dto the DTO to convert
     * @return the corresponding entity
     */
    public static Customer toEntity(CustomerDto dto) {
        if (dto == null) return null;

        Customer customer = new Customer();
        customer.setCustomerId(dto.getCustomerId());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setOtherName(dto.getOtherName());
        customer.setCreatedAt(dto.getCreatedAt());
        return customer;
    }
}
