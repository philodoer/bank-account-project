package com.banking.system.accountservice.controllers;

import com.banking.system.accountservice.dtos.AccountDto;
import com.banking.system.accountservice.dtos.AccountUpdateDto;
import com.banking.system.accountservice.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Account management controller
 * Contains - Saving, updating, fetchAll and fetchById, deleting accounts endpoints.
 *
 */

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "Account management", description = "All CRUD operations on accounts")
public class AccountController {
    private final AccountService accountService;

    /**
     * @param dto
     * @return saved Accountdto
     */
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


    /**
     *
     * @param page - the page number to be fetched
     * @param size - Expected size of the page, number of records
     * @param iban - mapped to account Iban Number - Not mandatory, used for filter
     * @param cardAlias - Account that has card with this card Alias - Not mandatory, used for filter
     * @param customerId - customer linked to the account
     * @return paginated list of filtered accounts
     */
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

    /**
     *
     * @param accountId - id to be fetched
     * @return AccountDto of the fetched record
     */

    @GetMapping("/{accountId}")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Fetch a account by its ID")
    public ResponseEntity<AccountDto> getAccountById(
            @PathVariable Long accountId
    ) {
        return new ResponseEntity<>(accountService.getAccountById(accountId), HttpStatus.OK);
    }

    /**
     *
     * @param dto - update dto, only Iban and BIC/SWIFT can be updated
     * @param accountId - account to be updated
     * @return -AccountDto of the updated record
     */
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

    /**
     * Delete Account based on accountId
     * @param accountId - record unique id
     * @return success/error message
     */
    @DeleteMapping("/{accountId}")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Delete An account. Accounts with cards cannot be deleted")
    public ResponseEntity<String> delete(
            @PathVariable Long accountId){
        return new ResponseEntity<>(accountService.deleteAccount(accountId), HttpStatus.OK);
    }
}
