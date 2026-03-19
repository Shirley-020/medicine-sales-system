package medicine.example.medicine_sales_system.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medicine.example.medicine_sales_system.common.ApiResponse;
import medicine.example.medicine_sales_system.dto.TransferRequest;
import medicine.example.medicine_sales_system.dto.AdjustRequest;
import medicine.example.medicine_sales_system.dto.ReminderRequest;
import medicine.example.medicine_sales_system.dto.TemplateRequest;
import medicine.example.medicine_sales_system.dto.StockDetailDto;
import medicine.example.medicine_sales_system.service.StockWarningService;
import java.util.List;
import java.util.stream.Collectors;
import medicine.example.medicine_sales_system.entity.StockBatch;

import java.util.Map;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final medicine.example.medicine_sales_system.repository.StockBatchRepository stockBatchRepository;
    private final StockWarningService stockWarningService;

    public StockController(
            medicine.example.medicine_sales_system.repository.StockBatchRepository stockBatchRepository,
            StockWarningService stockWarningService) {
        this.stockBatchRepository = stockBatchRepository;
        this.stockWarningService = stockWarningService;
    }

    @PostMapping("/{stockId}/transfer")
    public ApiResponse<Object> transfer(@PathVariable Integer stockId, @RequestBody TransferRequest req) {
        var opt = stockBatchRepository.findById(stockId.longValue());
        if (opt.isEmpty()) return ApiResponse.error("源库存记录不存在");
        var src = opt.get();
        int qty = req.getQuantity() == null ? 0 : req.getQuantity();
        if (src.getQty() < qty) return ApiResponse.error("源库存不足");
        src.setQty(src.getQty() - qty);
        src.setUpdatedAt(java.time.LocalDateTime.now());
        stockBatchRepository.save(src);

        String targetWarehouse = "WH-" + (req.getToWarehouseId() != null ? req.getToWarehouseId() : "TARGET");
        var list = stockBatchRepository.findByDrug_IdAndBatch_Id(src.getDrug().getId(), src.getBatch().getId());
        StockBatch target = null;
        for (var b : list) {
            if (targetWarehouse.equals(b.getWarehouse())) { target = b; break; }
        }
        if (target == null) {
            target = new StockBatch();
            target.setDrug(src.getDrug());
            target.setBatch(src.getBatch());
            target.setWarningQty(100); // 强制设为100
            target.setWarehouse(targetWarehouse);
            target.setQty(qty);
            target.setUpdatedAt(java.time.LocalDateTime.now());
        } else {
            target.setQty(target.getQty() + qty);
            target.setUpdatedAt(java.time.LocalDateTime.now());
        }
        stockBatchRepository.save(target);
        return ApiResponse.success(Map.of("transferred", qty));
    }

    @PostMapping("/{stockId}/adjust")
    public ApiResponse<Object> adjust(@PathVariable Integer stockId, @RequestBody AdjustRequest req) {
        var opt = stockBatchRepository.findById(stockId.longValue());
        if (opt.isEmpty()) return ApiResponse.error("库存记录不存在");
        var sb = opt.get();
        int quantity = req.getQuantity() == null ? 0 : req.getQuantity();
        if (quantity <= 0) return ApiResponse.error("调整数量必须大于0");
        
        String adjustType = req.getType();
        int delta;
        
        // 根据调整类型决定是增加还是减少库存
        if ("out".equals(adjustType)) {
            // 出库：减少库存
            delta = -quantity;
        } else if ("in".equals(adjustType) || "transfer".equals(adjustType) || "correct".equals(adjustType)) {
            // 入库、调拨、盘点修正：增加库存
            delta = quantity;
        } else {
            // 如果没有指定类型，默认按正数处理（兼容旧逻辑）
            delta = quantity;
        }
        
        int newQty = sb.getQty() + delta;
        if (newQty < 0) return ApiResponse.error("调整后库存不能为负");
        sb.setQty(newQty);
        sb.setUpdatedAt(java.time.LocalDateTime.now());
        stockBatchRepository.save(sb);
        return ApiResponse.success(Map.of("newQty", newQty));
    }

    @PostMapping("/warning/{warningId}/process")
    public ApiResponse<Object> processWarning(@PathVariable Long warningId, @RequestBody Map<String, Object> body) {
        String action = (String) body.getOrDefault("action", "handle");
        var w = stockWarningService.processWarning(warningId, action);
        return ApiResponse.success(Map.of("warningId", warningId, "status", w.getStatus()));
    }

    @PostMapping("/warning/template")
    public ApiResponse<Object> saveTemplate(@RequestBody TemplateRequest req) {
        medicine.example.medicine_sales_system.entity.WarningTemplate t = new medicine.example.medicine_sales_system.entity.WarningTemplate();
        t.setName(req.getName());
        t.setRules(req.getRules());
        t.setActions(req.getActions());
        var saved = stockWarningService.saveTemplate(t);
        return ApiResponse.success(Map.of("templateId", saved.getId(), "name", saved.getName()));
    }

    @PostMapping("/warning/{warningId}/remind")
    public ApiResponse<Object> remind(@PathVariable Long warningId, @RequestBody ReminderRequest req) {
        stockWarningService.remind(warningId, req.getChannel(), req.getMessage());
        return ApiResponse.success(Map.of("warningId", warningId, "channel", req.getChannel()));
    }

    @PostMapping("/warning/batch/process")
    public ApiResponse<Object> batchProcess(@RequestBody Map<String, Object> body) {
        var idsObj = body.getOrDefault("warningIds", List.of());
        List<Long> ids = new java.util.ArrayList<>();
        if (idsObj instanceof List) {
            for (Object o : (List<?>) idsObj) {
                try { ids.add(Long.valueOf(o.toString())); } catch (Exception e) {}
            }
        }
        String action = (String) body.getOrDefault("action", "batch");
        int processed = stockWarningService.batchProcess(ids, action);
        return ApiResponse.success(Map.of("processed", processed));
    }

    @GetMapping("/export")
    public ApiResponse<Object> export() {
        return ApiResponse.success(Map.of("url", "/api/files/stock_export.xlsx"));
    }

    @GetMapping("/{drugId}")
    public ApiResponse<Integer> getStock(@PathVariable Integer drugId) {
        // Aggregate qty from DB batches to reflect persisted state
        try {
            long id = drugId.longValue();
            var batches = stockBatchRepository.findByDrug_Id(id);
            int total = 0;
            for (var b : batches) total += b.getQty();
            return ApiResponse.success(total);
        } catch (Exception e) {
            return ApiResponse.error("查询库存失败: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ApiResponse<List<StockDetailDto>> getAllStock() {
        try {
            List<StockBatch> allBatches = stockBatchRepository.findAll();
            List<StockDetailDto> stockDetails = allBatches.stream().map(b -> {
                StockDetailDto dto = new StockDetailDto();
                dto.setId(b.getId().intValue());
                dto.setCurrentStock(b.getQty());
                dto.setMinStock(b.getWarningQty());
                dto.setMaxStock(9999); // Placeholder for max stock
                dto.setLocation(b.getWarehouse());
                dto.setLastUpdated(b.getUpdatedAt());

                if (b.getDrug() != null) {
                    dto.setDrugCode(b.getDrug().getCode());
                    dto.setDrugName(b.getDrug().getName());
                    dto.setSpecification(b.getDrug().getSpec());
                    dto.setUnit(b.getDrug().getUnit());
                    dto.setSupplier(b.getDrug().getManufacturer());
                    dto.setUnitPrice(b.getDrug().getRetailPrice());
                    dto.setIsPrescription(false); // Placeholder
                }

                if (b.getBatch() != null) {
                    dto.setBatchNo(b.getBatch().getBatchNo());
                    dto.setProductionDate(b.getBatch().getProductionDate());
                    dto.setExpiryDate(b.getBatch().getExpireDate());
                }

                return dto;
            }).collect(Collectors.toList());

            return ApiResponse.success(stockDetails);
        } catch (Exception e) {
            return ApiResponse.error("查询全部库存失败: " + e.getMessage());
        }
    }

    @GetMapping("/warning/{drugId}")
    public ApiResponse<String> checkWarning(@PathVariable Integer drugId) {
        // Use DB aggregates to determine warning
        try {
            long id = drugId.longValue();
            var batches = stockBatchRepository.findByDrug_Id(id);
            int total = 0;
            int warning = 0;
            for (var b : batches) {
                total += b.getQty();
                warning = Math.max(warning, b.getWarningQty() != null ? b.getWarningQty() : 0);
            }
            if (total <= warning) {
                return ApiResponse.success("⚠ 库存预警：drugId=" + drugId + "，当前库存=" + total + "，最低库存=" + warning);
            }
            return ApiResponse.success("库存正常");
        } catch (Exception e) {
            return ApiResponse.error("检查预警失败: " + e.getMessage());
        }
    }

    @GetMapping("/suggest/{drugId}")
    public ApiResponse<Integer> suggest(@PathVariable Integer drugId) {
        try {
            long id = drugId.longValue();
            var batches = stockBatchRepository.findByDrug_Id(id);
            int total = 0;
            int warning = 0;
            for (var b : batches) {
                total += b.getQty();
                warning = Math.max(warning, b.getWarningQty() != null ? b.getWarningQty() : 0);
            }
            if (total > warning) return ApiResponse.success(0);
            int target = warning * 2;
            int suggest = target - total;
            return ApiResponse.success(suggest);
        } catch (Exception e) {
            return ApiResponse.error("计算补货建议失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{stockId}")
    public ApiResponse<Object> deleteStock(@PathVariable Integer stockId) {
        try {
            var opt = stockBatchRepository.findById(stockId.longValue());
            if (opt.isEmpty()) {
                return ApiResponse.error("库存记录不存在");
            }
            stockBatchRepository.deleteById(stockId.longValue());
            return ApiResponse.success(Map.of("deleted", true, "stockId", stockId));
        } catch (Exception e) {
            return ApiResponse.error("删除库存失败: " + e.getMessage());
        }
    }

}
