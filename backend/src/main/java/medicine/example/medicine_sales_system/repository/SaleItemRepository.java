package medicine.example.medicine_sales_system.repository;

import medicine.example.medicine_sales_system.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    // 按时间段汇总销售（按药品），只统计已完成的销售（status = 1）
    @Query("SELECT si.drug.id as drugId, si.drug.name as drugName, SUM(si.qty) as totalQty, SUM(si.amount) as totalAmount " +
           "FROM SaleItem si WHERE si.sale.soldAt BETWEEN :start AND :end AND si.sale.status = 1 GROUP BY si.drug.id, si.drug.name")
    List<SalesSummary> findSalesSummaryBySoldAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    List<SaleItem> findBySale_Id(Long saleId);
}
