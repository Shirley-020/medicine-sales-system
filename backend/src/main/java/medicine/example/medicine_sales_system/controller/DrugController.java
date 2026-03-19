package medicine.example.medicine_sales_system.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medicine.example.medicine_sales_system.common.ApiResponse;
import medicine.example.medicine_sales_system.dto.DrugAddRequest;
import medicine.example.medicine_sales_system.dto.DrugUpdateRequest;
import medicine.example.medicine_sales_system.service.DrugService;


@RestController
@RequestMapping("/drug")

public class DrugController {

    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    @GetMapping("/list")
    public ApiResponse<List<Map<String, Object>>> listDrugs() {
        return ApiResponse.success(drugService.getDrugList());
    }
    @GetMapping("/detail/{id}")
    public ApiResponse<Map<String, Object>> getDrugById(@PathVariable Integer id) {
        return ApiResponse.success(drugService.getDrugById(id));
    }
    @PostMapping("/add")
    public ApiResponse<String> addDrug(@RequestBody DrugAddRequest request) {
        drugService.addDrug(request.getName(), request.getPrice());
        return ApiResponse.success("添加成功");
    }
    @PutMapping("/update")
    public ApiResponse<String> updateDrug(@RequestBody DrugUpdateRequest request) {
        drugService.updateDrug(
            request.getId(),
            request.getName(),
            request.getPrice(),
            request.getSpecification(),
            request.getUnit(),
            request.getManufacturer(),
            request.getStatus()
        );
        return ApiResponse.success("修改成功");
    }


}
