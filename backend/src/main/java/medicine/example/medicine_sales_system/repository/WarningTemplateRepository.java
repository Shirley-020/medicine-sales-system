package medicine.example.medicine_sales_system.repository;

import medicine.example.medicine_sales_system.entity.WarningTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarningTemplateRepository extends JpaRepository<WarningTemplate, Long> {
    Optional<WarningTemplate> findByName(String name);
}
