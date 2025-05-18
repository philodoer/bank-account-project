package com.banking.system.customer.service.services;

import com.banking.system.customer.service.dtos.AccountResponse;
import com.banking.system.customer.service.dtos.CustomerDto;
import com.banking.system.customer.service.feign.AccountServiceFeign;
import com.banking.system.customer.service.models.Customer;
import com.banking.system.customer.service.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private AccountServiceFeign accountServiceFeign;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDto createDto;
    int page = 0;
    int size = 10;

    @BeforeEach
    void setUp() {
        customer = new Customer();
            customer.setCustomerId(1L);
            customer.setFirstName("John");
            customer.setLastName("Doe");

        createDto = new CustomerDto();
            createDto.setFirstName("John");
            createDto.setLastName("Doe");

    }


    @Test
    void saveCustomer_returnCreatedCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDto result = customerService.saveCustomer(createDto);

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void saveCustomer_InvalidDtoShowException_missingFirstName() {
        createDto = new CustomerDto();
        createDto.setLastName("Doe");

        when(messageSource.getMessage(eq("first.name.validation"), isNull(), any()))
                .thenReturn("First name is required");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.saveCustomer(createDto));

        assertEquals("First name is required", exception.getMessage());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void saveCustomer_InvalidDtoShowException_missingLastName() {
        createDto = new CustomerDto();
        createDto.setFirstName("John");

        when(messageSource.getMessage(eq("last.name.validation"), isNull(), any()))
                .thenReturn("Last name is required");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.saveCustomer(createDto));

        assertEquals("Last name is required", exception.getMessage());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void getAllCustomers_WithFilters() {
        String name = "John";
        LocalDate startDate = LocalDate.of(2025, 5, 1);
        LocalDate endDate = LocalDate.of(2025, 5, 31);

        Pageable pageable = PageRequest.of(page, size);

        Page<Customer> customerPage = new PageImpl<>(List.of(customer));

        when(customerRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(customerPage);

        Page<CustomerDto> result = customerService.getAllCustomers(page, size, name, startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getCustomerId());
        verify(customerRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getAllCustomers_WithoutFilters() {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = new PageImpl<>(Collections.emptyList());

        when(customerRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(customerPage);

        Page<CustomerDto> result = customerService.getAllCustomers(page, size, null, null, null);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(customerRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getCustomerById_ExistingId() {
        Long customerId = 1L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        CustomerDto result = customerService.getCustomerById(customerId);

        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals("John", result.getFirstName());
        verify(customerRepository).findById(customerId);
    }

    @Test
    void getCustomerById_NonExistingId() {
        Long customerId = 1000L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("customer.not.found"), any(), any()))
                .thenReturn("Customer not found");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.getCustomerById(customerId));

        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository).findById(customerId);
    }

    @Test
    void updateCustomer_ValidUpdate() {
        Long customerId = 1L;
        CustomerDto updateDto = new CustomerDto();
        updateDto.setFirstName("Philip");
        updateDto.setLastName("Shaw");
        updateDto.setOtherName("Luke");

        Customer savedCustomer = new Customer();
        savedCustomer.setCustomerId(customerId);
        savedCustomer.setFirstName("Philip");
        savedCustomer.setLastName("Shaw");
        savedCustomer.setOtherName("Luke");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(savedCustomer);

        CustomerDto result = customerService.updateCustomer(customerId, updateDto);

        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals("Philip", result.getFirstName());
        assertEquals("Shaw", result.getLastName());
        assertEquals("Luke", result.getOtherName());
        verify(customerRepository).findById(customerId);
        verify(customerRepository).save(customer);
    }

    @Test
    void updateCustomer_NonExistingId() {
        Long customerId = 1000L;
        CustomerDto updateDto = new CustomerDto();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("customer.not.found"), any(), any()))
                .thenReturn("Customer not found");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.updateCustomer(customerId, updateDto));

        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void deleteCustomer_NoAccounts() {
        Long customerId = 1L;

        AccountResponse emptyAccountResponse = new AccountResponse();
        emptyAccountResponse.setTotalElements(0L);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(accountServiceFeign.getAccountsByCustomerCode(eq(customerId), eq(0), eq(1)))
                .thenReturn(emptyAccountResponse);
        when(messageSource.getMessage(eq("customer.deleted.success"), any(), any()))
                .thenReturn("Customer deleted successfully");

        String result = customerService.deleteCustomer(customerId);

        assertEquals("Customer deleted successfully", result);
        verify(customerRepository).findById(customerId);
        verify(accountServiceFeign).getAccountsByCustomerCode(customerId, 0, 1);
        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomer_WithAccounts() {
        Long customerId = 1L;

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setTotalElements(1L);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(accountServiceFeign.getAccountsByCustomerCode(eq(customerId), eq(0), eq(1)))
                .thenReturn(accountResponse);
        when(messageSource.getMessage(eq("customer.has.accounts"), any(), any()))
                .thenReturn("Customer has accounts and cannot be deleted");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.deleteCustomer(customerId));

        assertEquals("Customer has accounts and cannot be deleted", exception.getMessage());
        verify(customerRepository).findById(customerId);
        verify(accountServiceFeign).getAccountsByCustomerCode(customerId, 0, 1);
        verify(customerRepository, never()).delete(any(Customer.class));
    }

    @Test
    void deleteCustomer_NonExistingId() {
        Long customerId = 1000L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("customer.not.found"), any(), any()))
                .thenReturn("Customer not found");


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.deleteCustomer(customerId));

        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository).findById(customerId);
        verify(accountServiceFeign, never()).getAccountsByCustomerCode(anyLong(), anyInt(), anyInt());
        verify(customerRepository, never()).delete(any(Customer.class));
    }
}