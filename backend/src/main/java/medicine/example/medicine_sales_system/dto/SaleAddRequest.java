package medicine.example.medicine_sales_system.dto;

import java.util.List;
import java.math.BigDecimal;

public class SaleAddRequest {

    private Integer customerId;
    private String salesman;
    private List<SaleItemRequest> items;

    public static class SaleItemRequest {
        private Integer drugId;
        private Integer quantity;
        private BigDecimal salePrice;

        // Getters and Setters
        public Integer getDrugId() {
            return drugId;
        }
        public void setDrugId(Integer drugId) {
            this.drugId = drugId;
        }
        public Integer getQuantity() {
            return quantity;
        }
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        public BigDecimal getSalePrice() {
            return salePrice;
        }
        public void setSalePrice(BigDecimal salePrice) {
            this.salePrice = salePrice;
        }
    }

    // Getters and Setters
    public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    public List<SaleItemRequest> getItems() {
        return items;
    }
    public void setItems(List<SaleItemRequest> items) {
        this.items = items;
    }
    public String getSalesman() {
        return salesman;
    }
    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }
}
