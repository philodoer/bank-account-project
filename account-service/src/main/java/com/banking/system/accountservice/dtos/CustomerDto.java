package com.banking.system.accountservice.dtos;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "customer response",description = "Response object containing a Customer details.")
public class CustomerDto {
    @Schema(description = "Identifier of the referenced customer")
    private Long customerId;
}
