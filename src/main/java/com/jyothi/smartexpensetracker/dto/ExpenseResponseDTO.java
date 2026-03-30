package com.jyothi.smartexpensetracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponseDTO(
        Long id,
        String title,
        Double amount,
        String category,
        LocalDate date,
        LocalDateTime createdAt
) {
}
