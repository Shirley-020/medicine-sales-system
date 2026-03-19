package medicine.example.medicine_sales_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import medicine.example.medicine_sales_system.entity.Drug;
import medicine.example.medicine_sales_system.entity.DrugBatch;
import medicine.example.medicine_sales_system.entity.Purchase;
import medicine.example.medicine_sales_system.entity.PurchaseItem;
import medicine.example.medicine_sales_system.entity.StockBatch;
import medicine.example.medicine_sales_system.repository.DrugBatchRepository;
import medicine.example.medicine_sales_system.repository.SupplierRepository;
import medicine.example.medicine_sales_system.repository.UserRepository;
import medicine.example.medicine_sales_system.repository.RoleRepository;
import medicine.example.medicine_sales_system.entity.User;
import medicine.example.medicine_sales_system.entity.Role;
import medicine.example.medicine_sales_system.repository.DrugRepository;
import medicine.example.medicine_sales_system.repository.PurchaseItemRepository;
import medicine.example.medicine_sales_system.repository.PurchaseRepository;
import medicine.example.medicine_sales_system.repository.StockBatchRepository;
import medicine.example.medicine_sales_system.dto.PurchaseConfirmRequest;
import medicine.example.medicine_sales_system.dto.PurchaseOrderRequest;
import medicine.example.medicine_sales_system.entity.Supplier;
import java.time.LocalDate;
import java.math.BigDecimal;

@Service
public class PurchaseService {

    private final StockService stockService;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final StockBatchRepository stockBatchRepository;
    private final DrugRepository drugRepository;
    private final DrugBatchRepository drugBatchRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SupplierRepository supplierRepository;

    public PurchaseService(StockService stockService,
                           PurchaseRepository purchaseRepository,
                           PurchaseItemRepository purchaseItemRepository,
                           StockBatchRepository stockBatchRepository,
                           DrugRepository drugRepository,
                           DrugBatchRepository drugBatchRepository,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           SupplierRepository supplierRepository) {
        this.stockService = stockService;
        this.purchaseRepository = purchaseRepository;
        this.purchaseItemRepository = purchaseItemRepository;
        this.stockBatchRepository = stockBatchRepository;
        this.drugRepository = drugRepository;
        this.drugBatchRepository = drugBatchRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.supplierRepository = supplierRepository;
    }

    public void addPurchase(Integer drugId, Integer quantity, Double costPrice) {
        // 增加内存缓存（保留以防其他代码仍依赖）
        stockService.increaseStock(drugId, quantity);

        double totalAmount = quantity * costPrice;

        Purchase p = new Purchase();
        p.setPurchaseNo("P-" + System.currentTimeMillis());
        p.setTotalAmount(java.math.BigDecimal.valueOf(totalAmount));
        p.setStatus((byte)1);
        p.setPurchasedAt(LocalDateTime.now());
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());
        // 设置采购人：优先使用已有用户，否则创建一个 system 用户（用于本地调试）
        User purchaser = null;
        try {
            var users = userRepository.findAll();
            if (users != null && !users.isEmpty()) purchaser = users.get(0);
            if (purchaser == null) {
                Role r = new Role();
                r.setRoleName("SYSTEM");
                r.setCreatedAt(LocalDateTime.now());
                r.setUpdatedAt(LocalDateTime.now());
                roleRepository.save(r);

                User u = new User();
                u.setUsername("system");
                // 使用 BCrypt 进行本地账号密码哈希
                org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder enc = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
                u.setPasswordHash(enc.encode("system"));
                u.setRole(r);
                u.setStatus((byte)1);
                u.setCreatedAt(LocalDateTime.now());
                u.setUpdatedAt(LocalDateTime.now());
                userRepository.save(u);
                purchaser = u;
            }
        } catch (Exception ex) {
            System.err.println("创建/查找采购人失败: " + ex.getMessage());
        }
        if (purchaser != null) p.setPurchaser(purchaser);
        // 设置供应商：优先使用已有 supplier，否则创建一个默认 supplier（本地调试用）
        Supplier supplier = null;
        try {
            var sl = supplierRepository.findAll();
            if (sl != null && !sl.isEmpty()) supplier = sl.get(0);
            if (supplier == null) {
                Supplier s = new Supplier();
                s.setName("默认供应商");
                s.setCreatedAt(LocalDateTime.now());
                s.setUpdatedAt(LocalDateTime.now());
                supplierRepository.save(s);
                supplier = s;
            }
        } catch (Exception ex) {
            System.err.println("创建/查找供应商失败: " + ex.getMessage());
        }
        if (supplier != null) p.setSupplier(supplier);
        purchaseRepository.save(p);

