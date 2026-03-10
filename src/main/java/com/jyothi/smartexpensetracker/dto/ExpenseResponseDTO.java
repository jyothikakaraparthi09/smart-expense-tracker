package com.jyothi.smartexpensetracker.dto;

import java.time.LocalDate;

public record ExpenseResponseDTO(
        Long id,
        String title,
        Double amount,
        String category,
        LocalDate date
) {
}
