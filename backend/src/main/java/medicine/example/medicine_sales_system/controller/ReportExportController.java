package medicine.example.medicine_sales_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import medicine.example.medicine_sales_system.common.ApiResponse;
import medicine.example.medicine_sales_system.service.ReportService;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportExportController {

    private final ReportService reportService;

    public ReportExportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales/export")
    public ApiResponse<Object> exportSales(@RequestParam String start, @RequestParam String end) {
        // 真实实现应返回文件流；此处返回临时 URL
        String filename = "sales_report_" + start + "_" + end + ".xlsx";
        return ApiResponse.success(Map.of("url", "/api/files/" + filename));
    }

    @GetMapping("/finance/export")
    public ApiResponse<Object> exportFinance(@RequestParam String periodType, @RequestParam String start, @RequestParam String end) {
        String filename = "finance_report_" + start + "_" + end + ".xlsx";
        return ApiResponse.success(Map.of("url", "/api/files/" + filename));
    }
}
