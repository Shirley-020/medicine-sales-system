package medicine.example.medicine_sales_system.repository;

import medicine.example.medicine_sales_system.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findBySoldAtBetween(LocalDateTime start, LocalDateTime end);
    List<Sale> findByCustomer_Id(Long customerId);
}
