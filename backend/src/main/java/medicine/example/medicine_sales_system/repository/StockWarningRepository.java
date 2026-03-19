package medicine.example.medicine_sales_system.repository;

import medicine.example.medicine_sales_system.entity.StockWarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockWarningRepository extends JpaRepository<StockWarning, Long> {
    List<StockWarning> findByStatus(String status);
}
