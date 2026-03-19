package medicine.example.medicine_sales_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medicine.example.medicine_sales_system.common.ApiResponse;
import medicine.example.medicine_sales_system.dto.FinanceReportResponse;
import medicine.example.medicine_sales_system.service.SaleService;

@RestController
@RequestMapping("/finance")
public class FinanceController {

    private final SaleService saleService;

    public FinanceController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping("/today")
    public ApiResponse<FinanceReportResponse> today() {
        return ApiResponse.success(saleService.getTodayFinanceReport());
    }

    @GetMapping("/month")
    public ApiResponse<FinanceReportResponse> month() {
        // 当前阶段，月报 = 今日逻辑
        return ApiResponse.success(saleService.getTodayFinanceReport());
    }
}
