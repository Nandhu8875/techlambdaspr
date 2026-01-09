package com.example.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class ReportDTOs {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategorySummary {
        private String category;
        private Double totalAmount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentModeSummary {
        private String paymentMode;
        private Double totalAmount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyTrend {
        private LocalDate date;
        private Double totalAmount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyTrend {
        private int year;
        private int month;
        private Double totalAmount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IncomeVsExpense {
        private Double totalIncome;
        private Double totalExpense;
        private Double balance;
    }
}
