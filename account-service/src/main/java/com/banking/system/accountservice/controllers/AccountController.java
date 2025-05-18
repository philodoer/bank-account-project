package com.banking.system.accountservice.controllers;

import com.banking.system.accountservice.dtos.AccountDto;
import com.banking.system.accountservice.dtos.AccountUpdateDto;
import com.banking.system.accountservice.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(summary = "Create a new account. " +
            "Requires a valid IBAN and BicSwift code. " +
            "The provided customerId must correspond to an existing customer.")
    public ResponseEntity<AccountDto> save(
            @Parameter(description = "Account details",required = true)
            @Valid @RequestBody AccountDto dto
    ) {
        AccountDto returnedDto = accountService.saveAccount(dto);
        return new ResponseEntity<>(returnedDto, HttpStatus.CREATED);
    }


    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "fetch all Accounts, Filters supported include customerId, Iban")
    public ResponseEntity<Page<AccountDto>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String iban,
            @RequestParam(required = false) String cardAlias,
            @RequestParam(required = false) Long customerId) {
        return new ResponseEntity<>(accountService.findAllAccounts(page, size, customerId, iban, cardAlias), HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Fetch a account by its ID")
    public ResponseEntity<AccountDto> getAccountById(
            @PathVariable Long accountId
    ) {
        return new ResponseEntity<>(accountService.getAccountById(accountId), HttpStatus.OK);
    }


    @PutMapping("/{accountId}")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Update an existing account. " +
            "Requires a valid IBAN and BicSwift code. " +
            "The provided customerId must correspond to an existing customer.")
    public ResponseEntity<AccountDto> update(
            @Valid @RequestBody AccountUpdateDto dto,
            @PathVariable Long accountId
    ){
        return new ResponseEntity<>(accountService.updateAccount(accountId, dto),HttpStatus.OK);
    }

    @DeleteMapping("/{accountId}")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Delete An account. Accounts with cards cannot be deleted")
    public ResponseEntity<String> delete(
            @PathVariable Long accountId){
        return new ResponseEntity<>(accountService.deleteAccount(accountId), HttpStatus.OK);
    }
}
