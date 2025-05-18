package com.banking.system.customer.service.controllers;

import com.banking.system.customer.service.dtos.CustomerDto;
import com.banking.system.customer.service.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private CustomerDto createDto;
    private CustomerDto fetchDto;

    @BeforeEach
    void setUp() {
        createDto = new CustomerDto();
            createDto.setFirstName("John");
            createDto.setLastName("Doe");
            createDto.setOtherName("Smith");

        fetchDto = new CustomerDto();
            fetchDto.setCustomerId(1L);
            createDto.setFirstName("John");
            createDto.setLastName("Doe");
            createDto.setOtherName("Smith");
    }

    @Test
    void saveCustomer() {

        when(customerService.saveCustomer(createDto)).thenReturn(fetchDto);

        ResponseEntity<CustomerDto> response = customerController.save(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(fetchDto, response.getBody());
        verify(customerService).saveCustomer(createDto);
    }

    @Test
    void findAllCustomers_NoFilters() {
        int page = 0;
        int size = 10;
        String name = "John";
        LocalDate startDate = LocalDate.of(2025, 5, 1);
        LocalDate endDate = LocalDate.of(2025, 5, 31);

        Page<CustomerDto> expectedPage = new PageImpl<>(List.of(fetchDto));
        when(customerService.getAllCustomers(page, size, name, startDate, endDate))
                .thenReturn(expectedPage);

        ResponseEntity<Page<CustomerDto>> response = customerController.findAll(
                page, size, name, startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(fetchDto, response.getBody().getContent().get(0));
    }

    @Test
    void findAllCustomers_WithoutFilters() {
        int page = 0;
        int size = 10;

        Page<CustomerDto> expectedPage = new PageImpl<>(Collections.emptyList());
        when(customerService.getAllCustomers(page, size, null, null, null))
                .thenReturn(expectedPage);

        ResponseEntity<Page<CustomerDto>> response = customerController.findAll(
                page, size, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getTotalElements());
    }

    @Test
    void getCustomerById() {
        Long customerId = 1L;
        when(customerService.getCustomerById(customerId)).thenReturn(fetchDto);

        ResponseEntity<CustomerDto> response = customerController.getCustomerById(customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fetchDto, response.getBody());
        verify(customerService).getCustomerById(customerId);
    }

    @Test
    void updateCustomer() {
        Long customerId = 1L;
        CustomerDto updateDto = new CustomerDto();
        updateDto.setFirstName("Philip");
        updateDto.setLastName("Shaw");

        CustomerDto savedDto = new CustomerDto();
        savedDto.setCustomerId(customerId);
        savedDto.setFirstName("Philip");
        savedDto.setLastName("Shaw");

        when(customerService.updateCustomer(customerId, updateDto)).thenReturn(savedDto);

        ResponseEntity<CustomerDto> response = customerController.update(updateDto, customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedDto, response.getBody());
        verify(customerService).updateCustomer(customerId, updateDto);
    }

    @Test
    void deleteCustomer() {
        Long customerId = 1L;
        String successMessage = "customer.deleted.success";

        when(customerService.deleteCustomer(customerId)).thenReturn(successMessage);

        ResponseEntity<String> response = customerController.delete(customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(successMessage, response.getBody());
        verify(customerService).deleteCustomer(customerId);
    }
}