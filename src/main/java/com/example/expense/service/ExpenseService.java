package com.example.expense.service;

import com.example.expense.dto.ReportDTOs.*;
import com.example.expense.model.Expense;
import com.example.expense.model.ExpenseType;
import com.example.expense.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final MongoTemplate mongoTemplate;

    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Page<Expense> getExpenses(String category, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Query query = new Query().with(pageable);
        if (category != null) {
            query.addCriteria(Criteria.where("category").is(category));
        }
        if (startDate != null && endDate != null) {
            query.addCriteria(Criteria.where("date").gte(startDate).lte(endDate));
        }

        List<Expense> list = mongoTemplate.find(query, Expense.class);
        long count = mongoTemplate.count(query, Expense.class);
        return new org.springframework.data.domain.PageImpl<>(list, pageable, count);
    }

    public Optional<Expense> getExpenseById(String id) {
        return expenseRepository.findById(id);
    }

    public void deleteExpense(String id) {
        expenseRepository.deleteById(id);
    }

    public List<CategorySummary> getCategorySummary() {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("type").is(ExpenseType.EXPENSE)),
                group("category").sum("amount").as("totalAmount"),
                project("totalAmount").and("category").previousOperation());
        AggregationResults<CategorySummary> results = mongoTemplate.aggregate(aggregation, "expenses",
                CategorySummary.class);
        return results.getMappedResults();
    }

    public List<PaymentModeSummary> getPaymentModeSummary() {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("type").is(ExpenseType.EXPENSE)),
                group("paymentMode").sum("amount").as("totalAmount"),
                project("totalAmount").and("paymentMode").previousOperation());
        AggregationResults<PaymentModeSummary> results = mongoTemplate.aggregate(aggregation, "expenses",
                PaymentModeSummary.class);
        return results.getMappedResults();
    }

    public List<DailyTrend> getDailyTrend(LocalDate startDate, LocalDate endDate) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("date").gte(startDate).lte(endDate).and("type").is(ExpenseType.EXPENSE)),
                group("date").sum("amount").as("totalAmount"),
                project("totalAmount").and("date").previousOperation(),
                sort(org.springframework.data.domain.Sort.Direction.ASC, "date"));
        AggregationResults<DailyTrend> results = mongoTemplate.aggregate(aggregation, "expenses", DailyTrend.class);
        return results.getMappedResults();
    }

    public IncomeVsExpense getIncomeVsExpense() {
        Aggregation aggregation = newAggregation(
                group("type").sum("amount").as("totalAmount"),
                project("totalAmount").and("type").previousOperation());

        // This is a simplified manual aggregation after fetching grouped results
        // Ideally can be done in one complex aggregation.
        // Let's stick to simple individual queries if aggregation is too complex for
        // now, or just iterate results.

        // Let's try to do two queries or one aggregation that groups by type
        // The above query groups by TYPE (INCOME/EXPENSE).

        // Result will be List of {type: INCOME, totalAmount}, {type: EXPENSE,
        // totalAmount}

        // Since we need to return a single object, let's fetch list and map.
        List<java.util.Map> results = mongoTemplate.aggregate(aggregation, "expenses", java.util.Map.class)
                .getMappedResults();

        double income = 0;
        double expense = 0;

        for (java.util.Map map : results) {
            String type = map.get("_id").toString(); // Enum name
            Double amount = (Double) map.get("totalAmount");
            if (ExpenseType.INCOME.name().equals(type)) {
                income = amount;
            } else if (ExpenseType.EXPENSE.name().equals(type)) {
                expense = amount;
            }
        }
        return new IncomeVsExpense(income, expense, income - expense);
    }

    public List<CategorySummary> getTopCategories(int limit) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("type").is(ExpenseType.EXPENSE)),
                group("category").sum("amount").as("totalAmount"),
                project("totalAmount").and("category").previousOperation(),
                sort(org.springframework.data.domain.Sort.Direction.DESC, "totalAmount"),
                limit(limit));
        AggregationResults<CategorySummary> results = mongoTemplate.aggregate(aggregation, "expenses",
                CategorySummary.class);
        return results.getMappedResults();
    }

    public List<MonthlyTrend> getMonthlyTrend(int year) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("type").is(ExpenseType.EXPENSE).andOperator(
                        Criteria.where("date").gte(LocalDate.of(year, 1, 1)).lte(LocalDate.of(year, 12, 31)))),
                project("amount").and(org.springframework.data.mongodb.core.aggregation.DateOperators.Year.year("date"))
                        .as("year")
                        .and(org.springframework.data.mongodb.core.aggregation.DateOperators.Month.month("date"))
                        .as("month"),
                group("year", "month").sum("amount").as("totalAmount"),
                project("totalAmount").and("_id.year").as("year").and("_id.month").as("month"),
                sort(org.springframework.data.domain.Sort.Direction.ASC, "year", "month"));
        AggregationResults<MonthlyTrend> results = mongoTemplate.aggregate(aggregation, "expenses", MonthlyTrend.class);
        return results.getMappedResults();
    }
}
