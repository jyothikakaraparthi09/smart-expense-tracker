package com.jyothi.smartexpensetracker.repository;

import com.jyothi.smartexpensetracker.dto.CategorySummary;
import com.jyothi.smartexpensetracker.entity.Expense;
import com.jyothi.smartexpensetracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Page<Expense> findByCategoryAndUserUsername(String category, String username, Pageable pageable);

    Page<Expense> findByUserUsername(String username, Pageable pageable);

    Optional<Expense> findByIdAndUserUsername(Long id,String username);

    @Query("""
    SELECT e.category AS category, SUM(e.amount) as total FROM Expense e 
    WHERE e.user.username = :username
        AND MONTH(e.date) = :month
        AND YEAR(e.date) = :year
    GROUP BY e.category
    """)
    List<CategorySummary> getMonthlySummary(String username, int month, int year);

    @Query("""
    SELECT e.category AS category, SUM(e.amount) as total FROM Expense e 
        where e.user.username = :username
            GROUP BY e.category ORDER BY total DESC
    """)
    List<CategorySummary> findCategorySummary(String username);

    @Query("SELECT SUM(amount) from Expense")
    Long getTotalAmountSpent();

    List<Expense> findAllByUserUsername(String username);
}
