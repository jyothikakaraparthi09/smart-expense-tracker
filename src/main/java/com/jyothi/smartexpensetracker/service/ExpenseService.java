package com.jyothi.smartexpensetracker.service;

import com.jyothi.smartexpensetracker.dto.CategorySummary;
import com.jyothi.smartexpensetracker.dto.ExpenseRequestDTO;
import com.jyothi.smartexpensetracker.dto.ExpenseResponseDTO;
import com.jyothi.smartexpensetracker.exception.ExpenseNotFoundException;
import com.jyothi.smartexpensetracker.mapper.ExpenseMapper;
import com.jyothi.smartexpensetracker.entity.Expense;
import com.jyothi.smartexpensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {
    private final ExpenseRepository repository;

    @Autowired
    ExpenseMapper mapper;

    public ExpenseService(ExpenseRepository repository){
        this.repository = repository;
    }

    public ExpenseResponseDTO createExpense(ExpenseRequestDTO requestDTO){

        Expense expense = mapper.toEntity(requestDTO);
        Expense savedExpense = repository.save(expense);

        return mapper.toDTO(savedExpense);

    }

    public ExpenseResponseDTO getExpense(Long id){
       Expense expense = repository.findById(id).orElseThrow( () -> new ExpenseNotFoundException(("Expense not found with id: "+id)));
       return mapper.toDTO(expense);
    }

    public List<ExpenseResponseDTO> getAllExpenses(){
        List<ExpenseResponseDTO> expenseDTOs = mapper.toListDTOs(repository.findAll());
        return expenseDTOs;
    }

    public ExpenseResponseDTO updateExpense(Long id, ExpenseRequestDTO request){

        Expense expense = mapper.toEntity(request);
        Expense existing = repository.findById(id).orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id: "+id));

        existing.setTitle(expense.getTitle());
        existing.setAmount(expense.getAmount());
        existing.setCategory(expense.getCategory());
        existing.setDate(expense.getDate());

        return mapper.toDTO(repository.save(existing));
    }

    public void deleteExpense(Long id){

        repository.deleteById(id);
    }

    public List<ExpenseResponseDTO> getExpensesByCategory(String category){

        return mapper.toListDTOs(repository.findByCategory(category));
    }

    public Map<String, Double> getCategorySummary(){
        List<CategorySummary> results = repository.findCategorySummary();

        Map<String, Double> summary = new HashMap<>();

        for(CategorySummary category: results){
            summary.put((String) category.getCategory() , (Double) category.getTotal());
        }

        return  summary;
    }

    public Map<String, Double> getMonthlySummary(int month, int year){
        List<CategorySummary> results = repository.getMonthlySummary(month, year);

        Map<String,Double> summary = new HashMap<>();

        for(CategorySummary category: results){
            summary.put((String) category.getCategory(), (Double) category.getTotal());
        }

        return summary;
    }

    public Double getTotalExpense(){
        return repository.findAll().stream().mapToDouble(Expense :: getAmount).sum();
    }

    public Map<String, Double> getTopSpentCategory(){
        List<CategorySummary> result = repository.findCategorySummary();

        if(result.isEmpty()){
            return Map.of();
        }

        CategorySummary summary = result.get(0);

        return Map.of(
                (String) summary.getCategory(),
                (Double) summary.getTotal()
        );
    }
}
