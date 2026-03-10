package com.jyothi.smartexpensetracker.mapper;

import com.jyothi.smartexpensetracker.dto.ExpenseRequestDTO;
import com.jyothi.smartexpensetracker.dto.ExpenseResponseDTO;
import com.jyothi.smartexpensetracker.entity.Expense;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class ExpenseMapper {

    public Expense toEntity(ExpenseRequestDTO requestDTO){
        Expense expense = new Expense();
        expense.setTitle(requestDTO.title());
        expense.setAmount(requestDTO.amount());
        expense.setCategory(requestDTO.category());
        expense.setDate(requestDTO.date());

        return expense;
    }

    public ExpenseResponseDTO toDTO(Expense expense){

        return new ExpenseResponseDTO(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDate()
        );
    }

    public List<ExpenseResponseDTO> toListDTOs(List<Expense> expenses){
        List<ExpenseResponseDTO> expenseDTOs = new LinkedList<>();

        for(Expense expense: expenses){
            expenseDTOs.add(toDTO(expense));
        }
        return expenseDTOs;
    }
}
