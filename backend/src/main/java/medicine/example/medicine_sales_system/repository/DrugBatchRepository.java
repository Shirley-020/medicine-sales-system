package medicine.example.medicine_sales_system.repository;

import medicine.example.medicine_sales_system.entity.DrugBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DrugBatchRepository extends JpaRepository<DrugBatch, Long> {
    List<DrugBatch> findByDrug_Id(Long drugId);
    List<DrugBatch> findByExpireDateBefore(LocalDate date);
    List<DrugBatch> findByExpireDateBetween(LocalDate start, LocalDate end);
    List<DrugBatch> findByDrug_IdAndBatchNo(Long drugId, String batchNo);
} 
