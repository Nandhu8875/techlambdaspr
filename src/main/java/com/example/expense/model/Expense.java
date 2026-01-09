package com.example.expense.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "expenses")
public class Expense {
    @Id
    private String id;
    private String userId;
    private String category;
    private Double amount;
    private LocalDate date;
    private String paymentMode;
    private String description;
    private ExpenseType type; // INCOME or EXPENSE
}
