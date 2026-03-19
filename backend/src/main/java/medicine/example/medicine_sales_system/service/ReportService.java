package medicine.example.medicine_sales_system.service;

import medicine.example.medicine_sales_system.dto.LowStockDto;
import medicine.example.medicine_sales_system.dto.SalesSummaryDto;
import medicine.example.medicine_sales_system.dto.DailySalesReportDto;
import medicine.example.medicine_sales_system.repository.SaleItemRepository;
import medicine.example.medicine_sales_system.repository.StockBatchRepository;
import medicine.example.medicine_sales_system.repository.SaleRepository;
import medicine.example.medicine_sales_system.repository.SalesSummary;
import medicine.example.medicine_sales_system.entity.StockBatch;
import medicine.example.medicine_sales_system.entity.Sale;
import medicine.example.medicine_sales_system.entity.SaleItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final StockBatchRepository stockBatchRepository;
    private final SaleItemRepository saleItemRepository;
    private final SaleRepository saleRepository;

    public ReportService(StockBatchRepository stockBatchRepository, SaleItemRepository saleItemRepository, SaleRepository saleRepository) {
        this.stockBatchRepository = stockBatchRepository;
        this.saleItemRepository = saleItemRepository;
        this.saleRepository = saleRepository;
    }

    public List<LowStockDto> getLowStockWarnings() {
        java.util.List<LowStockDto> result = new java.util.ArrayList<>();
        
        // 1. 获取库存不足的批次
        List<StockBatch> lowStock = stockBatchRepository.findLowStockBatches();
        for (StockBatch s : lowStock) {
            result.add(new LowStockDto(
                    s.getDrug().getId(),
                    s.getDrug().getCode(),
                    s.getDrug().getName(),
                    s.getBatch().getBatchNo(),
                    s.getQty(),
                    s.getWarningQty(),
                    s.getWarehouse(),
                    s.getBatch().getExpireDate(),
                    "stock_low"
            ));
        }
        
        // 2. 获取即将过期的批次（90天内过期，且不在低库存列表中）
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate futureDate = today.plusDays(90);
        List<StockBatch> expiringSoon = stockBatchRepository.findExpiringSoonBatches(today, futureDate);
        for (StockBatch s : expiringSoon) {
            // 避免重复：如果已经在低库存列表中，跳过
            boolean alreadyInList = result.stream().anyMatch(dto -> 
                dto.getDrugId().equals(s.getDrug().getId()) && 
                dto.getBatchNo().equals(s.getBatch().getBatchNo())
            );
            if (!alreadyInList) {
                result.add(new LowStockDto(
                        s.getDrug().getId(),
                        s.getDrug().getCode(),
                        s.getDrug().getName(),
                        s.getBatch().getBatchNo(),
                        s.getQty(),
                        s.getWarningQty(),
                        s.getWarehouse(),
                        s.getBatch().getExpireDate(),
                        "expiring"
                ));
            }
        }
        
        // 3. 获取已过期的批次（且不在前面的列表中）
        List<StockBatch> expired = stockBatchRepository.findExpiredBatches(today);
        for (StockBatch s : expired) {
            // 避免重复
            boolean alreadyInList = result.stream().anyMatch(dto -> 
                dto.getDrugId().equals(s.getDrug().getId()) && 
                dto.getBatchNo().equals(s.getBatch().getBatchNo())
            );
            if (!alreadyInList) {
                result.add(new LowStockDto(
                        s.getDrug().getId(),
                        s.getDrug().getCode(),
                        s.getDrug().getName(),
                        s.getBatch().getBatchNo(),
                        s.getQty(),
                        s.getWarningQty(),
                        s.getWarehouse(),
                        s.getBatch().getExpireDate(),
                        "expired"
                ));
            }
        }
        
        return result;
    }

    public List<SalesSummaryDto> getSalesSummary(LocalDateTime start, LocalDateTime end) {
        List<SalesSummary> raw = saleItemRepository.findSalesSummaryBySoldAtBetween(start, end);
        return raw.stream().map(r -> new SalesSummaryDto(r.getDrugId(), r.getDrugName(), r.getTotalQty(), r.getTotalAmount()))
                .collect(Collectors.toList());
    }

    /**
     * 获取当日销售报表
     */
    public DailySalesReportDto getDailySalesReport(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        
        // 获取当日的所有已完成销售
        List<Sale> todaySales = saleRepository.findBySoldAtBetween(startOfDay, endOfDay)
                .stream()
                .filter(sale -> sale.getStatus() != null && sale.getStatus() == 1) // 只统计已完成的销售
                .collect(Collectors.toList());
        
        // 获取昨日的销售数据用于对比
        LocalDate yesterday = date.minusDays(1);
        LocalDateTime yesterdayStart = yesterday.atStartOfDay();
        LocalDateTime yesterdayEnd = yesterday.atTime(23, 59, 59);
        List<Sale> yesterdaySales = saleRepository.findBySoldAtBetween(yesterdayStart, yesterdayEnd)
                .stream()
                .filter(sale -> sale.getStatus() != null && sale.getStatus() == 1)
                .collect(Collectors.toList());
        
        // 计算统计数据
        Map<String, Object> stats = calculateStats(todaySales, yesterdaySales);
        
        // 获取热销药品TOP 10
        List<DailySalesReportDto.HotProductDto> hotProducts = getHotProducts(startOfDay, endOfDay, yesterdayStart, yesterdayEnd);
        
        // 获取时段销售数据（按小时）
        List<DailySalesReportDto.TimeSalesDto> timeSeries = getTimeSeries(startOfDay, endOfDay, todaySales);
        
        // 获取渠道分布（这里简化为按销售员分组）
        List<DailySalesReportDto.ChannelDistributionDto> channels = getChannelDistribution(todaySales);
        
        return new DailySalesReportDto(stats, hotProducts, timeSeries, channels);
    }
    
    private Map<String, Object> calculateStats(List<Sale> todaySales, List<Sale> yesterdaySales) {
        Map<String, Object> stats = new HashMap<>();
        
        // 当日数据
        BigDecimal todayTotalSales = todaySales.stream()
                .map(Sale::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int todayOrderCount = todaySales.size();
        long todayCustomerCount = todaySales.stream()
                .map(sale -> sale.getCustomer() != null ? sale.getCustomer().getId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        BigDecimal todayAvgOrderValue = todayOrderCount > 0 ? 
                todayTotalSales.divide(BigDecimal.valueOf(todayOrderCount), 2, java.math.RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;
        
        // 昨日数据
        BigDecimal yesterdayTotalSales = yesterdaySales.stream()
                .map(Sale::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int yesterdayOrderCount = yesterdaySales.size();
        long yesterdayCustomerCount = yesterdaySales.stream()
                .map(sale -> sale.getCustomer() != null ? sale.getCustomer().getId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        BigDecimal yesterdayAvgOrderValue = yesterdayOrderCount > 0 ? 
                yesterdayTotalSales.divide(BigDecimal.valueOf(yesterdayOrderCount), 2, java.math.RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;
        
        // 计算增长率
        double salesGrowth = yesterdayTotalSales.compareTo(BigDecimal.ZERO) > 0 ?
                todayTotalSales.subtract(yesterdayTotalSales).divide(yesterdayTotalSales, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue() : 0.0;
        double orderGrowth = yesterdayOrderCount > 0 ?
                ((double)(todayOrderCount - yesterdayOrderCount) / yesterdayOrderCount) * 100 : 0.0;
        double customerGrowth = yesterdayCustomerCount > 0 ?
                ((double)(todayCustomerCount - yesterdayCustomerCount) / yesterdayCustomerCount) * 100 : 0.0;
        double avgOrderGrowth = yesterdayAvgOrderValue.compareTo(BigDecimal.ZERO) > 0 ?
                todayAvgOrderValue.subtract(yesterdayAvgOrderValue).divide(yesterdayAvgOrderValue, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue() : 0.0;
        
        stats.put("totalSales", todayTotalSales);
        stats.put("salesGrowth", salesGrowth);
        stats.put("orderCount", todayOrderCount);
        stats.put("orderGrowth", orderGrowth);
        stats.put("customerCount", (int)todayCustomerCount);
        stats.put("customerGrowth", customerGrowth);
        stats.put("avgOrderValue", todayAvgOrderValue);
        stats.put("avgOrderGrowth", avgOrderGrowth);
        
        return stats;
    }
    
    private List<DailySalesReportDto.HotProductDto> getHotProducts(LocalDateTime todayStart, LocalDateTime todayEnd,
                                                                     LocalDateTime yesterdayStart, LocalDateTime yesterdayEnd) {
        // 获取今日销售汇总
        List<SalesSummary> todaySummary = saleItemRepository.findSalesSummaryBySoldAtBetween(todayStart, todayEnd);
        // 获取昨日销售汇总
        List<SalesSummary> yesterdaySummary = saleItemRepository.findSalesSummaryBySoldAtBetween(yesterdayStart, yesterdayEnd);
        
        // 创建昨日销售Map用于计算增长率
        Map<Long, BigDecimal> yesterdaySalesMap = yesterdaySummary.stream()
                .collect(Collectors.toMap(
                        SalesSummary::getDrugId,
                        SalesSummary::getTotalAmount,
                        BigDecimal::add
                ));
        
        // 获取今日销售项，用于获取药品规格信息
        List<SaleItem> todayItems = saleItemRepository.findAll().stream()
                .filter(item -> {
                    Sale sale = item.getSale();
                    return sale != null && 
                           sale.getStatus() != null && sale.getStatus() == 1 &&
                           !sale.getSoldAt().isBefore(todayStart) && 
                           !sale.getSoldAt().isAfter(todayEnd);
                })
                .collect(Collectors.toList());
        
        // 创建药品ID到规格的映射
        Map<Long, String> drugSpecMap = todayItems.stream()
                .collect(Collectors.toMap(
                        item -> item.getDrug().getId(),
                        item -> item.getDrug().getSpec() != null ? item.getDrug().getSpec() : "",
                        (existing, replacement) -> existing // 如果有重复，保留第一个
                ));
        
        // 转换为HotProductDto并排序
        List<DailySalesReportDto.HotProductDto> hotProducts = todaySummary.stream()
                .map(summary -> {
                    BigDecimal yesterdayAmount = yesterdaySalesMap.getOrDefault(summary.getDrugId(), BigDecimal.ZERO);
                    double growth = yesterdayAmount.compareTo(BigDecimal.ZERO) > 0 ?
                            summary.getTotalAmount().subtract(yesterdayAmount)
                                    .divide(yesterdayAmount, 4, java.math.RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).doubleValue() : 0.0;
                    
                    return new DailySalesReportDto.HotProductDto(
                            null, // rank会在后面设置
                            summary.getDrugName(),
                            drugSpecMap.getOrDefault(summary.getDrugId(), ""),
                            "西药", // category简化
                            summary.getTotalQty().intValue(),
                            summary.getTotalAmount(),
                            growth
                    );
                })
                .sorted((a, b) -> b.getSalesAmount().compareTo(a.getSalesAmount()))
                .limit(10)
                .collect(Collectors.toList());
        
        // 设置排名
        for (int i = 0; i < hotProducts.size(); i++) {
            hotProducts.get(i).setRank(i + 1);
        }
        
        return hotProducts;
    }
    
    private List<DailySalesReportDto.TimeSalesDto> getTimeSeries(LocalDateTime startOfDay, LocalDateTime endOfDay, List<Sale> sales) {
        List<DailySalesReportDto.TimeSalesDto> timeSeries = new ArrayList<>();
        
        // 按小时分组统计
        Map<Integer, List<Sale>> salesByHour = sales.stream()
                .collect(Collectors.groupingBy(sale -> sale.getSoldAt().getHour()));
        
        // 存储前一时段的销售额，用于计算趋势
        BigDecimal previousHourAmount = BigDecimal.ZERO;
        
        for (int hour = 0; hour < 24; hour++) {
            List<Sale> hourSales = salesByHour.getOrDefault(hour, Collections.emptyList());
            BigDecimal hourAmount = hourSales.stream()
                    .map(Sale::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            int orderCount = hourSales.size();
            long customerCount = hourSales.stream()
                    .map(sale -> sale.getCustomer() != null ? sale.getCustomer().getId() : null)
                    .filter(Objects::nonNull)
                    .distinct()
                    .count();
            BigDecimal avgOrderValue = orderCount > 0 ?
                    hourAmount.divide(BigDecimal.valueOf(orderCount), 2, java.math.RoundingMode.HALF_UP) :
                    BigDecimal.ZERO;
            
            // 计算趋势百分比（相对于前一时段）
            double trendPercentage = 0.0;
            if (previousHourAmount.compareTo(BigDecimal.ZERO) > 0) {
                // 有前一时段数据，计算增长率
                trendPercentage = hourAmount.subtract(previousHourAmount)
                        .divide(previousHourAmount, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
            } else if (hourAmount.compareTo(BigDecimal.ZERO) > 0 && hour > 0) {
                // 前一时段为0，当前时段有数据，显示100%增长
                trendPercentage = 100.0;
            } else if (previousHourAmount.compareTo(BigDecimal.ZERO) > 0 && hourAmount.compareTo(BigDecimal.ZERO) == 0) {
                // 前一时段有数据，当前时段为0，显示-100%下降
                trendPercentage = -100.0;
            }
            
            DailySalesReportDto.TimeSalesDto dto = new DailySalesReportDto.TimeSalesDto();
            dto.setTime(String.format("%02d:00", hour));
            dto.setTimeRange(String.format("%02d:00-%02d:59", hour, hour));
            dto.setOrderCount(orderCount);
            dto.setCustomerCount((int)customerCount);
            dto.setSalesAmount(hourAmount);
            dto.setAvgOrderValue(avgOrderValue);
            dto.setTrendPercentage(trendPercentage);
            
            timeSeries.add(dto);
            
            // 更新前一时段的销售额
            previousHourAmount = hourAmount;
        }
        
        return timeSeries;
    }
    
    private List<DailySalesReportDto.ChannelDistributionDto> getChannelDistribution(List<Sale> sales) {
        // 按销售员分组统计（作为渠道）
        Map<String, BigDecimal> channelMap = sales.stream()
                .collect(Collectors.groupingBy(
                        sale -> sale.getSeller() != null ? sale.getSeller().getUsername() : "未知",
                        Collectors.reducing(BigDecimal.ZERO, Sale::getTotalAmount, BigDecimal::add)
                ));
        
        return channelMap.entrySet().stream()
                .map(entry -> new DailySalesReportDto.ChannelDistributionDto(
                        entry.getKey(),
                        entry.getKey(),
                        entry.getValue(),
                        entry.getValue()
                ))
                .collect(Collectors.toList());
    }
}