        PurchaseItem item = new PurchaseItem();
        item.setPurchase(p);
        Drug d = drugRepository.findById(Long.valueOf(drugId)).orElse(null);
        item.setDrug(d);
        item.setQty(quantity);
        item.setUnitPrice(java.math.BigDecimal.valueOf(costPrice));
        item.setAmount(java.math.BigDecimal.valueOf(totalAmount));
        item.setCreatedAt(LocalDateTime.now());
        purchaseItemRepository.save(item);

        // 持久化库存到 StockBatch 表：优先更新已有主仓库记录，否则尝试使用已有 DrugBatch
        try {
            Long did = d != null && d.getId() != null ? d.getId() : Long.valueOf(drugId);
            var batches = stockBatchRepository.findByDrug_Id(did);
            String mainWarehouse = "主仓库";
            medicine.example.medicine_sales_system.entity.StockBatch target = null;
            for (var b : batches) {
                if (mainWarehouse.equals(b.getWarehouse())) { target = b; break; }
            }
            if (target != null) {
                target.setQty(target.getQty() + quantity);
                target.setUpdatedAt(LocalDateTime.now());
                stockBatchRepository.save(target);
            } else {
                // 没有现成的 StockBatch，尝试取一个已有的 DrugBatch 作为批次
                var dbatches = drugBatchRepository.findByDrug_Id(did);
                if (dbatches != null && !dbatches.isEmpty()) {
                    var db = dbatches.get(0);
                    StockBatch sb = new StockBatch();
                    sb.setDrug(d);
                    sb.setBatch(db);
                    sb.setQty(quantity);
                    sb.setWarningQty(100);
                    sb.setWarehouse(mainWarehouse);
                    sb.setUpdatedAt(LocalDateTime.now());
                    stockBatchRepository.save(sb);
                } else {
                    System.err.println("未找到可用 DrugBatch，跳过持久化 StockBatch（drugId=" + did + ")");
                }
            }
        } catch (Exception ex) {
            System.err.println("持久化库存失败: " + ex.getMessage());
        }

