package medicine.example.medicine_sales_system.repository;

import medicine.example.medicine_sales_system.entity.StockBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockBatchRepository extends JpaRepository<StockBatch, Long> {
    List<StockBatch> findByDrug_IdAndQtyGreaterThanOrderByBatch_ExpireDateAsc(Long drugId, int qty);

    // 库存预警：库存 <= 警戒值
    @Query("SELECT s FROM StockBatch s WHERE s.qty <= s.warningQty")
    List<StockBatch> findLowStockBatches();

    // 即将过期的库存：90天内过期
    @Query("SELECT s FROM StockBatch s WHERE s.batch.expireDate BETWEEN :today AND :futureDate")
    List<StockBatch> findExpiringSoonBatches(java.time.LocalDate today, java.time.LocalDate futureDate);

    // 已过期的库存
    @Query("SELECT s FROM StockBatch s WHERE s.batch.expireDate < :today")
    List<StockBatch> findExpiredBatches(java.time.LocalDate today);

    // 按药品编码查库存
    List<StockBatch> findByDrug_Code(String code);

    // 查找指定药品与批次的库存记录
    List<StockBatch> findByDrug_IdAndBatch_Id(Long drugId, Long batchId);

    // 查找指定药品的所有批次库存记录
    List<StockBatch> findByDrug_Id(Long drugId);
} 
