package medicine.example.medicine_sales_system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "stock_batch")
public class StockBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    private DrugBatch batch;

    @Column(nullable = false)
    private Integer qty;

    @Column(name = "warning_qty", nullable = false)
    private Integer warningQty;

    @Column(length = 100)
    private String warehouse;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public StockBatch() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Drug getDrug() { return drug; }
    public void setDrug(Drug drug) { this.drug = drug; }

    public DrugBatch getBatch() { return batch; }
    public void setBatch(DrugBatch batch) { this.batch = batch; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }

    public Integer getWarningQty() { return warningQty; }
    public void setWarningQty(Integer warningQty) { this.warningQty = warningQty; }

    public String getWarehouse() { return warehouse; }
    public void setWarehouse(String warehouse) { this.warehouse = warehouse; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockBatch that = (StockBatch) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return "StockBatch{" + "id=" + id + ", qty=" + qty + '}'; }
}
