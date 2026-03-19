package medicine.example.medicine_sales_system.controller;

import medicine.example.medicine_sales_system.dto.LowStockDto;
import medicine.example.medicine_sales_system.dto.SalesSummaryDto;
import medicine.example.medicine_sales_system.dto.DailySalesReportDto;
import medicine.example.medicine_sales_system.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<LowStockDto>> lowStock() {
        return ResponseEntity.ok(reportService.getLowStockWarnings());
    }

    @GetMapping("/sales")
    public ResponseEntity<?> sales(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        // 如果提供了date参数，返回当日销售报表
        if (date != null) {
            return ResponseEntity.ok(reportService.getDailySalesReport(date));
        }
        // 否则使用原有的时间段查询
        if (start != null && end != null) {
            return ResponseEntity.ok(reportService.getSalesSummary(start, end));
        }
        // 默认返回今日报表
        return ResponseEntity.ok(reportService.getDailySalesReport(LocalDate.now()));
    }
}
