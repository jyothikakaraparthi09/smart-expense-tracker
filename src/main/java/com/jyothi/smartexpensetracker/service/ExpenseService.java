package com.jyothi.smartexpensetracker.service;

import com.jyothi.smartexpensetracker.dto.*;
import com.jyothi.smartexpensetracker.entity.User;
import com.jyothi.smartexpensetracker.exception.ExpenseNotFoundException;
import com.jyothi.smartexpensetracker.mapper.ExpenseMapper;
import com.jyothi.smartexpensetracker.entity.Expense;
import com.jyothi.smartexpensetracker.repository.ExpenseRepository;
import com.jyothi.smartexpensetracker.repository.UserRepository;
import com.jyothi.smartexpensetracker.utility.SecurityUtility;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final UserRepository userRepository;

    Logger log = Logger.getLogger(ExpenseService.class.getName());
   public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository){

        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @CacheEvict(value = "expenses", allEntries = true)
    public ExpenseResponseDTO createExpense(String username,ExpenseRequestDTO requestDTO){

       User user = userRepository.findByUsername(username).orElseThrow();
       Expense expense = ExpenseMapper.toEntity(requestDTO);
       expense.setUser(user);
       Expense savedExpense = expenseRepository.save(expense);

        return ExpenseMapper.toDTO(savedExpense);

    }

    public ExpenseResponseDTO getExpense(Long id){
       Expense expense = expenseRepository.findById(id).orElseThrow( () -> new ExpenseNotFoundException(("Expense not found with id: "+id)));
       return ExpenseMapper.toDTO(expense);
    }

    public Page<ExpenseResponseDTO> getAllExpenses(String username,int size, int page){

        Pageable pageable = PageRequest.of(page, size);

        return expenseRepository.findByUserUsername(username, pageable).map(ExpenseMapper::toDTO);
    }

    public ExpenseResponseDTO updateExpense(Long id, ExpenseRequestDTO request){

        String username = SecurityUtility.getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow();

        Expense expense = ExpenseMapper.toEntity(request);
        expense.setUser(user);
        Expense existing = expenseRepository.findByIdAndUserUsername(id,username).orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id: "+id));

        if(!expense.getUser().getUsername().equals(username)){
            throw new RuntimeException("You are not allowed to update this expense");
        }
        existing.setTitle(expense.getTitle());
        existing.setAmount(expense.getAmount());
        existing.setCategory(expense.getCategory());
        existing.setDate(expense.getDate());
        return ExpenseMapper.toDTO(expenseRepository.save(existing));
    }

    public void deleteExpense(Long id){

        String username = SecurityUtility.getCurrentUsername();

        Expense expense = expenseRepository.findByIdAndUserUsername(id, username)
                        .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));

        log.info("Removing Expense:"+ id);
        expenseRepository.deleteById(id);
    }

    public Page<ExpenseResponseDTO> getExpensesByCategory(String category,int size, int page){

        String username = SecurityUtility.getCurrentUsername();
        Pageable pageable = PageRequest.of(page,size,Sort.by("date").descending());
        return expenseRepository.findByCategoryAndUserUsername(category, username, pageable).map(ExpenseMapper::toDTO);
    }

    public Map<String, Double> getCategorySummary(){

        String username = SecurityUtility.getCurrentUsername();

        List<CategorySummary> results = expenseRepository.findCategorySummary(username);

        Map<String, Double> summary = new HashMap<>();

        for(CategorySummary category: results){
            summary.put((String) category.getCategory() , (Double) category.getTotal());
        }

        return  summary;
    }

    public Map<String, Double> getMonthlySummary(int month, int year){

        String username = SecurityUtility.getCurrentUsername();

        List<CategorySummary> results = expenseRepository.getMonthlySummary(username, month, year);

        Map<String,Double> summary = new HashMap<>();

        for(CategorySummary category: results){
            summary.put((String) category.getCategory(), (Double) category.getTotal());
        }

        return summary;
    }

    public Double getTotalExpense(){
        String username = SecurityUtility.getCurrentUsername();
        return expenseRepository.findAllByUserUsername(username).stream().mapToDouble(Expense :: getAmount).sum();
    }

    public Map<String, Double> getTopSpentCategory(){
        String username = SecurityUtility.getCurrentUsername();

        List<CategorySummary> result = expenseRepository.findCategorySummary(username);

        if(result.isEmpty()){
            return Map.of();
        }

        CategorySummary summary = result.getFirst();

        return Map.of(
                (String) summary.getCategory(),
                (Double) summary.getTotal()
        );
    }
}
