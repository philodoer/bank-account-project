package com.banking.system.customer.service.services;

import com.banking.system.customer.service.dtos.AccountResponse;
import com.banking.system.customer.service.dtos.CustomerDto;
import com.banking.system.customer.service.feign.AccountServiceFeign;
import com.banking.system.customer.service.mappers.CustomerMapper;
import com.banking.system.customer.service.models.Customer;
import com.banking.system.customer.service.repositories.CustomerRepository;
import com.banking.system.customer.service.specifications.CustomerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final MessageSource messageSource;
    private final AccountServiceFeign accountServiceFeign;

    @Override
    public CustomerDto saveCustomer(CustomerDto dto) {

        validateCustomerDto(dto);

        Customer customer = CustomerMapper.toEntity(dto);
        Customer savedCustomer = customerRepository.save(customer);
        return CustomerMapper.toDto(savedCustomer);
    }

    @Override
    public Page<CustomerDto> getAllCustomers(int page, int size, String name, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Customer> spec = CustomerSpecification.findWithFilters(name, startDate, endDate);
        Page<Customer> customers = customerRepository.findAll(spec, pageable);
        return customers.map(CustomerMapper::toDto);
    }


    private void validateCustomerDto(CustomerDto dto) {
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage("first.name.validation", null, null));
        }

        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage("last.name.validation", null, null));
        }
    }

    @Override
    public CustomerDto updateCustomer(Long customerId, CustomerDto dto) {
        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        messageSource.getMessage("customer.not.found", new Object[]{customerId},null)));

        if (dto.getFirstName() != null && !dto.getFirstName().trim().isEmpty()) {
            existingCustomer.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null && !dto.getLastName().trim().isEmpty()) {
            existingCustomer.setLastName(dto.getLastName());
        }

        if (dto.getOtherName() != null && !dto.getOtherName().trim().isEmpty()) {
            existingCustomer.setOtherName(dto.getOtherName());
        }

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return CustomerMapper.toDto(updatedCustomer);
    }


    @Override
    public String deleteCustomer(Long customerId){
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        messageSource.getMessage("customer.not.found", new Object[]{customerId},null)));

        AccountResponse accounts = accountServiceFeign.getAccountsByCustomerCode(
                customer.getCustomerId(),0,1
        );

        if (accounts.getTotalElements() > 0) {
            throw new IllegalArgumentException(
                    messageSource.getMessage("customer.has.accounts", new Object[]{customerId}, null)
            );
        }
        customerRepository.delete(customer);
        return messageSource.getMessage("customer.deleted.success", new Object[]{customerId},null);
    }

    @Override
    public CustomerDto getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        messageSource.getMessage("customer.not.found", new Object[]{customerId},null)));

        return  CustomerMapper.toDto(customer);
    }

}
