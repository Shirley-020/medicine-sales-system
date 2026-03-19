package medicine.example.medicine_sales_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import medicine.example.medicine_sales_system.common.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/drug")
public class DrugExportController {

    @GetMapping("/export")
    public ApiResponse<Object> export(@RequestParam(required = false) String q) {
        String filename = "drug_export" + (q != null ? ("_" + q) : "") + ".xlsx";
        return ApiResponse.success(Map.of("url", "/api/files/" + filename));
    }
}
