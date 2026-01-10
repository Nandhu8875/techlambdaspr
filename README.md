Swagger:http://localhost:8080/swagger-ui/index.html#/
1.Reports
By Category: GET http://localhost:8080/api/expenses/reports/category
By Payment Mode: GET http://localhost:8080/api/expenses/reports/payment-mode
By Month (Monthly Trend): GET http://localhost:8080/api/expenses/reports/monthly-trend?year=2025
2. Top Categories by Spending
GET http://localhost:8080/api/expenses/reports/top-categories?limit=5
3. Spending Trends
Daily Trend: GET http://localhost:8080/api/expenses/reports/daily-trend?startDate=2025-01-01&endDate=2025-01-31
Monthly Trend: GET http://localhost:8080/api/expenses/reports/monthly-trend?year=2025
4. Combined Income vs Expense Summary
GET http://localhost:8080/api/expenses/reports/income-vs-expense
5. Filtering, Sorting, and Pagination (Listing API)
Base Endpoint: GET http://localhost:8080/api/expenses
With Filters, Sorting, and Pagination (Example): GET http://localhost:8080/api/expenses?page=0&size=10&sort=date,desc&category=Food&startDate=2025-01-01&endDate=2025-01-31
