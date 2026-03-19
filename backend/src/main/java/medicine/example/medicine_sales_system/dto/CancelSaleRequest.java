package medicine.example.medicine_sales_system.dto;

public class CancelSaleRequest {
    private Long saleId;
    private String reason;

    public Long getSaleId() { return saleId; }
    public void setSaleId(Long saleId) { this.saleId = saleId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