        System.out.println(
                "进货完成：药品ID=" + drugId +
                "，数量=" + quantity +
                "，进价=" + costPrice
        );
    }

    public Map<String, Object> getPurchaseReport() {
        List<PurchaseItem> items = purchaseItemRepository.findAll();
        double totalAmount = 0.0;
        int totalQuantity = 0;
        for (PurchaseItem it : items) {
            totalAmount += it.getAmount().doubleValue();
            totalQuantity += it.getQty();
        }

        return Map.of(
            "totalAmount", totalAmount,
            "totalQuantity", totalQuantity,
            "purchaseCount", items.size()
        );
    }

    /**
     * 获取进货单列表
     */
    public Map<String, Object> getPurchaseList(java.util.Map<String, Object> params) {
        List<Purchase> purchases = purchaseRepository.findAll();
        
        // 过滤：只返回未删除的（status != 0）
        purchases = purchases.stream()
            .filter(p -> p.getStatus() != null && p.getStatus() != 0)
            .collect(java.util.stream.Collectors.toList());
        
        // 转换为前端需要的格式
        List<Map<String, Object>> orderList = new java.util.ArrayList<>();
        for (Purchase purchase : purchases) {
            // 获取该进货单的所有药品项
            List<PurchaseItem> items = purchaseItemRepository.findAll().stream()
                .filter(item -> item.getPurchase() != null && 
                               item.getPurchase().getId().equals(purchase.getId()))
                .collect(java.util.stream.Collectors.toList());
            
            // 计算总数量
            int totalQuantity = items.stream()
                .mapToInt(PurchaseItem::getQty)
                .sum();
            
            // 状态映射：1=待入库, 2=已入库, 0=已取消
            String status = "pending";
            if (purchase.getStatus() != null) {
                if (purchase.getStatus() == 2) {
                    status = "completed";
                } else if (purchase.getStatus() == 0) {
                    status = "cancelled";
                }
            }
            
            Map<String, Object> order = new java.util.HashMap<>();
            order.put("id", purchase.getId());
            order.put("orderNo", purchase.getPurchaseNo());
            order.put("supplierName", purchase.getSupplier() != null ? purchase.getSupplier().getName() : "");
            order.put("supplierContact", purchase.getSupplier() != null && purchase.getSupplier().getPhone() != null ? 
                     purchase.getSupplier().getPhone() : "");
            order.put("purchaseDate", purchase.getPurchasedAt() != null ? 
                     purchase.getPurchasedAt().toLocalDate().toString() : "");
            order.put("expectedDate", ""); // 前端字段，后端暂未存储
            order.put("handler", purchase.getPurchaser() != null ? purchase.getPurchaser().getUsername() : "");
            order.put("drugCount", items.size());
            order.put("totalQuantity", totalQuantity);
            order.put("totalAmount", purchase.getTotalAmount() != null ? purchase.getTotalAmount().doubleValue() : 0.0);
            order.put("purchaseType", ""); // 前端字段，后端暂未存储
            order.put("paymentMethod", ""); // 前端字段，后端暂未存储
            order.put("warehouse", ""); // 前端字段，后端暂未存储
            order.put("status", status);
            order.put("createdAt", purchase.getCreatedAt() != null ? purchase.getCreatedAt().toString() : "");
            
            orderList.add(order);
        }
        
        // 简单分页处理
        int page = params != null && params.get("page") != null ? 
                  Integer.parseInt(params.get("page").toString()) : 1;
        int limit = params != null && params.get("limit") != null ? 
                   Integer.parseInt(params.get("limit").toString()) : 10;
        int total = orderList.size();
        int startIndex = (page - 1) * limit;
        int endIndex = Math.min(startIndex + limit, total);
        
        List<Map<String, Object>> pagedList = startIndex < total ? 
            orderList.subList(startIndex, endIndex) : new java.util.ArrayList<>();
        
        return Map.of(
            "items", pagedList,
            "total", total,
            "page", page,
            "limit", limit
        );
    }

    public void deletePurchase(Long id) {
        purchaseRepository.findById(id).ifPresent(p -> {
            p.setStatus((byte)0);
            p.setUpdatedAt(LocalDateTime.now());
            purchaseRepository.save(p);
            System.out.println("采购单已软删除 id=" + id);
        });
    }

    @Transactional
    public Map<String, Object> confirmPurchase(Long id, PurchaseConfirmRequest req) {
        Purchase p = purchaseRepository.findById(id).orElseThrow(() -> new RuntimeException("采购单不存在"));
        List<PurchaseItem> items = purchaseItemRepository.findAll();
        items.removeIf(it -> !it.getPurchase().getId().equals(id));

        for (PurchaseItem it : items) {
            Long drugId = it.getDrug().getId();
            Long batchId = it.getBatch() != null ? it.getBatch().getId() : null;
            int qty = it.getQty();

            if (batchId != null) {
                var batches = stockBatchRepository.findByDrug_IdAndBatch_Id(drugId, batchId);
                if (batches != null && !batches.isEmpty()) {
                    StockBatch sb = batches.get(0);
                    sb.setQty(sb.getQty() + qty);
                    sb.setUpdatedAt(LocalDateTime.now());
                    stockBatchRepository.save(sb);
                } else {
                    StockBatch sb = new StockBatch();
                    Drug drug = drugRepository.findById(drugId).orElse(null);
                    DrugBatch db = drugBatchRepository.findById(batchId).orElse(null);
                    sb.setDrug(drug);
                    sb.setBatch(db);
                    sb.setQty(qty);
                    sb.setWarningQty(100);
                    sb.setWarehouse(req != null && req.getWarehouseId() != null ? ("WH-" + req.getWarehouseId()) : "主仓库");
                    sb.setUpdatedAt(LocalDateTime.now());
                    stockBatchRepository.save(sb);
                }
            } else {
                stockService.increaseStock(drugId.intValue(), qty);
            }
        }

        p.setStatus((byte)2);
        p.setUpdatedAt(LocalDateTime.now());
        purchaseRepository.save(p);

        return Map.of(
            "purchaseId", id,
            "status", "RECEIVED",
            "receivedAt", java.time.OffsetDateTime.now().toString()
        );
    }

    /**
     * 添加完整的进货单（包含多个药品项）
     */
    @Transactional
    public Purchase addPurchaseOrder(PurchaseOrderRequest request) {
        LocalDateTime now = LocalDateTime.now();
        
        // 创建进货单
        Purchase purchase = new Purchase();
        purchase.setPurchaseNo("P-" + now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        purchase.setPurchasedAt(parseDate(request.getPurchaseDate(), now));
        purchase.setCreatedAt(now);
        purchase.setUpdatedAt(now);
        purchase.setStatus((byte) 1); // 1 = 待入库
        
        // 设置供应商
        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId().longValue())
                .orElseThrow(() -> new RuntimeException("供应商不存在: " + request.getSupplierId()));
            purchase.setSupplier(supplier);
        } else {
            // 如果没有提供供应商，使用默认供应商
            Supplier defaultSupplier = supplierRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    Supplier s = new Supplier();
                    s.setName("默认供应商");
                    s.setCreatedAt(now);
                    s.setUpdatedAt(now);
                    return supplierRepository.save(s);
                });
            purchase.setSupplier(defaultSupplier);
        }
        
        // 设置采购人（根据 handler 查找用户，或使用默认用户）
        User purchaser = null;
        if (request.getHandler() != null && !request.getHandler().trim().isEmpty()) {
            purchaser = userRepository.findByUsername(request.getHandler().trim())
                .orElse(null);
        }
        if (purchaser == null) {
            purchaser = userRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setRoleName("SYSTEM");
                    r.setCreatedAt(now);
                    r.setUpdatedAt(now);
                    roleRepository.save(r);
                    
                    User u = new User();
                    u.setUsername("system");
                    org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder enc = 
                        new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
                    u.setPasswordHash(enc.encode("system"));
                    u.setRole(r);
                    u.setStatus((byte) 1);
                    u.setCreatedAt(now);
                    u.setUpdatedAt(now);
                    return userRepository.save(u);
                });
        }
        purchase.setPurchaser(purchaser);
        
        // 先设置一个临时的总金额，以便先保存 Purchase 对象
        purchase.setTotalAmount(BigDecimal.ZERO);
        
        // 先保存 Purchase 对象，使其获得 ID（持久化状态）
        purchase = purchaseRepository.save(purchase);
        
        // 计算总金额（先初始化为0，后面会累加药品项金额）
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // 处理每个药品项
        String warehouse = request.getWarehouse() != null ? request.getWarehouse() : "主仓库";
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (PurchaseOrderRequest.PurchaseItemRequest itemRequest : request.getItems()) {
            Drug drug = drugRepository.findById(itemRequest.getDrugId().longValue())
                .orElseThrow(() -> new RuntimeException("药品不存在: " + itemRequest.getDrugId()));
            
            // 计算单项金额
            BigDecimal unitPrice = itemRequest.getUnitPrice() != null ? 
                itemRequest.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal itemAmount = unitPrice.multiply(new BigDecimal(itemRequest.getQuantity()));
            
            // 处理税率（如果有）
            if (itemRequest.getTaxRate() != null && itemRequest.getTaxRate().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal taxMultiplier = BigDecimal.ONE.add(
                    itemRequest.getTaxRate().divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP)
                );
                itemAmount = itemAmount.multiply(taxMultiplier);
            }
            totalAmount = totalAmount.add(itemAmount);
            
            // 创建或查找批次
            DrugBatch batch = null;
            if (itemRequest.getBatchNo() != null && !itemRequest.getBatchNo().trim().isEmpty()) {
                // 查找是否已存在该批次
                var existingBatches = drugBatchRepository.findByDrug_IdAndBatchNo(
                    drug.getId(), 
                    itemRequest.getBatchNo().trim()
                );
                if (!existingBatches.isEmpty()) {
                    batch = existingBatches.get(0);
                } else {
                    // 创建新批次
                    batch = new DrugBatch();
                    batch.setDrug(drug);
                    batch.setBatchNo(itemRequest.getBatchNo().trim());
                    if (itemRequest.getProductionDate() != null && !itemRequest.getProductionDate().trim().isEmpty()) {
                        batch.setProductionDate(LocalDate.parse(itemRequest.getProductionDate(), dateFormatter));
                    }
                    if (itemRequest.getExpiryDate() != null && !itemRequest.getExpiryDate().trim().isEmpty()) {
                        batch.setExpireDate(LocalDate.parse(itemRequest.getExpiryDate(), dateFormatter));
                    } else {
                        throw new RuntimeException("有效期不能为空");
                    }
                    batch.setPurchasePrice(itemRequest.getUnitPrice());
                    batch.setCreatedAt(now);
                    batch.setUpdatedAt(now);
                    batch = drugBatchRepository.save(batch);
                }
            } else {
                throw new RuntimeException("批次号不能为空");
            }
            
            // 创建进货项（此时 purchase 已经是持久化状态）
            PurchaseItem purchaseItem = new PurchaseItem();
            purchaseItem.setPurchase(purchase);
            purchaseItem.setDrug(drug);
            purchaseItem.setBatch(batch);
            purchaseItem.setQty(itemRequest.getQuantity());
            purchaseItem.setUnitPrice(itemRequest.getUnitPrice());
            purchaseItem.setAmount(itemAmount);
            purchaseItem.setCreatedAt(now);
            purchaseItemRepository.save(purchaseItem);
            
            // 更新库存（创建或更新 StockBatch）
            var stockBatches = stockBatchRepository.findByDrug_IdAndBatch_Id(drug.getId(), batch.getId());
            StockBatch stockBatch = null;
            for (var sb : stockBatches) {
                if (warehouse.equals(sb.getWarehouse())) {
                    stockBatch = sb;
                    break;
                }
            }
            
            if (stockBatch != null) {
                // 更新现有库存
                stockBatch.setQty(stockBatch.getQty() + itemRequest.getQuantity());
                stockBatch.setUpdatedAt(now);
            } else {
                // 创建新库存批次
                stockBatch = new StockBatch();
                stockBatch.setDrug(drug);
                stockBatch.setBatch(batch);
                stockBatch.setQty(itemRequest.getQuantity());
                stockBatch.setWarningQty(100);
                stockBatch.setWarehouse(warehouse);
                stockBatch.setUpdatedAt(now);
            }
            stockBatchRepository.save(stockBatch);
        }
        
        // 加上运费和其他费用，减去折扣
        if (request.getFreight() != null) totalAmount = totalAmount.add(request.getFreight());
        if (request.getOtherCharges() != null) totalAmount = totalAmount.add(request.getOtherCharges());
        if (request.getDiscount() != null) totalAmount = totalAmount.subtract(request.getDiscount());
        
        // 更新 Purchase 的总金额
        purchase.setTotalAmount(totalAmount);
        return purchaseRepository.save(purchase);
    }
    
    private LocalDateTime parseDate(String dateStr, LocalDateTime defaultDate) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return defaultDate;
        }
        try {
            LocalDate date = LocalDate.parse(dateStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return date.atStartOfDay();
        } catch (Exception e) {
            return defaultDate;
        }
    }

}
