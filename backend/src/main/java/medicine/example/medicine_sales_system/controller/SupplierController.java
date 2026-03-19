package medicine.example.medicine_sales_system.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medicine.example.medicine_sales_system.common.ApiResponse;
import medicine.example.medicine_sales_system.entity.Supplier;
import medicine.example.medicine_sales_system.repository.SupplierRepository;
import java.util.Map;

@RestController
@RequestMapping("/supplier")
public class SupplierController {

    private final SupplierRepository supplierRepository;

    public SupplierController(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @GetMapping("/list")
    public ApiResponse<List<Map<String, Object>>> list() {
        List<Map<String, Object>> out = new java.util.ArrayList<>();
        for (Supplier s : supplierRepository.findAll()) {
            out.add(Map.of(
                "id", s.getId(),
                "name", s.getName(),
                "phone", s.getPhone(),
                "address", s.getAddress(),
                "licenseNo", s.getLicenseNo()
            ));
        }
        return ApiResponse.success(out);
    }
}
