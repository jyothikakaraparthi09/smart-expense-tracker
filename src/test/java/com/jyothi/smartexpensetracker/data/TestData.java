package com.jyothi.smartexpensetracker.data;

import com.jyothi.smartexpensetracker.dto.CategorySummary;
import com.jyothi.smartexpensetracker.entity.Expense;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestData {
    public static List<Expense> getMockExpenseList(){
        return List.of(
                new Expense("Food Expense",200.0,"Food", LocalDate.now()),
                new Expense("Power Bill",500.0,"Bill", LocalDate.now()),
                new Expense("Home expenses",800.0,"Miscellaneous", LocalDate.now())
        );
    }

    public static Expense getMockExpense(){
        return new Expense("Food Expense",300.0,"Food",LocalDate.now());
    }

    public static List<CategorySummary> getMockSummary(){
        CategorySummary food = mock(CategorySummary.class);
        when(food.getCategory()).thenReturn("Food");
        when(food.getTotal()).thenReturn(300.0);

        CategorySummary bills = mock(CategorySummary.class);
        when(bills.getCategory()).thenReturn("Bill");
        when(bills.getTotal()).thenReturn(500.0);

        return List.of(food,bills);
    }
}
