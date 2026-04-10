package com.jyothi.smartexpensetracker.service;

import com.jyothi.smartexpensetracker.data.TestData;
import com.jyothi.smartexpensetracker.dto.CategorySummary;
import com.jyothi.smartexpensetracker.dto.ExpenseRequestDTO;
import com.jyothi.smartexpensetracker.dto.ExpenseResponseDTO;
import com.jyothi.smartexpensetracker.entity.Expense;
import com.jyothi.smartexpensetracker.entity.User;
import com.jyothi.smartexpensetracker.repository.ExpenseRepository;
import com.jyothi.smartexpensetracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private ExpenseRepository mockRepository;

    @InjectMocks
    private ExpenseService mockService;

    @Test
    void testCreateExpense(){

        Expense expense = TestData.getMockExpense();

        when(mockUserRepository.findByUsername(any(String.class))).thenReturn(Optional.of(new User("jyothi","sldfkj")));
        when(mockRepository.save(any(Expense.class))).thenReturn(expense);

        ExpenseResponseDTO response = mockService.createExpense("Jyothi", new ExpenseRequestDTO("Food Expense",300.0,"Food", LocalDate.now()));
        assertNotNull(response);
        assertEquals("Food Expense", response.title());
    }

    @Test
    void testGetExpenses(){
        List<Expense> expenses = TestData.getMockExpenseList();

        Pageable pageable = PageRequest.of(0,3);

        Page<Expense> page = new PageImpl<>(expenses,pageable,expenses.size());
        when(mockRepository.findByUserUsername(any(String.class),any(Pageable.class))).thenReturn(page);

        Page<ExpenseResponseDTO> responses = mockService.getAllExpenses("Jyothi",3,1);

        assertEquals(3, responses.getContent().size());
        assertEquals(1, responses.getTotalPages());
        assertEquals("Home expenses",responses.getContent().get(2).title());
    }

    @Test
    void testGetCategorySummary(){
        List<CategorySummary> summaries = TestData.getMockSummary();
        when(mockRepository.findCategorySummary("jyothi")).thenReturn(summaries);

        Map<String, Double> summary = mockService.getCategorySummary("jyothi");

        assertEquals(500, summary.get("Bill"));
        assertEquals(300, summary.get("Food"));
    }

    @Test
    void testGetMonthlySummary(){
        List<CategorySummary> summaries = TestData.getMockSummary();
        when(mockRepository.getMonthlySummary(any(String.class),any(Integer.class),any(Integer.class))).thenReturn(summaries);

        Map<String,Double> summary = mockService.getMonthlySummary("jyothi",9,2026);
        assertEquals(500, summary.get("Bill"));
        assertEquals(300, summary.get("Food"));
    }

    @Test
    void testGetTotalExpense(){
        List<Expense> expenses = TestData.getMockExpenseList();
        when(mockRepository.findAllByUserUsername(any(String.class))).thenReturn(expenses);

        Double totalAmount = mockService.getTotalExpense("jyothi");

        assertEquals(1500, totalAmount);
    }
}
