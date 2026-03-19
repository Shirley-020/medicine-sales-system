package medicine.example.medicine_sales_system.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class StockService {

    // key: drugId, value: stock quantity
    private final Map<Integer, Integer> stockMap = new HashMap<>();

    public void increaseStock(Integer drugId, Integer quantity) {
        int current = stockMap.getOrDefault(drugId, 0);
        stockMap.put(drugId, current + quantity);

        System.out.println(
                "库存增加：drugId=" + drugId +
                "，当前库存=" + stockMap.get(drugId)
        );
    }

    public Integer getStock(Integer drugId) {
        return stockMap.getOrDefault(drugId, 0);
    }

    public Map<Integer, Integer> getAllStock() {
        return stockMap;
    }

    public boolean decreaseStock(Integer drugId, Integer quantity) {
        int current = stockMap.getOrDefault(drugId, 0);
        if (current < quantity) {
            return false;
        }
        stockMap.put(drugId, current - quantity);

        System.out.println(
            "库存减少：drugId=" + drugId +
            "，当前库存=" + stockMap.get(drugId)
        );
        return true;
    }

    // 最低库存阈值
    private final Map<Integer, Integer> warningLevelMap = new HashMap<>();

    @PostConstruct
    public void initWarningLevel() {
        warningLevelMap.put(1, 10);
        warningLevelMap.put(2, 20);
        warningLevelMap.put(3, 15);
    }

    public String checkWarning(Integer drugId) {
        int current = stockMap.getOrDefault(drugId, 0);
        int warningLevel = warningLevelMap.getOrDefault(drugId, 0);

        if (current <= warningLevel) {
            String msg = "⚠ 库存预警：drugId=" + drugId +
                "，当前库存=" + current +
                "，最低库存=" + warningLevel;
            System.out.println(msg);
            return msg;
        }
        return "库存正常";
    }

    public Integer suggestReplenishment(Integer drugId) {
        int current = stockMap.getOrDefault(drugId, 0);
        int warningLevel = warningLevelMap.getOrDefault(drugId, 0);

        if (current > warningLevel) {
        return 0; // 库存正常，不需要补货
        }

        int target = warningLevel * 2;
        int suggest = target - current;

        System.out.println(
            "智能补货建议：drugId=" + drugId +
            "，当前库存=" + current +
            "，建议补货=" + suggest
        );
        return suggest;
    }
}



