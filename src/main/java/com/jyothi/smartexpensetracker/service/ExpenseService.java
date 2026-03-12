package com.jyothi.smartexpensetracker.service;

import com.jyothi.smartexpensetracker.dto.CategorySummary;
import com.jyothi.smartexpensetracker.dto.ExpenseRequestDTO;
import com.jyothi.smartexpensetracker.dto.ExpenseResponseDTO;
import com.jyothi.smartexpensetracker.entity.User;
import com.jyothi.smartexpensetracker.exception.ExpenseNotFoundException;
import com.jyothi.smartexpensetracker.mapper.ExpenseMapper;
import com.jyothi.smartexpensetracker.entity.Expense;
import com.jyothi.smartexpensetracker.repository.ExpenseRepository;
import com.jyothi.smartexpensetracker.repository.UserRepository;
import com.jyothi.smartexpensetracker.utility.SecurityUtility;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final UserRepository userRepository;

    private final ExpenseMapper mapper;

    Logger log = Logger.getLogger(ExpenseService.class.getName());
   public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository, ExpenseMapper mapper){

        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public ExpenseResponseDTO createExpense(ExpenseRequestDTO requestDTO){

        String username = SecurityUtility.getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow();
       Expense expense = mapper.toEntity(requestDTO);
       expense.setUser(user);
       Expense savedExpense = expenseRepository.save(expense);

        return mapper.toDTO(savedExpense);

    }

    public ExpenseResponseDTO getExpense(Long id){
       Expense expense = expenseRepository.findById(id).orElseThrow( () -> new ExpenseNotFoundException(("Expense not found with id: "+id)));
       return mapper.toDTO(expense);
    }

    public List<ExpenseResponseDTO> getAllExpenses(){

        String username = SecurityUtility.getCurrentUsername();
        log.info("Username: {}"+username);
        List<ExpenseResponseDTO> expenseDTOs = mapper.toListDTOs(expenseRepository.findByUserUsername(username));
        return expenseDTOs;
    }

    public ExpenseResponseDTO updateExpense(Long id, ExpenseRequestDTO request){

        String username = SecurityUtility.getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow();

        Expense expense = mapper.toEntity(request);
        expense.setUser(user);
        Expense existing = expenseRepository.findByIdAndUserUsername(id,username).orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id: "+id));

        if(!expense.getUser().getUsername().equals(username)){
            throw new RuntimeException("You are not allowed to update this expense");
        }
        existing.setTitle(expense.getTitle());
        existing.setAmount(expense.getAmount());
        existing.setCategory(expense.getCategory());
        existing.setDate(expense.getDate());
        return mapper.toDTO(expenseRepository.save(existing));
    }

    public void deleteExpense(Long id){

        String username = SecurityUtility.getCurrentUsername();

        Expense expense = expenseRepository.findByIdAndUserUsername(id, username)
                        .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));
        expenseRepository.deleteById(id);
    }

    public List<ExpenseResponseDTO> getExpensesByCategory(String category){

        String username = SecurityUtility.getCurrentUsername();
        return mapper.toListDTOs(expenseRepository.findByCategoryAndUserUsername(category, username));
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
        return expenseRepository.findAll().stream().mapToDouble(Expense :: getAmount).sum();
    }

    public Map<String, Double> getTopSpentCategory(){
        String username = SecurityUtility.getCurrentUsername();

        List<CategorySummary> result = expenseRepository.findCategorySummary(username);

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
