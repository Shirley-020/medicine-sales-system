package medicine.example.medicine_sales_system.dto;

public class PurchaseConfirmRequest {
    private Long warehouseId;
    private Long receiverId;
    private String note;

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
