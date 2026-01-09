package com.example.expense.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI expenseTrackerOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Expense Tracker API")
                        .description("API for Expense Management System")
                        .version("v1.0.0"));
    }
}
