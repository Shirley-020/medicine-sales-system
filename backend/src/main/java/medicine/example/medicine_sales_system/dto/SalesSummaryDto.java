package medicine.example.medicine_sales_system.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class SalesSummaryDto implements Serializable {
    private Long drugId;
    private String drugName;
    private Long totalQty;
    private BigDecimal totalAmount;

    public SalesSummaryDto() {}

    public SalesSummaryDto(Long drugId, String drugName, Long totalQty, BigDecimal totalAmount) {
        this.drugId = drugId;
        this.drugName = drugName;
        this.totalQty = totalQty;
        this.totalAmount = totalAmount;
    }

    public Long getDrugId() { return drugId; }
    public void setDrugId(Long drugId) { this.drugId = drugId; }

    public String getDrugName() { return drugName; }
    public void setDrugName(String drugName) { this.drugName = drugName; }

    public Long getTotalQty() { return totalQty; }
    public void setTotalQty(Long totalQty) { this.totalQty = totalQty; }

    public java.math.BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(java.math.BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
