package medicine.example.medicine_sales_system.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import medicine.example.medicine_sales_system.repository.DrugRepository;
import medicine.example.medicine_sales_system.repository.StockBatchRepository;
import medicine.example.medicine_sales_system.entity.Drug;

@Service
public class DrugService {

    private final DrugRepository drugRepository;
    private final StockBatchRepository stockBatchRepository;

    public DrugService(DrugRepository drugRepository, StockBatchRepository stockBatchRepository) {
        this.drugRepository = drugRepository;
        this.stockBatchRepository = stockBatchRepository;
    }

    private Map<String, Object> drugToMap(Drug drug) {
        Map<String, Object> map = new HashMap<>();

        // 统一字段名并处理 null，确保与 DrugList.vue 兼容
        map.put("id", drug.getId());
        map.put("drugCode", drug.getCode());
        map.put("drugName", drug.getName());
        map.put("specification", drug.getSpec());
        map.put("unit", drug.getUnit());
        map.put("manufacturer", drug.getManufacturer());
        map.put("createTime", drug.getCreatedAt());

        // 安全地处理 price，避免前端 toFixed(2) 错误
        java.math.BigDecimal retailPrice = drug.getRetailPrice();
        map.put("price", retailPrice != null ? retailPrice : java.math.BigDecimal.ZERO);

        // 将 Byte 类型的 status 转换为前端期望的 String
        Byte statusByte = drug.getStatus();
        String statusString = "discontinued"; // 默认为停售
        if (statusByte != null && statusByte == 1) {
            statusString = "normal";
        }
        map.put("status", statusString);

        // 为前端必需但 drug 表中不存在的字段提供模拟数据
        map.put("genericName", "通用-" + (drug.getName() != null ? drug.getName() : "未知"));
        map.put("drugType", "western");
        map.put("isPrescription", false);
        
        // 计算实际库存：同一种药品的所有批次库存之和
        int totalStock = stockBatchRepository.findByDrug_Id(drug.getId())
                .stream()
                .mapToInt(stockBatch -> stockBatch.getQty() != null ? stockBatch.getQty() : 0)
                .sum();
        map.put("stock", totalStock);
        
        // 最低库存设为100
        map.put("minStock", 100);
        map.put("expiryDate", java.time.LocalDate.now().plusMonths(18));

        return map;
    }

    public List<Map<String, Object>> getDrugList() {
        return drugRepository.findAll().stream()
                .map(this::drugToMap)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getDrugById(Integer id) {
        Drug drug = drugRepository.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("药品不存在, id=" + id));
        return drugToMap(drug);
    }

    public void addDrug(String name, Double price) {
        Drug drug = new Drug();
        drug.setName(name);
        if (price != null) {
            drug.setRetailPrice(java.math.BigDecimal.valueOf(price));
        }
        drug.setCreatedAt(java.time.LocalDateTime.now());
        drug.setUpdatedAt(java.time.LocalDateTime.now());
        drug.setStatus((byte) 1); // 1 = normal
        drugRepository.save(drug);
    }

    public void updateDrug(Integer id, String name, Double price, String specification, String unit, String manufacturer, String status) {
        Drug drug = drugRepository.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("药品不存在，id=" + id));
        
        if (name != null && !name.trim().isEmpty()) {
            drug.setName(name);
        }
        if (price != null) {
            drug.setRetailPrice(java.math.BigDecimal.valueOf(price));
        }
        if (specification != null) {
            drug.setSpec(specification);
        }
        if (unit != null) {
            drug.setUnit(unit);
        }
        if (manufacturer != null) {
            drug.setManufacturer(manufacturer);
        }
        if (status != null) {
            // 将前端的状态字符串转换为 Byte
            // "normal" -> 1, 其他 -> 0
            if ("normal".equals(status)) {
                drug.setStatus((byte) 1);
            } else {
                drug.setStatus((byte) 0);
            }
        }
        drug.setUpdatedAt(java.time.LocalDateTime.now());
        
        drugRepository.save(drug);
    }

    public void deleteDrug(Integer id) {
        if (!drugRepository.existsById(id.longValue())) {
            throw new RuntimeException("药品不存在，id=" + id);
        }
        drugRepository.deleteById(id.longValue());
    }
}
