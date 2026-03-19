package medicine.example.medicine_sales_system.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class LowStockDto implements Serializable {
    private Long drugId;
    private String drugCode;
    private String drugName;
    private String batchNo;
    private Integer qty;
    private Integer warningQty;
    private String warehouse;
    private LocalDate expiryDate; // 过期日期
    private String warningType; // 预警类型：'stock_low' 库存不足, 'expiring' 即将过期, 'expired' 已过期

    public LowStockDto() {}

    public LowStockDto(Long drugId, String drugCode, String drugName, String batchNo, Integer qty, Integer warningQty, String warehouse, LocalDate expiryDate, String warningType) {
        this.drugId = drugId;
        this.drugCode = drugCode;
        this.drugName = drugName;
        this.batchNo = batchNo;
        this.qty = qty;
        this.warningQty = warningQty;
        this.warehouse = warehouse;
        this.expiryDate = expiryDate;
        this.warningType = warningType;
    }

    public Long getDrugId() { return drugId; }
    public void setDrugId(Long drugId) { this.drugId = drugId; }

    public String getDrugCode() { return drugCode; }
    public void setDrugCode(String drugCode) { this.drugCode = drugCode; }

    public String getDrugName() { return drugName; }
    public void setDrugName(String drugName) { this.drugName = drugName; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }

    public Integer getWarningQty() { return warningQty; }
    public void setWarningQty(Integer warningQty) { this.warningQty = warningQty; }

    public String getWarehouse() { return warehouse; }
    public void setWarehouse(String warehouse) { this.warehouse = warehouse; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getWarningType() { return warningType; }
    public void setWarningType(String warningType) { this.warningType = warningType; }
}
