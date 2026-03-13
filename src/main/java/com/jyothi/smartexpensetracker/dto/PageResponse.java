package com.jyothi.smartexpensetracker.dto;

import java.util.List;

public record PageResponse<ExpenseResponseDTO>(
    List<ExpenseResponseDTO> content,
    int page,
    int size,
    long totalElements,
    int totalpages,
    boolean first,
    boolean last
    ){}
