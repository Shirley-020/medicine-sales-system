package medicine.example.medicine_sales_system.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import medicine.example.medicine_sales_system.entity.StockWarning;
import medicine.example.medicine_sales_system.entity.WarningTemplate;
import medicine.example.medicine_sales_system.repository.StockWarningRepository;
import medicine.example.medicine_sales_system.repository.WarningTemplateRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockWarningService {

    private final StockWarningRepository stockWarningRepository;
    private final WarningTemplateRepository warningTemplateRepository;

    public StockWarningService(StockWarningRepository stockWarningRepository,
                               WarningTemplateRepository warningTemplateRepository) {
        this.stockWarningRepository = stockWarningRepository;
        this.warningTemplateRepository = warningTemplateRepository;
    }

    public StockWarning createWarning(Long drugId, String message, String warningType, medicine.example.medicine_sales_system.repository.DrugRepository drugRepo) {
        var drug = drugRepo.findById(drugId).orElse(null);
        StockWarning w = new StockWarning();
        w.setDrug(drug);
        w.setMessage(message);
        w.setWarningType(warningType);
        w.setStatus("NEW");
        w.setCreatedAt(LocalDateTime.now());
        w.setUpdatedAt(LocalDateTime.now());
        return stockWarningRepository.save(w);
    }

    @Transactional
    public StockWarning processWarning(Long warningId, String action) {
        StockWarning w = stockWarningRepository.findById(warningId).orElseThrow(() -> new RuntimeException("warning not found"));
        w.setStatus("PROCESSED");
        w.setMessage((w.getMessage() == null ? "" : w.getMessage()) + " | processed:" + action);
        w.setUpdatedAt(LocalDateTime.now());
        return stockWarningRepository.save(w);
    }

    public WarningTemplate saveTemplate(WarningTemplate t) {
        var now = LocalDateTime.now();
        if (t.getId() == null) {
            t.setCreatedAt(now);
        }
        t.setUpdatedAt(now);
        return warningTemplateRepository.save(t);
    }

    public void remind(Long warningId, String channel, String message) {
        StockWarning w = stockWarningRepository.findById(warningId).orElseThrow(() -> new RuntimeException("warning not found"));
        // 简化：这里只保存提醒时间并在日志打印
        w.setLastRemindedAt(LocalDateTime.now());
        stockWarningRepository.save(w);
        System.out.println("Remind via " + channel + " for warning " + warningId + ": " + message);
    }

    @Transactional
    public int batchProcess(List<Long> ids, String action) {
        int count = 0;
        for (Long id : ids) {
            try {
                processWarning(id, action);
                count++;
            } catch (Exception ex) {
                // continue
            }
        }
        return count;
    }
}
