package medicine.example.medicine_sales_system.repository;

import java.math.BigDecimal;

public interface SalesSummary {
    Long getDrugId();
    String getDrugName();
    Long getTotalQty();
    BigDecimal getTotalAmount();
}
