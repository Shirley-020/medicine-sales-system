package medicine.example.medicine_sales_system.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medicine.example.medicine_sales_system.common.ApiResponse;
import medicine.example.medicine_sales_system.dto.CancelSaleRequest;
import medicine.example.medicine_sales_system.dto.SaleAddRequest;
import medicine.example.medicine_sales_system.dto.SaleReturnRequest;
import medicine.example.medicine_sales_system.dto.SaleListDto;
import medicine.example.medicine_sales_system.dto.SaleDetailDto;
import medicine.example.medicine_sales_system.entity.Sale;
import medicine.example.medicine_sales_system.service.SaleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sale")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/cancel")
    public ApiResponse<Object> cancelSale(@RequestBody CancelSaleRequest req) {
        saleService.cancelSale(req.getSaleId());
        return ApiResponse.success(Map.of("saleId", req.getSaleId(), "status", "CANCELLED"));
    }

    @PostMapping("/add")
    public ApiResponse<Sale> addSale(@RequestBody SaleAddRequest request) {
        try {
            Sale newSale = saleService.addSale(request);
            return ApiResponse.success(newSale);
        } catch (Exception e) {
            return ApiResponse.error("Failed to add sale: " + e.getMessage());
        }
    }

    @PostMapping("/return")
    public ApiResponse<String> returnSale(@RequestBody SaleReturnRequest request) {
        try {
            saleService.returnSale(request.getSaleId().longValue(), request.getItems());
            return ApiResponse.success("退货成功");
        } catch (Exception e) {
            return ApiResponse.error("退货失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/detail")
    public ApiResponse<SaleDetailDto> getSaleDetail(@PathVariable Long id) {
        try {
            SaleDetailDto detail = saleService.getSaleDetail(id);
            return ApiResponse.success(detail);
        } catch (Exception e) {
            return ApiResponse.error("获取销售单详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/print")
    public ApiResponse<Object> printSale(@PathVariable Long id) {
        String url = "/api/files/sale_" + id + ".pdf";
        return ApiResponse.success(Map.of("url", url));
    }

    @GetMapping("/report")
    public ApiResponse<List<SaleListDto>> report(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) String status) {
        try {
            // 转换字符串状态为 Byte
            Byte statusByte = null;
            if (status != null && !status.trim().isEmpty()) {
                switch (status.toLowerCase()) {
                    case "completed":
                        statusByte = (byte) 1;
                        break;
                    case "cancelled":
                        statusByte = (byte) 0;
                        break;
                    default:
                        // 如果无法识别，保持为 null（不过滤）
                        break;
                }
            }
            
            List<SaleListDto> sales = saleService.getSaleList(orderNo, customerId, start, end, statusByte);
            return ApiResponse.success(sales);
        } catch (Exception e) {
            return ApiResponse.error("Failed to get sale list: " + e.getMessage());
        }
    }

    @GetMapping("/return/report")
    public ApiResponse<Map<String, Object>> returnReport() {
        // This method is deprecated in SaleService and will throw an exception.
        return ApiResponse.success(Map.of("message", "Return report functionality is currently disabled for refactoring."));
    }

}
