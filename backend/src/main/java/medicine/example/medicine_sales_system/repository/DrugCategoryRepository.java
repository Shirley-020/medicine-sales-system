package medicine.example.medicine_sales_system.repository;

import medicine.example.medicine_sales_system.entity.DrugCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugCategoryRepository extends JpaRepository<DrugCategory, Long> {
}
