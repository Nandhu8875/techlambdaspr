package com.example.expense.controller;

import com.example.expense.dto.ReportDTOs.*;
import com.example.expense.model.Expense;
import com.example.expense.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow frontend integration if needed
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
        return ResponseEntity.ok(expenseService.saveExpense(expense));
    }

    @GetMapping
    public ResponseEntity<Page<Expense>> getAllExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        return ResponseEntity.ok(expenseService.getExpenses(category, startDate, endDate, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable String id) {
        return expenseService.getExpenseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable String id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok().build();
    }

    // Reports

    @GetMapping("/reports/category")
    public ResponseEntity<List<CategorySummary>> getCategorySummary() {
        return ResponseEntity.ok(expenseService.getCategorySummary());
    }

    @GetMapping("/reports/payment-mode")
    public ResponseEntity<List<PaymentModeSummary>> getPaymentModeSummary() {
        return ResponseEntity.ok(expenseService.getPaymentModeSummary());
    }

    @GetMapping("/reports/daily-trend")
    public ResponseEntity<List<DailyTrend>> getDailyTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(expenseService.getDailyTrend(startDate, endDate));
    }

    @GetMapping("/reports/monthly-trend")
    public ResponseEntity<List<MonthlyTrend>> getMonthlyTrend(@RequestParam int year) {
        return ResponseEntity.ok(expenseService.getMonthlyTrend(year));
    }

    @GetMapping("/reports/income-vs-expense")
    public ResponseEntity<IncomeVsExpense> getIncomeVsExpense() {
        return ResponseEntity.ok(expenseService.getIncomeVsExpense());
    }

    @GetMapping("/reports/top-categories")
    public ResponseEntity<List<CategorySummary>> getTopCategories(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(expenseService.getTopCategories(limit));
    }
}
