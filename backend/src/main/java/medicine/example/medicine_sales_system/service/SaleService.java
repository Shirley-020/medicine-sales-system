package medicine.example.medicine_sales_system.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import medicine.example.medicine_sales_system.dto.SaleAddRequest;
import medicine.example.medicine_sales_system.dto.FinanceReportResponse;
import medicine.example.medicine_sales_system.dto.SaleListDto;
import medicine.example.medicine_sales_system.dto.SaleDetailDto;
import medicine.example.medicine_sales_system.entity.Customer;
import medicine.example.medicine_sales_system.entity.Drug;
import medicine.example.medicine_sales_system.entity.Sale;
import medicine.example.medicine_sales_system.entity.SaleItem;
import medicine.example.medicine_sales_system.entity.StockBatch;
import medicine.example.medicine_sales_system.repository.CustomerRepository;
import medicine.example.medicine_sales_system.repository.DrugRepository;
import medicine.example.medicine_sales_system.repository.SaleItemRepository;
import medicine.example.medicine_sales_system.repository.SaleRepository;
import medicine.example.medicine_sales_system.repository.StockBatchRepository;
import medicine.example.medicine_sales_system.repository.UserRepository;
import medicine.example.medicine_sales_system.entity.User;
import java.time.LocalDate;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final StockBatchRepository stockBatchRepository;
    private final DrugRepository drugRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public SaleService(SaleRepository saleRepository,
                       SaleItemRepository saleItemRepository,
                       StockBatchRepository stockBatchRepository,
                       DrugRepository drugRepository,
                       CustomerRepository customerRepository,
                       UserRepository userRepository) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.stockBatchRepository = stockBatchRepository;
        this.drugRepository = drugRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Sale addSale(SaleAddRequest request) {
        Sale sale = new Sale();
        
        // 设置客户（如果为 null，使用默认客户或创建散客记录）
        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId().longValue())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.getCustomerId()));
            sale.setCustomer(customer);
        } else {
            // 如果没有客户ID，尝试查找或创建一个默认的"散客"客户
            Customer guestCustomer = customerRepository.findAll().stream()
                .filter(c -> "散客".equals(c.getName()) || "Guest".equals(c.getName()))
                .findFirst()
                .orElseGet(() -> {
                    // 如果没有找到散客，创建一个
                    Customer newGuest = new Customer();
                    newGuest.setName("散客");
                    newGuest.setPhone("00000000000");
                    newGuest.setAddress("散客");
                    LocalDateTime now = LocalDateTime.now();
                    newGuest.setCreatedAt(now);
                    newGuest.setUpdatedAt(now);
                    return customerRepository.save(newGuest);
                });
            sale.setCustomer(guestCustomer);
        }

        // 设置销售员（根据前端传入的销售员姓名查找用户）
        User seller;
        if (request.getSalesman() != null && !request.getSalesman().trim().isEmpty()) {
            seller = userRepository.findByUsername(request.getSalesman().trim())
                .orElseThrow(() -> new RuntimeException("销售员 '" + request.getSalesman() + "' 不存在，请检查用户名是否正确"));
        } else {
            // 如果没有提供销售员，使用第一个可用的用户作为默认销售员
            seller = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No user found. Please create at least one user first."));
        }
        sale.setSeller(seller);

        // 生成销售单号
        LocalDateTime now = LocalDateTime.now();
        String saleNo = "SALE-" + now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        sale.setSaleNo(saleNo);

        sale.setSoldAt(now);
        sale.setCreatedAt(now);
        sale.setUpdatedAt(now);
        sale.setStatus((byte) 1); // 1 for 'completed'
        sale.setTotalAmount(BigDecimal.ZERO);

        Sale savedSale = saleRepository.save(sale);
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SaleAddRequest.SaleItemRequest itemRequest : request.getItems()) {
            Drug drug = drugRepository.findById(itemRequest.getDrugId().longValue())
                .orElseThrow(() -> new RuntimeException("Drug not found with id: " + itemRequest.getDrugId()));

            int remainingQtyToSell = itemRequest.getQuantity();
            
            // Find stock batches for the drug, ordered by expiry date (FIFO)
            List<StockBatch> stockBatches = stockBatchRepository.findByDrug_IdAndQtyGreaterThanOrderByBatch_ExpireDateAsc(drug.getId(), 0);

            int availableStock = stockBatches.stream().mapToInt(StockBatch::getQty).sum();
            if (availableStock < remainingQtyToSell) {
                throw new RuntimeException("Insufficient stock for drug: " + drug.getName() + ". Available: " + availableStock + ", Requested: " + remainingQtyToSell);
            }

            for (StockBatch stockBatch : stockBatches) {
                if (remainingQtyToSell <= 0) break;

                int qtyToTake = Math.min(remainingQtyToSell, stockBatch.getQty());

                SaleItem saleItem = new SaleItem();
                saleItem.setSale(savedSale);
                saleItem.setDrug(drug);
                saleItem.setBatch(stockBatch.getBatch());
                saleItem.setQty(qtyToTake);
                saleItem.setUnitPrice(itemRequest.getSalePrice());
                // 计算金额：单价 * 数量
                BigDecimal itemAmount = itemRequest.getSalePrice().multiply(new BigDecimal(qtyToTake));
                saleItem.setAmount(itemAmount);
                saleItem.setCreatedAt(LocalDateTime.now());
                saleItem.setUpdatedAt(LocalDateTime.now());
                saleItemRepository.save(saleItem);

                stockBatch.setQty(stockBatch.getQty() - qtyToTake);
                stockBatchRepository.save(stockBatch);

                totalAmount = totalAmount.add(itemRequest.getSalePrice().multiply(new BigDecimal(qtyToTake)));
                remainingQtyToSell -= qtyToTake;
            }
        }

        savedSale.setTotalAmount(totalAmount);
        return saleRepository.save(savedSale);
    }

    @Transactional
    public void cancelSale(Long saleId) {
        Sale sale = saleRepository.findById(saleId).orElseThrow(() -> new RuntimeException("Sale not found: " + saleId));
        if (sale.getStatus() == 0) {
            throw new RuntimeException("Sale is already cancelled.");
        }

        List<SaleItem> items = saleItemRepository.findBySale_Id(saleId);
        for (SaleItem item : items) {
            List<StockBatch> stockBatches = stockBatchRepository.findByDrug_IdAndBatch_Id(item.getDrug().getId(), item.getBatch().getId());
            if (!stockBatches.isEmpty()) {
                StockBatch stockBatch = stockBatches.get(0); // Assuming one stock record per drug/batch combination
                stockBatch.setQty(stockBatch.getQty() + item.getQty());
                stockBatchRepository.save(stockBatch);
            } else {
                // This case should ideally not happen if data is consistent
                throw new IllegalStateException("Stock batch not found for sold item, cannot return stock.");
            }
        }

        sale.setStatus((byte) 0); // 0 for 'cancelled'
        sale.setUpdatedAt(LocalDateTime.now());
        saleRepository.save(sale);
    }

    // Other methods like returnSale, getSaleReport etc. would also need to be refactored
    // to use the database instead of in-memory lists.
    // For now, they are left as is to focus on the addSale fix.

    @Transactional
    public void returnSale(Long saleId, List<medicine.example.medicine_sales_system.dto.SaleReturnRequest.ReturnItem> items) {
        // 查找销售单
        Sale sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new RuntimeException("销售单不存在: " + saleId));
        
        // 检查销售单状态
        if (sale.getStatus() == 0) {
            throw new RuntimeException("已取消的销售单不能退货");
        }
        
        // 计算总退货数量
        int totalReturnQuantity = 0;
        int totalOriginalQuantity = 0;
        
        // 处理每个退货项
        for (medicine.example.medicine_sales_system.dto.SaleReturnRequest.ReturnItem item : items) {
            Long saleItemId = item.getSaleItemId() != null ? item.getSaleItemId().longValue() : null;
            if (saleItemId == null) {
                throw new RuntimeException("销售项ID不能为空");
            }
            
            SaleItem saleItem = saleItemRepository.findById(saleItemId)
                .orElseThrow(() -> new RuntimeException("销售项不存在: " + saleItemId));
            
            if (!saleItem.getSale().getId().equals(saleId)) {
                throw new RuntimeException("销售项不属于该销售单");
            }
            
            int returnQty = item.getQuantity() != null ? item.getQuantity() : 0;
            totalReturnQuantity += returnQty;
            totalOriginalQuantity += saleItem.getQty() != null ? saleItem.getQty() : 0;
            
            // 恢复库存：根据销售项中的批次信息恢复库存
            if (returnQty > 0 && saleItem.getBatch() != null) {
                List<StockBatch> stockBatches = stockBatchRepository.findByDrug_IdAndBatch_Id(
                    saleItem.getDrug().getId(), 
                    saleItem.getBatch().getId()
                );
                
                if (!stockBatches.isEmpty()) {
                    // 找到对应的库存批次，恢复库存
                    StockBatch stockBatch = stockBatches.get(0);
                    stockBatch.setQty(stockBatch.getQty() + returnQty);
                    stockBatch.setUpdatedAt(LocalDateTime.now());
                    stockBatchRepository.save(stockBatch);
                } else {
                    // 如果找不到对应的库存批次，创建一个新的库存批次
                    StockBatch newStockBatch = new StockBatch();
                    newStockBatch.setDrug(saleItem.getDrug());
                    newStockBatch.setBatch(saleItem.getBatch());
                    newStockBatch.setQty(returnQty);
                    newStockBatch.setWarningQty(100);
                    newStockBatch.setWarehouse("主仓库");
                    newStockBatch.setUpdatedAt(LocalDateTime.now());
                    stockBatchRepository.save(newStockBatch);
                }
            }
        }
        
        // 更新销售单状态
        if (totalReturnQuantity >= totalOriginalQuantity) {
            // 全部退货
            sale.setStatus((byte) 2); // 2 = 已退货
        } else if (totalReturnQuantity > 0) {
            // 部分退货
            sale.setStatus((byte) 3); // 3 = 部分退货
        }
        
        sale.setUpdatedAt(LocalDateTime.now());
        saleRepository.save(sale);
    }

    /**
     * 获取销售单列表
     * 支持按日期范围、客户、状态等条件查询
     */
    public List<SaleListDto> getSaleList(String orderNo, Integer customerId, 
                                         LocalDateTime startDate, LocalDateTime endDate,
                                         Byte status) {
        List<Sale> sales = saleRepository.findAll();
        
        // 应用筛选条件
        java.util.stream.Stream<Sale> stream = sales.stream();
        
        if (orderNo != null && !orderNo.trim().isEmpty()) {
            stream = stream.filter(sale -> sale.getSaleNo() != null && 
                sale.getSaleNo().contains(orderNo));
        }
        
        if (customerId != null) {
            stream = stream.filter(sale -> sale.getCustomer() != null && 
                sale.getCustomer().getId().equals(customerId.longValue()));
        }
        
        if (startDate != null) {
            stream = stream.filter(sale -> sale.getSoldAt() != null && 
                !sale.getSoldAt().isBefore(startDate));
        }
        
        if (endDate != null) {
            stream = stream.filter(sale -> sale.getSoldAt() != null && 
                !sale.getSoldAt().isAfter(endDate));
        }
        
        if (status != null) {
            stream = stream.filter(sale -> sale.getStatus() != null && 
                sale.getStatus().equals(status));
        }
        
        // 转换为 DTO
        return stream.map(sale -> {
            SaleListDto dto = new SaleListDto();
            dto.setId(sale.getId());
            dto.setOrderNo(sale.getSaleNo());
            
            if (sale.getCustomer() != null) {
                dto.setCustomerName(sale.getCustomer().getName());
                // 使用 creditCode 作为会员号（如果没有则使用 phone）
                dto.setMemberId(sale.getCustomer().getCreditCode() != null ? 
                    sale.getCustomer().getCreditCode() : sale.getCustomer().getPhone());
            } else {
                dto.setCustomerName("散客");
                dto.setMemberId(null);
            }
            
            dto.setSaleDate(sale.getSoldAt());
            
            // 转换状态：1=已完成, 0=已取消, 2=已退货, 3=部分退货
            if (sale.getStatus() != null) {
                if (sale.getStatus() == 1) {
                    dto.setStatus("completed");
                } else if (sale.getStatus() == 0) {
                    dto.setStatus("cancelled");
                } else if (sale.getStatus() == 2) {
                    dto.setStatus("returned");
                } else if (sale.getStatus() == 3) {
                    dto.setStatus("partial_return");
                } else {
                    dto.setStatus("unknown");
                }
            } else {
                dto.setStatus("unknown");
            }
            
            // 查询销售项
            List<SaleItem> items = saleItemRepository.findBySale_Id(sale.getId());
            
            // 计算药品品种数（去重）
            long uniqueDrugCount = items.stream()
                .map(item -> item.getDrug().getId())
                .distinct()
                .count();
            dto.setDrugCount((int) uniqueDrugCount);
            
            // 计算总数量
            int totalQty = items.stream()
                .mapToInt(SaleItem::getQty)
                .sum();
            dto.setTotalQuantity(totalQty);
            
            // 计算金额
            BigDecimal totalAmount = sale.getTotalAmount() != null ? sale.getTotalAmount() : BigDecimal.ZERO;
            dto.setTotalAmount(totalAmount);
            dto.setActualAmount(totalAmount); // 实收金额 = 总金额（假设没有折扣）
            dto.setOriginalAmount(totalAmount); // 原价总额 = 总金额（假设没有折扣）
            dto.setDiscountAmount(BigDecimal.ZERO); // 折扣金额 = 0（假设没有折扣）
            
            if (sale.getSeller() != null) {
                dto.setSalesman(sale.getSeller().getUsername());
            } else {
                dto.setSalesman("未知");
            }
            
            // paymentMethod 在 Sale 实体中可能不存在，设为默认值
            dto.setPaymentMethod("cash"); // 默认现金
            
            // 销售类型（可以根据业务逻辑设置）
            dto.setSaleType("retail"); // 默认零售
            
            // 创建时间
            dto.setCreatedAt(sale.getCreatedAt());
            
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取销售单详情（包含销售项列表）
     */
    public medicine.example.medicine_sales_system.dto.SaleDetailDto getSaleDetail(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new RuntimeException("销售单不存在: " + saleId));
        
        SaleDetailDto dto = new SaleDetailDto();
        dto.setId(sale.getId());
        dto.setOrderNo(sale.getSaleNo());
        
        if (sale.getCustomer() != null) {
            dto.setCustomerName(sale.getCustomer().getName());
            dto.setMemberId(sale.getCustomer().getCreditCode() != null ? 
                sale.getCustomer().getCreditCode() : sale.getCustomer().getPhone());
        } else {
            dto.setCustomerName("散客");
            dto.setMemberId(null);
        }
        
        dto.setSaleDate(sale.getSoldAt());
        
        // 转换状态
        if (sale.getStatus() != null) {
            if (sale.getStatus() == 1) {
                dto.setStatus("completed");
            } else if (sale.getStatus() == 0) {
                dto.setStatus("cancelled");
            } else if (sale.getStatus() == 2) {
                dto.setStatus("returned");
            } else if (sale.getStatus() == 3) {
                dto.setStatus("partial_return");
            } else {
                dto.setStatus("unknown");
            }
        } else {
            dto.setStatus("unknown");
        }
        
        // 查询销售项
        List<SaleItem> items = saleItemRepository.findBySale_Id(sale.getId());
        
        // 转换为 DTO
        List<SaleDetailDto.SaleItemDto> itemDtos = items.stream().map(item -> {
            SaleDetailDto.SaleItemDto itemDto = new SaleDetailDto.SaleItemDto();
            itemDto.setId(item.getId());
            itemDto.setDrugId(item.getDrug().getId());
            itemDto.setDrugName(item.getDrug().getName());
            itemDto.setSpecification(item.getDrug().getSpec());
            itemDto.setManufacturer(item.getDrug().getManufacturer());
            if (item.getBatch() != null) {
                itemDto.setBatchNo(item.getBatch().getBatchNo());
            }
            itemDto.setQuantity(item.getQty());
            itemDto.setSalePrice(item.getUnitPrice());
            itemDto.setAmount(item.getAmount());
            itemDto.setDiscount(BigDecimal.ZERO); // 默认无折扣
            return itemDto;
        }).collect(java.util.stream.Collectors.toList());
        
        dto.setItems(itemDtos);
        
        // 计算汇总信息
        long uniqueDrugCount = items.stream()
            .map(item -> item.getDrug().getId())
            .distinct()
            .count();
        dto.setDrugCount((int) uniqueDrugCount);
        
        int totalQty = items.stream()
            .mapToInt(SaleItem::getQty)
            .sum();
        dto.setTotalQuantity(totalQty);
        
        BigDecimal totalAmount = sale.getTotalAmount() != null ? sale.getTotalAmount() : BigDecimal.ZERO;
        dto.setTotalAmount(totalAmount);
        dto.setActualAmount(totalAmount);
        dto.setOriginalAmount(totalAmount);
        dto.setDiscountAmount(BigDecimal.ZERO);
        
        if (sale.getSeller() != null) {
            dto.setSalesman(sale.getSeller().getUsername());
        } else {
            dto.setSalesman("未知");
        }
        
        dto.setPaymentMethod("cash");
        dto.setSaleType("retail");
        dto.setCreatedAt(sale.getCreatedAt());
        
        return dto;
    }

    public Map<String, Object> getSaleReport() {
        throw new UnsupportedOperationException("In-memory getSaleReport is deprecated. Please use getSaleList instead.");
    }

    public Map<String, Object> getReturnReport() {
        throw new UnsupportedOperationException("In-memory getReturnReport is deprecated. Please implement database version.");
    }

    /**
     * 获取今日财务报告
     * 统计今天的销售总额和销售数量
     */
    public FinanceReportResponse getTodayFinanceReport() {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(23, 59, 59);
            
            // 查询今天的销售记录（状态为已完成，即 status = 1）
            List<Sale> todaySales = saleRepository.findBySoldAtBetween(startOfDay, endOfDay)
                .stream()
                .filter(sale -> sale.getStatus() != null && sale.getStatus() == 1) // 只统计已完成的销售
                .collect(java.util.stream.Collectors.toList());
            
            // 计算总销售额
            BigDecimal totalAmount = todaySales.stream()
                .map(Sale::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 计算销售数量
            int salesCount = todaySales.size();
            
            return new FinanceReportResponse(
                totalAmount.doubleValue(),
                salesCount
            );
        } catch (Exception e) {
            // 如果查询失败，返回空数据而不是抛出异常
            return new FinanceReportResponse(0.0, 0);
        }
    }
}
