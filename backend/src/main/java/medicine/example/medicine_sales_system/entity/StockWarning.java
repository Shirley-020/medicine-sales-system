package medicine.example.medicine_sales_system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_warning")
public class StockWarning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "drug_id")
    private Drug drug;

    @Column(name = "warning_type")
    private String warningType; // e.g. LOW_STOCK

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "status")
    private String status; // NEW, PROCESSED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_reminded_at")
    private LocalDateTime lastRemindedAt;

    public StockWarning() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Drug getDrug() { return drug; }
    public void setDrug(Drug drug) { this.drug = drug; }

    public String getWarningType() { return warningType; }
    public void setWarningType(String warningType) { this.warningType = warningType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastRemindedAt() { return lastRemindedAt; }
    public void setLastRemindedAt(LocalDateTime lastRemindedAt) { this.lastRemindedAt = lastRemindedAt; }
}
