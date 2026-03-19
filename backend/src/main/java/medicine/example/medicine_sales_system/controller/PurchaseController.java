package medicine.example.medicine_sales_system.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medicine.example.medicine_sales_system.common.ApiResponse;
import medicine.example.medicine_sales_system.dto.PurchaseAddRequest;
import medicine.example.medicine_sales_system.dto.PurchaseOrderRequest;
import medicine.example.medicine_sales_system.service.PurchaseService;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping("/add")
    public ApiResponse<String> addPurchase(@RequestBody PurchaseAddRequest request) {
        purchaseService.addPurchase(
                request.getDrugId(),
                request.getQuantity(),
                request.getCostPrice()
        );
        return ApiResponse.success("进货成功");
    }

    @PostMapping("/order")
    public ApiResponse<String> addPurchaseOrder(@RequestBody PurchaseOrderRequest request) {
        try {
            purchaseService.addPurchaseOrder(request);
            return ApiResponse.success("进货单保存成功");
        } catch (Exception e) {
            return ApiResponse.error("保存进货单失败: " + e.getMessage());
        }
    }

    @GetMapping("/report")
    public ApiResponse<Map<String, Object>> report() {
        return ApiResponse.success(purchaseService.getPurchaseReport());
    }

    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getPurchaseList(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) String purchaseType,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String warehouse,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        try {
            Map<String, Object> params = new java.util.HashMap<>();
            params.put("orderNo", orderNo);
            params.put("supplierId", supplierId);
            params.put("start", start);
            params.put("end", end);
            params.put("purchaseType", purchaseType);
            params.put("paymentMethod", paymentMethod);
            params.put("warehouse", warehouse);
            params.put("status", status);
            params.put("page", page);
            params.put("limit", limit);
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
            
            return ApiResponse.success(purchaseService.getPurchaseList(params));
        } catch (Exception e) {
            return ApiResponse.error("获取进货单列表失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deletePurchase(@org.springframework.web.bind.annotation.PathVariable Long id) {
        purchaseService.deletePurchase(id);
        return ApiResponse.success("删除成功");
    }

    @PostMapping("/{id}/confirm")
    public ApiResponse<Map<String, Object>> confirmPurchase(@org.springframework.web.bind.annotation.PathVariable Long id,
                                                            @org.springframework.web.bind.annotation.RequestBody(required = false) medicine.example.medicine_sales_system.dto.PurchaseConfirmRequest req) {
        Map<String, Object> result = purchaseService.confirmPurchase(id, req);
        return ApiResponse.success(result);
    }

}
