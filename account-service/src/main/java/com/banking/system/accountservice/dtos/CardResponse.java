package com.banking.system.accountservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardResponse {
    private List<CardDto> card;
    private long totalElements;
    private int totalPages;
    private int number;
    private int size;
}
