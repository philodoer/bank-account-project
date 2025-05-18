package com.banking.system.customer.service.dtos;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccountResponse {
    private List<AccountDto> accounts;
    private long totalElements;
    private int totalPages;
    private int number;
    private int size;
}
