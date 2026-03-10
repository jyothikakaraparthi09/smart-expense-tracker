package com.jyothi.smartexpensetracker.repository;

import com.jyothi.smartexpensetracker.dto.CategorySummary;
import com.jyothi.smartexpensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByCategory(String category);

    @Query("""
    SELECT e.category AS category, SUM(e.amount) as total FROM Expense e 
    WHERE MONTH(e.date) = :month
        AND YEAR(e.date) = :year
    GROUP BY e.category
    """)
    List<CategorySummary> getMonthlySummary(int month, int year);

    @Query("""
    SELECT e.category AS category, SUM(e.amount) as total FROM Expense e GROUP BY e.category ORDER BY total DESC
    """)
    List<CategorySummary> findCategorySummary();

    @Query("SELECT SUM(amount) from Expense")
    Long getTotalAmountSpent();
}
