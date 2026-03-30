package com.jyothi.smartexpensetracker.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CursorResponse<ExpenseResponseDTO>(List<ExpenseResponseDTO> content,
                                                 LocalDateTime nextCursor,
                                                 boolean hasNext) {
}
