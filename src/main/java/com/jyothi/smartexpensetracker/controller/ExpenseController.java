package com.jyothi.smartexpensetracker.controller;

import com.jyothi.smartexpensetracker.dto.CursorResponse;
import com.jyothi.smartexpensetracker.dto.ExpenseRequestDTO;
import com.jyothi.smartexpensetracker.dto.ExpenseResponseDTO;
import com.jyothi.smartexpensetracker.dto.PageResponse;
import com.jyothi.smartexpensetracker.service.ExpenseService;
import com.jyothi.smartexpensetracker.utility.SecurityUtility;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService service;

    private final Logger log = Logger.getLogger(ExpenseController.class.toString());

    public ExpenseController(ExpenseService service){
        this.service = service;
    }

    @Operation(summary = "Creates a new expense")
    @PostMapping
    public ExpenseResponseDTO addExpense(@Valid @RequestBody ExpenseRequestDTO requestDTO){
        String username = SecurityUtility.getCurrentUsername();
        log.info("Creating expense for "+username+" with amount : "+requestDTO.amount());
        return service.createExpense(username,requestDTO);
    }

    @Operation(summary = "Get expense by ID")
    @GetMapping("/expense/{id}")
    public ExpenseResponseDTO getExpense(@PathVariable Long id){
        return service.getExpense(id);
    }

    @Operation(summary = "Gets all expenses ")
    @GetMapping("/")
    public PageResponse<ExpenseResponseDTO> getAllExpenses(@RequestParam int size, @RequestParam int page){
        String username = SecurityUtility.getCurrentUsername();
        Page<ExpenseResponseDTO> expensePage = service.getAllExpenses(username,size,page);

        return new PageResponse<>(
                expensePage.getContent(),
                expensePage.getNumber(),
                expensePage.getSize(),
                expensePage.getTotalElements(),
                expensePage.getTotalPages(),
                expensePage.isFirst(),
                expensePage.isLast()
        );
    }

    @Operation(summary = "Gets all expenses based on the category")
    @GetMapping("/category/{category}")
    public PageResponse<ExpenseResponseDTO> getByCategory(@PathVariable String category, @RequestParam int size, @RequestParam int page)
    {
        Page<ExpenseResponseDTO> expensePage = service.getExpensesByCategory(category,size, page);

        return new PageResponse<>(
                expensePage.getContent(),
                expensePage.getNumber(),
                expensePage.getSize(),
                expensePage.getTotalElements(),
                expensePage.getTotalPages(),
                expensePage.isFirst(),
                expensePage.isLast()
        );
    }

    @Operation(summary = "Updates the expense with new data ")
    @PutMapping("/{id}")
    public  ExpenseResponseDTO updateExpense(@PathVariable Long id, @RequestBody ExpenseRequestDTO request){
        return service.updateExpense(id, request);
    }

    @Operation(summary = "Removes expense based on id ")
    @DeleteMapping("/{id}")
    public String deleteExpense(@PathVariable Long id){
        log.info("Deleting expense id: "+ id);
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
