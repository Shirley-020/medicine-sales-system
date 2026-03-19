<template>
  <div class="sale-daily-container">
    <!-- 头部标题和日期选择 -->
    <div class="header-section">
      <div class="title-section">
        <h1 class="page-title">
          <el-icon class="title-icon"><TrendCharts /></el-icon>
          当日销售统计
        </h1>
        <p class="page-subtitle">实时监控当日销售数据与趋势</p>
      </div>

      <div class="date-picker-section">
        <el-date-picker
          v-model="selectedDate"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="fetchDailyData"
        />
        <el-button type="primary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-button type="success" @click="exportData">
          <el-icon><Download /></el-icon>
          导出报表
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :lg="6">
          <el-card shadow="hover" class="stat-card total-sales">
            <div class="card-content">
              <div class="card-icon">
                <el-icon><Money /></el-icon>
              </div>
              <div class="card-text">
                <div class="card-value">¥{{ formatNumber(dailyStats.totalSales) }}</div>
                <div class="card-label">当日销售额</div>
                <div class="card-change" :class="dailyStats.salesGrowth >= 0 ? 'positive' : 'negative'">
                  <el-icon v-if="dailyStats.salesGrowth >= 0"><Top /></el-icon>
                  <el-icon v-else><Bottom /></el-icon>
                  {{ Math.abs(dailyStats.salesGrowth) }}% 较昨日
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :lg="6">
          <el-card shadow="hover" class="stat-card orders">
            <div class="card-content">
              <div class="card-icon">
                <el-icon><ShoppingCart /></el-icon>
              </div>
              <div class="card-text">
                <div class="card-value">{{ dailyStats.orderCount }}</div>
                <div class="card-label">订单数量</div>
                <div class="card-change" :class="dailyStats.orderGrowth >= 0 ? 'positive' : 'negative'">
                  <el-icon v-if="dailyStats.orderGrowth >= 0"><Top /></el-icon>
                  <el-icon v-else><Bottom /></el-icon>
                  {{ Math.abs(dailyStats.orderGrowth) }}% 较昨日
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :lg="6">
          <el-card shadow="hover" class="stat-card customers">
            <div class="card-content">
              <div class="card-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="card-text">
                <div class="card-value">{{ dailyStats.customerCount }}</div>
                <div class="card-label">客户数量</div>
                <div class="card-change" :class="dailyStats.customerGrowth >= 0 ? 'positive' : 'negative'">
                  <el-icon v-if="dailyStats.customerGrowth >= 0"><Top /></el-icon>
                  <el-icon v-else><Bottom /></el-icon>
                  {{ Math.abs(dailyStats.customerGrowth) }}% 较昨日
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :lg="6">
          <el-card shadow="hover" class="stat-card avg-order">
            <div class="card-content">
              <div class="card-icon">
                <el-icon><Histogram /></el-icon>
              </div>
              <div class="card-text">
                <div class="card-value">¥{{ formatNumber(dailyStats.avgOrderValue) }}</div>
                <div class="card-label">客单价</div>
                <div class="card-change" :class="dailyStats.avgOrderGrowth >= 0 ? 'positive' : 'negative'">
                  <el-icon v-if="dailyStats.avgOrderGrowth >= 0"><Top /></el-icon>
                  <el-icon v-else><Bottom /></el-icon>
                  {{ Math.abs(dailyStats.avgOrderGrowth) }}% 较昨日
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="16">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>销售趋势（今日）</span>
                <div class="chart-actions">
                  <el-radio-group v-model="salesChartType" size="small" @change="updateSalesChart">
                    <el-radio-button value="hourly">按小时</el-radio-button>
                  </el-radio-group>
                </div>
              </div>
            </template>
            <!-- 关键修改：使用 ref 绑定 -->
            <div ref="salesChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <el-col :xs="24" :lg="8">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>销售渠道分布</span>
              </div>
            </template>
            <!-- 关键修改：使用 ref 绑定 -->
            <div ref="channelChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 热销药品表格 -->
    <div class="hot-products-section">
      <el-card shadow="hover">
        <template #header>
          <div class="table-header">
            <span>今日热销药品 TOP 10</span>
            <el-button type="primary" size="small" @click="viewAllProducts">
              查看全部
            </el-button>
          </div>
        </template>

        <el-table :data="hotProducts" style="width: 100%" v-loading="loading">
          <el-table-column prop="rank" label="排名" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="getRankType(row.rank)" size="small">
                {{ row.rank }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="drugName" label="药品名称" min-width="150">
            <template #default="{ row }">
              <div class="drug-info">
                <div class="drug-name">{{ row.drugName }}</div>
                <div class="drug-spec">{{ row.specification }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="品类" width="120" />
          <el-table-column prop="salesCount" label="销量" width="100" align="center">
            <template #default="{ row }">
              <span class="sales-count">{{ row.salesCount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="salesAmount" label="销售额" width="120" align="right">
            <template #default="{ row }">
              <span class="sales-amount">¥{{ formatNumber(row.salesAmount) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="growth" label="趋势" width="100" align="center">
            <template #default="{ row }">
              <div class="trend-indicator" :class="row.growth >= 0 ? 'positive' : 'negative'">
                <el-icon v-if="row.growth >= 0"><Top /></el-icon>
                <el-icon v-else><Bottom /></el-icon>
                {{ Math.abs(row.growth) }}%
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="viewDrugDetail(row)">
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 时段销售表格 -->
    <div class="time-sales-section">
      <el-card shadow="hover">
        <template #header>
          <div class="table-header">
            <span>各时段销售明细</span>
            <el-select v-model="timeInterval" placeholder="选择时段" size="small" style="width: 120px">
              <el-option label="每小时" value="hourly" />
              <el-option label="每半小时" value="half-hour" />
              <el-option label="每15分钟" value="quarter-hour" />
            </el-select>
          </div>
        </template>

        <el-table :data="timeSalesData" style="width: 100%">
          <el-table-column prop="timeRange" label="时间段" width="120" />
          <el-table-column prop="orderCount" label="订单数" width="100" align="center" />
          <el-table-column prop="customerCount" label="客户数" width="100" align="center" />
          <el-table-column prop="salesAmount" label="销售额" width="120" align="right">
            <template #default="{ row }">
              ¥{{ formatNumber(row.salesAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="avgOrderValue" label="客单价" width="120" align="right">
            <template #default="{ row }">
              ¥{{ formatNumber(row.avgOrderValue) }}
            </template>
          </el-table-column>
          <el-table-column label="销售趋势" width="150">
            <template #default="{ row }">
              <el-progress
                :percentage="Math.min(100, Math.max(0, Math.abs(row.trendPercentage || 0)))"
                :show-text="false"
                :color="row.trendPercentage >= 0 ? '#67c23a' : '#f56c6c'"
              />
              <div class="trend-text">
                {{ row.trendPercentage >= 0 ? '↑' : '↓' }} {{ Math.abs(row.trendPercentage || 0).toFixed(1) }}%
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import {
  TrendCharts,
  Refresh,
  Download,
  Money,
  ShoppingCart,
  User,
  Histogram,
  Top,
  Bottom
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 响应式数据
const selectedDate = ref(new Date().toISOString().split('T')[0]) // 今天
const loading = ref(false)
const salesChartType = ref('hourly')
const timeInterval = ref('hourly')

// ECharts 图表引用（与StockWarning.vue保持一致）
const salesChartRef = ref(null)
const channelChartRef = ref(null)
let salesChartInstance = null
let channelChartInstance = null

// 当日统计数据（由后端提供，缺省为零值）
const dailyStats = ref({
  totalSales: 0,
  salesGrowth: 0,
  orderCount: 0,
  orderGrowth: 0,
  customerCount: 0,
  customerGrowth: 0,
  avgOrderValue: 0,
  avgOrderGrowth: 0
})

// 热销药品数据（由后端提供）
const hotProducts = ref([])

// 时段销售数据（由后端提供）
const timeSalesData = ref([])

// 渠道分布（由后端提供）
const channelDistribution = ref([])

// 方法
const formatNumber = (num) => {
  return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const getRankType = (rank) => {
  if (rank <= 3) return 'danger'
  if (rank <= 6) return 'warning'
  if (rank <= 10) return 'success'
  return 'info'
}

import reportApi from '@/api/report'

const fetchDailyData = async () => {
  loading.value = true
  try {
    const res = await reportApi.getSalesReport({ date: selectedDate.value })
    const data = res.data ?? res

    // 兼容返回结构，后端可能返回 { stats, hotProducts, timeSeries }
    const stats = data.stats ?? data.summary ?? {}
    dailyStats.value.totalSales = stats.totalSales ?? stats.salesAmount ?? 0
    dailyStats.value.salesGrowth = stats.salesGrowth ?? 0
    dailyStats.value.orderCount = stats.orderCount ?? stats.salesCount ?? 0
    dailyStats.value.orderGrowth = stats.orderGrowth ?? 0
    dailyStats.value.customerCount = stats.customerCount ?? 0
    dailyStats.value.customerGrowth = stats.customerGrowth ?? 0
    dailyStats.value.avgOrderValue = stats.avgOrderValue ?? 0
    dailyStats.value.avgOrderGrowth = stats.avgOrderGrowth ?? 0

    hotProducts.value = data.hotProducts ?? data.topProducts ?? []
    timeSalesData.value = data.timeSeries ?? data.timeSales ?? []
    channelDistribution.value = data.channels ?? data.channelDistribution ?? data.channelData ?? []

    ElMessage.success(`已加载 ${selectedDate.value} 的销售数据`)
    updateCharts()
  } catch (error) {
    console.error('加载日销售数据失败', error)
    ElMessage.error('加载数据失败')
    hotProducts.value = []
    timeSalesData.value = []
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  fetchDailyData()
}

const exportData = () => {
  ElMessageBox.confirm('确认导出当日销售报表？', '导出报表', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await reportApi.getSalesReport({ date: selectedDate.value, export: true })
      const data = res.data ?? res
      if (data && (data.url || data.downloadUrl || data.link)) {
        window.open(data.url ?? data.downloadUrl ?? data.link, '_blank')
        ElMessage.success('导出开始，请检查下载')
      } else {
        throw new Error('后端未返回导出链接')
      }
    } catch (err) {
      console.warn('导出接口不可用或未实现', err)
      ElMessage.info('导出功能暂不可用')
    }
  })
}

const viewAllProducts = () => {
  ElMessage.info('跳转到药品销售页面')
}

const viewDrugDetail = (drug) => {
  ElMessageBox.alert(
    `药品详情：${drug.drugName}<br>规格：${drug.specification}<br>销售额：¥${formatNumber(drug.salesAmount)}<br>销量：${drug.salesCount}`,
    '药品详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '确定'
    }
  )
}

// 初始化ECharts图表（与StockWarning.vue保持一致）
const initCharts = () => {
  console.log('开始初始化图表...')
  console.log('销售图表DOM:', salesChartRef.value)
  console.log('渠道图表DOM:', channelChartRef.value)

  // 销毁旧的实例
  if (salesChartInstance) {
    salesChartInstance.dispose()
  }
  if (channelChartInstance) {
    channelChartInstance.dispose()
  }

  // 确保DOM元素存在
  if (salesChartRef.value && channelChartRef.value) {
    // 初始化销售趋势图表
    salesChartInstance = echarts.init(salesChartRef.value)
    console.log('销售图表实例已创建:', salesChartInstance)

    // 初始化渠道分布图表
    channelChartInstance = echarts.init(channelChartRef.value)
    console.log('渠道图表实例已创建:', channelChartInstance)

    updateCharts()
  } else {
    console.error('图表DOM元素未找到，延迟重试...')
    setTimeout(() => {
      if (salesChartRef.value && channelChartRef.value) {
        initCharts()
      }
    }, 100)
  }
}

const updateCharts = () => {
  if (!salesChartInstance || !channelChartInstance) {
    console.log('图表实例未初始化，跳过更新')
    return
  }

  console.log('正在更新图表...')

  // 销售趋势图表选项（优先使用后端返回的 timeSalesData）
  const xAxisData = timeSalesData.value && timeSalesData.value.length ? timeSalesData.value.map(item => item.time || item.label) : []
  const salesSeries = timeSalesData.value && timeSalesData.value.length ? timeSalesData.value.map(item => item.salesAmount ?? item.sales ?? 0) : []
  const ordersSeries = timeSalesData.value && timeSalesData.value.length ? timeSalesData.value.map(item => item.orderCount ?? item.orders ?? 0) : []

  const salesOption = {
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        if (!params || !params.length) return ''
        let result = (params[0].name || '') + '<br/>'
        params.forEach(item => {
          result += `${item.marker} ${item.seriesName}: ¥${formatNumber(item.value || 0)}<br/>`
        })
        return result
      }
    },
    legend: {
      data: ['销售额', '订单数'],
      top: 0,
      left: 'center'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xAxisData
    },
    yAxis: [
      {
        type: 'value',
        name: '销售额',
        position: 'left',
        axisLabel: {
          formatter: '¥{value}'
        }
      },
      {
        type: 'value',
        name: '订单数',
        position: 'right'
      }
    ],
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        yAxisIndex: 0,
        data: salesSeries,
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '订单数',
        type: 'bar',
        yAxisIndex: 1,
        data: ordersSeries,
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }

  // 渠道分布图表选项（优先使用后端返回的 channelDistribution）
  const channelData = channelDistribution.value && channelDistribution.value.length ? channelDistribution.value.map(item => ({ value: item.value ?? item.amount ?? 0, name: item.name ?? item.channel })) : []

  const channelOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: channelData.map(d => d.name)
    },
    series: [
      {
        name: '销售渠道',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: channelData
      }
    ]
  }

  try {
    salesChartInstance.setOption(salesOption)
    channelChartInstance.setOption(channelOption)
    console.log('图表更新成功')
  } catch (error) {
    console.error('图表更新失败:', error)
  }
}

const updateSalesChart = () => {
  // 根据选择的图表类型更新销售图表
  updateCharts()
}

// 监听窗口大小变化，重新调整图表大小
const handleResize = () => {
  if (salesChartInstance) {
    salesChartInstance.resize()
  }
  if (channelChartInstance) {
    channelChartInstance.resize()
  }
}

// 生命周期钩子
onMounted(() => {
  console.log('SaleDaily组件挂载完成')
  fetchDailyData()

  // 延迟初始化图表，确保DOM已完全渲染
  setTimeout(() => {
    console.log('开始初始化图表...')
    initCharts()
  }, 200)

  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  if (salesChartInstance) {
    salesChartInstance.dispose()
  }
  if (channelChartInstance) {
    channelChartInstance.dispose()
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.sale-daily-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.title-section {
  margin-bottom: 16px;
}

.page-title {
  display: flex;
  align-items: center;
  margin: 0;
  color: #303133;
  font-size: 24px;
}

.title-icon {
  margin-right: 12px;
  color: #409EFF;
  font-size: 28px;
}

.page-subtitle {
  margin: 8px 0 0;
  color: #909399;
  font-size: 14px;
}

.date-picker-section {
  display: flex;
  gap: 12px;
  align-items: center;
}

.stats-cards {
  margin-bottom: 24px;
}

.stat-card {
  margin-bottom: 20px;
}

.card-content {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 28px;
}

.total-sales .card-icon {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409EFF;
}

.orders .card-icon {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

.customers .card-icon {
  background-color: rgba(255, 158, 91, 0.1);
  color: #E6A23C;
}

.avg-order .card-icon {
  background-color: rgba(157, 103, 255, 0.1);
  color: #8E44AD;
}

.card-text {
  flex: 1;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.card-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.card-change {
  font-size: 12px;
  display: flex;
  align-items: center;
}

.card-change.positive {
  color: #67C23A;
}

.card-change.negative {
  color: #F56C6C;
}

.card-change .el-icon {
  margin-right: 4px;
}

.charts-section {
  margin-bottom: 24px;
}

.chart-card {
  height: 400px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  width: 100%;
  height: 320px;
  min-height: 320px;
}

.hot-products-section,
.time-sales-section {
  margin-bottom: 24px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.drug-info {
  line-height: 1.4;
}

.drug-name {
  font-weight: 500;
  color: #303133;
}

.drug-spec {
  font-size: 12px;
  color: #909399;
}

.sales-count {
  font-weight: bold;
  color: #409EFF;
}

.sales-amount {
  font-weight: bold;
  color: #67C23A;
}

.trend-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  width: fit-content;
  margin: 0 auto;
}

.trend-indicator.positive {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

.trend-indicator.negative {
  background-color: rgba(245, 108, 108, 0.1);
  color: #F56C6C;
}

.trend-indicator .el-icon {
  margin-right: 4px;
}

.trend-text {
  font-size: 12px;
  margin-top: 4px;
  text-align: center;
}

@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
  }

  .date-picker-section {
    width: 100%;
    justify-content: flex-start;
    margin-top: 16px;
  }

  .chart-card {
    height: 350px;
  }

  .chart-container {
    height: 280px;
  }
}
</style>
