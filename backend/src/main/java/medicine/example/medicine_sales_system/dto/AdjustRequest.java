package medicine.example.medicine_sales_system.dto;

public class AdjustRequest {
    private Integer quantity;
    private String reason;
    private Long operatorId;
    private String type; // 'in' 入库, 'out' 出库, 'transfer' 调拨, 'correct' 盘点修正

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
