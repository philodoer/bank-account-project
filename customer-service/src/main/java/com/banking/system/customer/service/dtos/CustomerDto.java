package com.banking.system.customer.service.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Data representation for customers")
public class CustomerDto {
    @Schema(description = "Unique identifier for the customer",
            example = "1001", accessMode = Schema.AccessMode.READ_ONLY)
    private Long customerId;

    @Schema(description = "First name of the customer",
            example = "Alice")
    @NotBlank(message = "{first.name.validation}")
    private String firstName;

    @Schema(description = "Last name of the customer. Must not be blank.",
            example = "Johnson")
    @NotBlank(message = "{last.name.validation}")
    private String lastName;

    @Schema(description = "Middle or other name of the customer",
            example = "Marie")
    private String otherName;

    @Schema(description = "Timestamp when the customer was created",
            example = "2024-09-18T14:23:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
}
