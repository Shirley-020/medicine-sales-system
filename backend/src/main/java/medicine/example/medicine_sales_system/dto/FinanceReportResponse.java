package medicine.example.medicine_sales_system.dto;

public class FinanceReportResponse {

    private Double salesAmount;
    private Integer salesCount;

    public FinanceReportResponse(Double salesAmount, Integer salesCount) {
        this.salesAmount = salesAmount;
        this.salesCount = salesCount;
    }

    public Double getSalesAmount() {
        return salesAmount;
    }

    public Integer getSalesCount() {
        return salesCount;
    }
}

