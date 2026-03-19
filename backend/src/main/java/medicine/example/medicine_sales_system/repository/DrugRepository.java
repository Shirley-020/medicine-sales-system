package medicine.example.medicine_sales_system.repository;

import medicine.example.medicine_sales_system.entity.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface DrugRepository extends JpaRepository<Drug, Long> {
    Optional<Drug> findByCode(String code);

    // 常用查找
    List<Drug> findByNameContainingIgnoreCase(String name);
    List<Drug> findByStatus(Byte status);
    List<Drug> findByManufacturerContainingIgnoreCase(String manufacturer);
} 
