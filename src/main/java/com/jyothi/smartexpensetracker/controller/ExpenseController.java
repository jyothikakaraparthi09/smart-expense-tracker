package com.jyothi.smartexpensetracker.controller;

import com.jyothi.smartexpensetracker.dto.ExpenseRequestDTO;
import com.jyothi.smartexpensetracker.dto.ExpenseResponseDTO;
import com.jyothi.smartexpensetracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service){
        this.service = service;
    }

    @Operation(summary = "Creates a new expense")
    @PostMapping
    public ExpenseResponseDTO addExpense(@Valid @RequestBody ExpenseRequestDTO requestDTO){

        return service.createExpense(requestDTO);
    }

    @Operation(summary = "Get expense by ID")
    @GetMapping("/expense/{id}")
    public ExpenseResponseDTO getExpense(@PathVariable Long id){
        return service.getExpense(id);
    }

    @Operation(summary = "Gets all expenses ")
    @GetMapping
    public Page<ExpenseResponseDTO> getAllExpenses(@RequestParam int size,@RequestParam int page){
        return service.getAllExpenses(page,size);
    }

    @Operation(summary = "Gets all expenses based on the category")
    @GetMapping("/category/{category}")
    public Page<ExpenseResponseDTO> getByCategory(@PathVariable String category, @RequestParam int size, @RequestParam int page)
    {
        return service.getExpensesByCategory(category,size, page);
    }

    @Operation(summary = "Updates the expense with new data ")
    @PutMapping("/{id}")
    public  ExpenseResponseDTO updateExpense(@PathVariable Long id, @RequestBody ExpenseRequestDTO request){
        return service.updateExpense(id, request);
    }

    @Operation(summary = "Removes expense based on id ")
    @DeleteMapping("/{id}")
    public String deleteExpense(@PathVariable Long id){
        service.deleteExpense(id);

        return "Expense deleted";
    }

    @Operation(summary = "Fetches the total amount spend for each category ")
    @GetMapping("/category-summary")
    public Map<String, Double> getCategorySummary(){
        return service.getCategorySummary();
    }

    @Operation(summary = "Gets all the expenses based on Month and Year ")
    @GetMapping("/monthly-summary/{year}/{month}")
    public Map<String, Double> getMonthlySummary(@PathVariable int year, @PathVariable int month){
        return service.getMonthlySummary(month, year);
    }

    @Operation(summary = "Sums all expenses amount")
    @GetMapping("/total-spent")
    public Double getTotalAmountSpent(){
        return service.getTotalExpense();
    }

    @Operation(summary = "Fetches the top amount spent category ")
    @GetMapping("/top-category")
    public Map<String, Double> getTopSpentCategory(){
        return service.getTopSpentCategory();
    }
}
