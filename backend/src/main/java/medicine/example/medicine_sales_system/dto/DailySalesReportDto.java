package medicine.example.medicine_sales_system.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DailySalesReportDto implements Serializable {
    private Map<String, Object> stats;
    private List<HotProductDto> hotProducts;
    private List<TimeSalesDto> timeSeries;
    private List<ChannelDistributionDto> channels;

    public DailySalesReportDto() {}

    public DailySalesReportDto(Map<String, Object> stats, List<HotProductDto> hotProducts, 
                               List<TimeSalesDto> timeSeries, List<ChannelDistributionDto> channels) {
        this.stats = stats;
        this.hotProducts = hotProducts;
        this.timeSeries = timeSeries;
        this.channels = channels;
    }

    public Map<String, Object> getStats() { return stats; }
    public void setStats(Map<String, Object> stats) { this.stats = stats; }

    public List<HotProductDto> getHotProducts() { return hotProducts; }
    public void setHotProducts(List<HotProductDto> hotProducts) { this.hotProducts = hotProducts; }

    public List<TimeSalesDto> getTimeSeries() { return timeSeries; }
    public void setTimeSeries(List<TimeSalesDto> timeSeries) { this.timeSeries = timeSeries; }

    public List<ChannelDistributionDto> getChannels() { return channels; }
    public void setChannels(List<ChannelDistributionDto> channels) { this.channels = channels; }

    // 热销药品DTO
    public static class HotProductDto implements Serializable {
        private Integer rank;
        private String drugName;
        private String specification;
        private String category;
        private Integer salesCount;
        private BigDecimal salesAmount;
        private Double growth;

        public HotProductDto() {}

        public HotProductDto(Integer rank, String drugName, String specification, String category, 
                            Integer salesCount, BigDecimal salesAmount, Double growth) {
            this.rank = rank;
            this.drugName = drugName;
            this.specification = specification;
            this.category = category;
            this.salesCount = salesCount;
            this.salesAmount = salesAmount;
            this.growth = growth;
        }

        public Integer getRank() { return rank; }
        public void setRank(Integer rank) { this.rank = rank; }

        public String getDrugName() { return drugName; }
        public void setDrugName(String drugName) { this.drugName = drugName; }

        public String getSpecification() { return specification; }
        public void setSpecification(String specification) { this.specification = specification; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public Integer getSalesCount() { return salesCount; }
        public void setSalesCount(Integer salesCount) { this.salesCount = salesCount; }

        public BigDecimal getSalesAmount() { return salesAmount; }
        public void setSalesAmount(BigDecimal salesAmount) { this.salesAmount = salesAmount; }

        public Double getGrowth() { return growth; }
        public void setGrowth(Double growth) { this.growth = growth; }
    }

    // 时段销售DTO
    public static class TimeSalesDto implements Serializable {
        private String time;
        private String timeRange;
        private Integer orderCount;
        private Integer customerCount;
        private BigDecimal salesAmount;
        private BigDecimal avgOrderValue;
        private Double trendPercentage;

        public TimeSalesDto() {}

        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }

        public String getTimeRange() { return timeRange; }
        public void setTimeRange(String timeRange) { this.timeRange = timeRange; }

        public Integer getOrderCount() { return orderCount; }
        public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }

        public Integer getCustomerCount() { return customerCount; }
        public void setCustomerCount(Integer customerCount) { this.customerCount = customerCount; }

        public BigDecimal getSalesAmount() { return salesAmount; }
        public void setSalesAmount(BigDecimal salesAmount) { this.salesAmount = salesAmount; }

        public BigDecimal getAvgOrderValue() { return avgOrderValue; }
        public void setAvgOrderValue(BigDecimal avgOrderValue) { this.avgOrderValue = avgOrderValue; }

        public Double getTrendPercentage() { return trendPercentage; }
        public void setTrendPercentage(Double trendPercentage) { this.trendPercentage = trendPercentage; }
    }

    // 渠道分布DTO
    public static class ChannelDistributionDto implements Serializable {
        private String name;
        private String channel;
        private BigDecimal value;
        private BigDecimal amount;

        public ChannelDistributionDto() {}

        public ChannelDistributionDto(String name, String channel, BigDecimal value, BigDecimal amount) {
            this.name = name;
            this.channel = channel;
            this.value = value;
            this.amount = amount;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getChannel() { return channel; }
        public void setChannel(String channel) { this.channel = channel; }

        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
}

