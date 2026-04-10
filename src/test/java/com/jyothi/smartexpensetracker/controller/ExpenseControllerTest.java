package com.jyothi.smartexpensetracker.controller;

import com.jyothi.smartexpensetracker.data.TestData;
import com.jyothi.smartexpensetracker.dto.CategorySummary;
import com.jyothi.smartexpensetracker.dto.ExpenseResponseDTO;
import com.jyothi.smartexpensetracker.service.ExpenseService;
import com.jyothi.smartexpensetracker.utility.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
@AutoConfigureMockMvc
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService mockExpenseService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/expenses/").param("size","10").param("page","0"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "jyothi")
    void testGetExpenses() throws Exception{
        when(mockExpenseService.getExpense(1L)).thenReturn(new ExpenseResponseDTO(1L,"Bill",300.0,"Bill", LocalDate.now(), LocalDateTime.now()));

        mockMvc.perform(get("/expenses/expense/{id}",1)).andExpect(status().isOk());
    }

    @Test
    void testGetCategorySummary() throws Exception{

        Authentication auth =
                new UsernamePasswordAuthenticationToken("jyothi", null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);

        Map<String, Double> summary = TestData.getMockSummary().stream().collect(Collectors.toMap(CategorySummary::getCategory, CategorySummary:: getTotal));
        when(mockExpenseService.getCategorySummary("jyothi")).thenReturn(summary);

        mockMvc.perform(get("/expenses/category-summary")).andExpect(status().isOk())
                .andExpect(jsonPath("$.Bill").value(500.0))
                .andExpect(jsonPath("$.Food").value(300.0));

        SecurityContextHolder.clearContext();
    }


}
