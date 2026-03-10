package com.jyothi.smartexpensetracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record ExpenseRequestDTO(

        @NotBlank(message = "Title cannot be empty")
        String title,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        Double amount,

        @NotBlank(message = "Category cannot be empty")
        String category,

        @NotNull(message = "Date is required")
        LocalDate date
) {
}
