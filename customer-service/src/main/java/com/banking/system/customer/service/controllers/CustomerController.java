package com.banking.system.customer.service.controllers;

import com.banking.system.customer.service.dtos.CustomerDto;
import com.banking.system.customer.service.services.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(summary = "Customer firstName and lastName are mandatory, other name is optional")
    public ResponseEntity<CustomerDto> save(
            @Parameter(description = "Customer details in JSON format",required = true)
            @Valid @RequestBody CustomerDto dto
    ) {
        CustomerDto returnedDto = customerService.saveCustomer(dto);
        return new ResponseEntity<>(returnedDto, HttpStatus.CREATED);
    }


    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "fetch all customers. Name, date created can be used to filter customers")
    public ResponseEntity<Page<CustomerDto>> findAll(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String name,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return new ResponseEntity<>(customerService.getAllCustomers(page, size, name, startDate,endDate), HttpStatus.OK);
    }

    @GetMapping("/{customerId}")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Fetch a customer using customer Id")
    public ResponseEntity<CustomerDto> getCustomerById(
            @PathVariable Long customerId
    ) {
        return new ResponseEntity<>(customerService.getCustomerById(customerId), HttpStatus.OK);
    }

    @PutMapping("/{customerId}")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "update customer details. Only FirstName, LastName and OtherName can be updated")
    public ResponseEntity<CustomerDto> update(
            @Valid @RequestBody CustomerDto dto,
            @PathVariable Long customerId
    ){
        return new ResponseEntity<>(customerService.updateCustomer(customerId, dto),HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Delete customers. Customer with an account cannot be deleted")
    public ResponseEntity<String> delete(
            @PathVariable Long customerId){
        return new ResponseEntity<>(customerService.deleteCustomer(customerId), HttpStatus.OK);
    }
}
