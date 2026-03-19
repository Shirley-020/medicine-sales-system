<template>
  <div class="sale-report-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>销售报表统计</h2>
        <p class="subtitle">多维度销售数据分析与统计报表</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="exportReport" :icon="Download">
          导出报表
        </el-button>
        <el-button @click="printReport" :icon="Printer">
          打印报表
        </el-button>
        <el-button @click="refreshData" :icon="Refresh">
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 日期范围选择 -->
    <el-card class="filter-card">
      <el-form :model="dateRange" label-width="100px" class="filter-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="统计周期">
              <el-select
                v-model="periodType"
                placeholder="请选择统计周期"
                @change="handlePeriodChange"
              >
                <el-option label="今日" value="today" />
                <el-option label="本周" value="week" />
                <el-option label="本月" value="month" />
                <el-option label="本季度" value="quarter" />
                <el-option label="本年" value="year" />
                <el-option label="自定义" value="custom" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="开始日期">
              <el-date-picker
                v-model="dateRange.startDate"
                type="date"
                placeholder="开始日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled="periodType !== 'custom'"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="结束日期">
              <el-date-picker
                v-model="dateRange.endDate"
                type="date"
                placeholder="结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled="periodType !== 'custom'"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24">
            <div class="filter-actions">
              <el-button type="primary" @click="generateReport" :icon="Search">
                生成报表
              </el-button>
              <el-button @click="resetFilter" :icon="Refresh">
                重置
              </el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 关键指标卡片 -->
    <div class="kpi-cards">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="kpi-card" shadow="hover">
            <div class="kpi-content">
              <div class="kpi-icon total-sales">
                <el-icon><Money /></el-icon>
              </div>
              <div class="kpi-info">
                <div class="kpi-label">总销售额</div>
                <div class="kpi-value">¥{{ formatNumber(reportStats.totalSales) }}</div>
                <div class="kpi-change" :class="getChangeClass(reportStats.salesGrowth)">
                  <el-icon v-if="reportStats.salesGrowth >= 0"><Top /></el-icon>
                  <el-icon v-else><Bottom /></el-icon>
                  {{ Math.abs(reportStats.salesGrowth) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="kpi-card" shadow="hover">
            <div class="kpi-content">
              <div class="kpi-icon orders">
                <el-icon><ShoppingCart /></el-icon>
              </div>
              <div class="kpi-info">
                <div class="kpi-label">总订单数</div>
                <div class="kpi-value">{{ reportStats.totalOrders }}</div>
                <div class="kpi-change" :class="getChangeClass(reportStats.orderGrowth)">
                  <el-icon v-if="reportStats.orderGrowth >= 0"><Top /></el-icon>
                  <el-icon v-else><Bottom /></el-icon>
                  {{ Math.abs(reportStats.orderGrowth) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="kpi-card" shadow="hover">
            <div class="kpi-content">
              <div class="kpi-icon profit">
                <el-icon><PieChart /></el-icon>
              </div>
              <div class="kpi-info">
                <div class="kpi-label">毛利润</div>
                <div class="kpi-value">¥{{ formatNumber(reportStats.grossProfit) }}</div>
                <div class="kpi-change" :class="getChangeClass(reportStats.profitGrowth)">
                  <el-icon v-if="reportStats.profitGrowth >= 0"><Top /></el-icon>
                  <el-icon v-else><Bottom /></el-icon>
                  {{ Math.abs(reportStats.profitGrowth) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="kpi-card" shadow="hover">
            <div class="kpi-content">
              <div class="kpi-icon avg-order">
                <el-icon><Histogram /></el-icon>
              </div>
              <div class="kpi-info">
                <div class="kpi-label">平均客单价</div>
                <div class="kpi-value">¥{{ formatNumber(reportStats.avgOrderValue) }}</div>
                <div class="kpi-change" :class="getChangeClass(reportStats.avgOrderGrowth)">
                  <el-icon v-if="reportStats.avgOrderGrowth >= 0"><Top /></el-icon>
                  <el-icon v-else><Bottom /></el-icon>
                  {{ Math.abs(reportStats.avgOrderGrowth) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 第一行图表：销售趋势和品类分布 -->
    <div class="chart-row">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="16">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>销售趋势分析</span>
                <div class="chart-actions">
                  <el-radio-group v-model="trendChartType" size="small">
                    <el-radio-button value="daily">日趋势</el-radio-button>
                    <el-radio-button value="weekly">周趋势</el-radio-button>
                    <el-radio-button value="monthly">月趋势</el-radio-button>
                  </el-radio-group>
                </div>
              </div>
            </template>
            <div ref="trendChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>销售品类分布</span>
              </div>
            </template>
            <div ref="categoryChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 第二行图表：药品销售排行和客户分析 -->
    <div class="chart-row">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>药品销售排行 TOP 10</span>
                <el-select v-model="rankType" size="small" style="width: 120px">
                  <el-option label="按销售额" value="amount" />
                  <el-option label="按销量" value="quantity" />
                  <el-option label="按利润" value="profit" />
                </el-select>
              </div>
            </template>
            <div ref="rankChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>客户购买力分析</span>
              </div>
            </template>
            <div ref="customerChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 第三行图表：渠道分析和月度对比 -->
    <div class="chart-row">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>销售渠道贡献度</span>
              </div>
            </template>
            <div ref="channelChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>月度销售对比</span>
                <div class="chart-actions">
                  <el-select v-model="compareYear" size="small" style="width: 120px">
                    <el-option label="2024年" value="2024" />
                    <el-option label="2023年" value="2023" />
                    <el-option label="2022年" value="2022" />
                  </el-select>
                </div>
              </div>
            </template>
            <div ref="monthlyChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 详细数据表格 -->
    <el-card class="detail-table-card">
      <template #header>
        <div class="table-header">
          <span>详细销售数据</span>
          <div class="table-actions">
            <el-button-group>
              <el-button @click="exportTableData" :icon="Download">
                导出数据
              </el-button>
              <el-button @click="showAllData" :icon="View">
                查看全部
              </el-button>
            </el-button-group>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        @sort-change="handleSortChange"
        stripe
      >
        <el-table-column prop="date" label="日期" width="120" sortable />
        <el-table-column prop="drugName" label="药品名称" min-width="150" />
        <el-table-column prop="category" label="品类" width="120" />
        <el-table-column prop="quantity" label="销量" width="100" align="right" sortable />
        <el-table-column prop="salesAmount" label="销售额" width="120" align="right" sortable>
          <template #default="{ row }">
            ¥{{ formatNumber(row.salesAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="cost" label="成本" width="120" align="right">
          <template #default="{ row }">
            ¥{{ formatNumber(row.cost) }}
          </template>
        </el-table-column>
        <el-table-column prop="profit" label="利润" width="120" align="right" sortable>
          <template #default="{ row }">
            <span :class="{ 'profit-positive': row.profit >= 0, 'profit-negative': row.profit < 0 }">
              ¥{{ formatNumber(row.profit) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="profitMargin" label="利润率" width="100" align="right" sortable>
          <template #default="{ row }">
            <span :class="{ 'margin-positive': row.profitMargin >= 20, 'margin-normal': row.profitMargin >= 10 && row.profitMargin < 20, 'margin-low': row.profitMargin < 10 }">
              {{ row.profitMargin.toFixed(1) }}%
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="channel" label="销售渠道" width="120" />
        <el-table-column prop="customerType" label="客户类型" width="120" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)" :icon="View">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 报表预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="报表预览"
      width="90%"
      top="5vh"
      :fullscreen="isFullscreen"
    >
      <div class="report-preview" ref="reportPreviewRef">
        <!-- 这里可以放置打印预览的内容 -->
        <div class="preview-header">
          <h2>医药销售系统 - 销售报表</h2>
          <div class="preview-info">
            <p>统计周期: {{ getPeriodText() }}</p>
            <p>生成时间: {{ new Date().toLocaleString('zh-CN') }}</p>
          </div>
        </div>

        <div class="preview-content">
          <!-- 可以在预览中显示图表和数据 -->
          <p>这是一个报表预览示例，实际打印时会包含完整的报表内容。</p>
          <p>总销售额: ¥{{ formatNumber(reportStats.totalSales) }}</p>
          <p>总订单数: {{ reportStats.totalOrders }}</p>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="previewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="printReport" :icon="Printer">
            打印
          </el-button>
          <el-button @click="toggleFullscreen" :icon="isFullscreen ? 'full-screen' : 'full-screen'">
            {{ isFullscreen ? '退出全屏' : '全屏' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, onActivated, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import {
  Download,
  Printer,
  Refresh,
  Search,
  Money,
  ShoppingCart,
  PieChart,
  Histogram,
  Top,
  Bottom,
  View
} from '@element-plus/icons-vue'
import reportApi from '@/api/report'
import saleApi from '@/api/sale'

// 图表引用
const trendChartRef = ref(null)
const categoryChartRef = ref(null)
const rankChartRef = ref(null)
const customerChartRef = ref(null)
const channelChartRef = ref(null)
const monthlyChartRef = ref(null)
const reportPreviewRef = ref(null)

// 图表实例
let trendChart = null
let categoryChart = null
let rankChart = null
let customerChart = null
let channelChart = null
let monthlyChart = null

// 筛选条件
const periodType = ref('month')
const dateRange = reactive({
  startDate: '',
  endDate: ''
})

// 图表类型选择
const trendChartType = ref('daily')
const rankType = ref('amount')
const compareYear = ref('2024')

// 加载状态
const loading = ref(false)

// 报表统计
const reportStats = reactive({
  totalSales: 0,
  salesGrowth: 0,
  totalOrders: 0,
  orderGrowth: 0,
  grossProfit: 0,
  profitGrowth: 0,
  avgOrderValue: 0,
  avgOrderGrowth: 0
})

// 图表数据（由后端提供或为空）
const trendData = ref({})
const categoryData = ref([])
const rankData = ref([])
const customerData = ref([])
const channelData = ref([])
const monthlyData = ref([])

// 表格数据
const tableData = ref([])

// 分页配置
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 排序配置
const sortConfig = reactive({
  prop: 'date',
  order: 'descending'
})

// 对话框控制
const previewDialogVisible = ref(false)
const isFullscreen = ref(false)

// 初始化日期范围
const initDateRange = () => {
  const today = new Date()

  switch (periodType.value) {
    case 'today':
      dateRange.startDate = today.toISOString().split('T')[0]
      dateRange.endDate = today.toISOString().split('T')[0]
      break
    case 'week':
      const weekStart = new Date(today)
      weekStart.setDate(today.getDate() - today.getDay() + 1)
      dateRange.startDate = weekStart.toISOString().split('T')[0]
      dateRange.endDate = today.toISOString().split('T')[0]
      break
    case 'month':
      const monthStart = new Date(today.getFullYear(), today.getMonth(), 1)
      dateRange.startDate = monthStart.toISOString().split('T')[0]
      dateRange.endDate = today.toISOString().split('T')[0]
      break
    case 'quarter':
      const quarter = Math.floor((today.getMonth() + 3) / 3)
      const quarterStart = new Date(today.getFullYear(), (quarter - 1) * 3, 1)
      dateRange.startDate = quarterStart.toISOString().split('T')[0]
      dateRange.endDate = today.toISOString().split('T')[0]
      break
    case 'year':
      const yearStart = new Date(today.getFullYear(), 0, 1)
      dateRange.startDate = yearStart.toISOString().split('T')[0]
      dateRange.endDate = today.toISOString().split('T')[0]
      break
  }
}

// 从后端加载销售报表数据
const loadReport = async () => {
  loading.value = true
  try {
    // 后端需要 ISO DATE_TIME 格式：2025-01-01T00:00:00
    const startDateTime = dateRange.startDate ? `${dateRange.startDate}T00:00:00` : null
    const endDateTime = dateRange.endDate ? `${dateRange.endDate}T23:59:59` : null
    
    if (!startDateTime || !endDateTime) {
      ElMessage.warning('请选择日期范围')
      loading.value = false
      return
    }
    
    const params = {
      start: startDateTime,
      end: endDateTime
    }
    
    const res = await reportApi.getSalesReport(params)
    
    // 后端返回格式：ResponseEntity<List<SalesSummaryDto>>
    // axios 会直接返回响应体（数组），但经过拦截器后可能是 res 本身
    let list = []
    if (Array.isArray(res)) {
      // 直接是数组
      list = res
    } else if (res && Array.isArray(res.data)) {
      // 包装在 data 中
      list = res.data
    } else {
      // 空数组
      list = []
      console.warn('销售报表数据格式异常，期望数组但收到:', res)
    }
    
    // 转换为表格数据格式
    const allRows = list.map(item => ({
      date: dateRange.startDate, // 使用查询的开始日期
      drugName: item.drugName || item.name || '未知药品',
      category: '', // 后端未提供，留空
      quantity: item.totalQty || item.quantity || 0,
      salesAmount: item.totalAmount ? (typeof item.totalAmount === 'number' ? item.totalAmount : parseFloat(item.totalAmount)) : 0,
      cost: 0, // 后端未提供成本，需要从其他地方计算
      profit: 0, // 后端未提供利润
      profitMargin: 0, // 后端未提供利润率
      channel: '', // 后端未提供
      customerType: '' // 后端未提供
    }))
    
    // 保存所有数据用于分页
    const allData = allRows
    pagination.total = allData.length
    
    // 分页处理
    const startIndex = (pagination.currentPage - 1) * pagination.pageSize
    const endIndex = startIndex + pagination.pageSize
    tableData.value = allData.slice(startIndex, endIndex)
    
    // 使用所有数据进行统计（按药品汇总）
    const rows = allData
    
    // 需要从销售单列表获取准确的订单数和总销售额
    // 因为报表接口返回的是按药品汇总的数据，无法准确计算订单数
    // 调用 /sale/report 接口获取销售单列表，确保与销售历史页面数据一致
    try {
      const saleParams = {
        start: startDateTime,
        end: endDateTime
      }
      const saleRes = await saleApi.getSaleReport(saleParams)
      const saleData = saleRes.data ?? saleRes
      const saleList = Array.isArray(saleData) ? saleData : (saleData.data ?? saleData.items ?? [])
      
      // 计算正确的订单数和总销售额（使用销售单的实收金额）
      reportStats.totalOrders = saleList.length
      if (saleList.length > 0) {
        // 使用销售单的实收金额计算总销售额
        const totalActualAmount = saleList.reduce((sum, order) => {
          const amount = order.actualAmount || 0
          return sum + (typeof amount === 'number' ? amount : parseFloat(amount) || 0)
        }, 0)
        
        // 使用销售单的实收金额作为总销售额（与销售历史页面保持一致）
        reportStats.totalSales = totalActualAmount
        reportStats.avgOrderValue = totalActualAmount / saleList.length
      } else {
        reportStats.totalOrders = 0
        reportStats.avgOrderValue = 0
        reportStats.totalSales = 0
      }
    } catch (err) {
      console.warn('获取销售单列表失败，使用药品汇总数据估算', err)
      // 如果获取销售单列表失败，使用药品汇总数据作为备选方案（不够准确）
      const totalAmountFromDrugs = rows.reduce((s, r) => s + (r.salesAmount || 0), 0)
      reportStats.totalSales = totalAmountFromDrugs
      reportStats.totalOrders = rows.length // 这是药品种类数，不是订单数，不准确
      reportStats.avgOrderValue = rows.length > 0 ? totalAmountFromDrugs / rows.length : 0
    }
    
    // 计算毛利润（假设毛利率30%）
    reportStats.grossProfit = reportStats.totalSales * 0.3
    
    // 生成图表数据（基于实际数据）
    // 销售趋势：按日期分组（这里简化为单日数据）
    trendData.value = {
      daily: {
        x: [dateRange.startDate],
        series: [reportStats.totalSales]
      }
    }
    
    // 品类分布：按药品分组
    const categoryMap = new Map()
    rows.forEach(row => {
      const name = row.drugName
      if (!categoryMap.has(name)) {
        categoryMap.set(name, 0)
      }
      categoryMap.set(name, categoryMap.get(name) + row.salesAmount)
    })
    categoryData.value = Array.from(categoryMap.entries()).map(([name, value]) => ({ name, value }))
    
    // 药品排行：按销售额排序
    rankData.value = Array.from(categoryMap.entries())
      .map(([name, value]) => ({ name, value }))
      .sort((a, b) => b.value - a.value)
      .slice(0, 10)
    
    // 客户和渠道数据为空（后端未提供）
    customerData.value = []
    channelData.value = []
    monthlyData.value = []

    updateCharts()
  } catch (err) {
    console.error('加载销售报表失败', err)
    ElMessage.error(err.message || '加载销售报表失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

// 触发生成报表时调用后端
const generateReport = () => {
  // 确保日期范围已初始化
  if (!dateRange.startDate || !dateRange.endDate) {
    initDateRange()
  }
  loading.value = true
  ElMessage.success('正在生成报表...')
  loadReport().then(() => {
    ElMessage.success('报表生成成功')
  }).catch(() => {
    // 错误已在 loadReport 中处理
  })
}

// 初始化图表
const initCharts = () => {
  // 销毁旧图表
  const charts = [trendChart, categoryChart, rankChart, customerChart, channelChart, monthlyChart]
  charts.forEach(chart => {
    if (chart) {
      chart.dispose()
    }
  })

  // 创建新图表实例
  const refs = [
    trendChartRef.value,
    categoryChartRef.value,
    rankChartRef.value,
    customerChartRef.value,
    channelChartRef.value,
    monthlyChartRef.value
  ]

  const chartInstances = refs.map(ref => ref ? echarts.init(ref) : null)

  if (chartInstances[0]) trendChart = chartInstances[0]
  if (chartInstances[1]) categoryChart = chartInstances[1]
  if (chartInstances[2]) rankChart = chartInstances[2]
  if (chartInstances[3]) customerChart = chartInstances[3]
  if (chartInstances[4]) channelChart = chartInstances[4]
  if (chartInstances[5]) monthlyChart = chartInstances[5]

  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
}

// 更新所有图表
const updateCharts = () => {
  if (!trendChart || !categoryChart || !rankChart || !customerChart || !channelChart || !monthlyChart) {
    console.log('图表未完全初始化')
    return
  }

  updateTrendChart()
  updateCategoryChart()
  updateRankChart()
  updateCustomerChart()
  updateChannelChart()
  updateMonthlyChart()
}

// 更新销售趋势图
const updateTrendChart = () => {
  if (!trendChart) return

  let xAxisData = []
  let seriesData = []

  // 优先使用后端返回的 trendData（支持多种结构），若无则置空
  const td = trendData.value || {}
  const typeKey = trendChartType.value

  if (td[typeKey] && td[typeKey].x && td[typeKey].series) {
    xAxisData = td[typeKey].x
    seriesData = td[typeKey].series
  } else if (td.x && td.series) {
    xAxisData = td.x
    seriesData = td.series
  } else if (Array.isArray(td)) {
    // 如果直接是数组，按 length 推 x 轴序列
    xAxisData = td.map((_, i) => String(i + 1))
    seriesData = td
  } else {
    // 无数据则清空
    xAxisData = []
    seriesData = []
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        return `${params[0].name}<br/>销售额: ¥${formatNumber(params[0].value)}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      top: '10%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xAxisData
    },
    yAxis: {
      type: 'value',
      name: '销售额(元)',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        data: seriesData,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        },
        itemStyle: {
          color: '#409EFF'
        },
        lineStyle: {
          width: 3
        }
      }
    ]
  }

  trendChart.setOption(option)
}

// 更新品类分布图
const updateCategoryChart = () => {
  if (!categoryChart) return

  // 优先使用后端返回的 categoryData
  const categorySeries = Array.isArray(categoryData.value) ? categoryData.value.map(item => ({ value: item.value ?? item.amount ?? 0, name: item.name ?? item.category })) : []

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: '5%',
      top: 'center',
      data: categorySeries.map(d => d.name)
    },
    series: [
      {
        name: '销售品类分布',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        avoidLabelOverlap: true,
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
            fontSize: '16',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: categorySeries
      }
    ]
  }

  categoryChart.setOption(option)
}

// 更新药品排行图
const updateRankChart = () => {
  if (!rankChart) return

  let data = []
  let title = ''

  // 优先使用后端返回的 rankData，否则回退为空
  const rd = Array.isArray(rankData.value) ? rankData.value : []
  if (rd.length > 0) {
    // 假设后端直接返回已排序的 {name,value} 列表
    data = rd
    title = rankType.value === 'amount' ? '药品销售额排行' : (rankType.value === 'quantity' ? '药品销量排行' : '药品利润排行')
  } else {
    data = []
    title = rankType.value === 'amount' ? '药品销售额排行' : (rankType.value === 'quantity' ? '药品销量排行' : '药品利润排行')
  }

  const option = {
    title: {
      text: title,
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      top: '15%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      name: rankType.value === 'quantity' ? '销量(件)' : '金额(元)',
      axisLabel: {
        formatter: rankType.value === 'quantity' ? '{value}' : '¥{value}'
      }
    },
    yAxis: {
      type: 'category',
      data: data.map(item => item.name),
      inverse: true
    },
    series: [
      {
        name: rankType.value === 'quantity' ? '销量' : (rankType.value === 'amount' ? '销售额' : '利润'),
        type: 'bar',
        data: data.map(item => item.value),
        itemStyle: {
          color: function(params) {
            const colorList = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#9a60b4', '#ea7ccc']
            return colorList[params.dataIndex % colorList.length]
          }
        },
        label: {
          show: true,
          position: 'right',
          formatter: rankType.value === 'quantity' ? '{c}' : '¥{c}'
        }
      }
    ]
  }

  rankChart.setOption(option)
}

// 更新客户分析图
const updateCustomerChart = () => {
  if (!customerChart) return
  // 从后端数据优先构建图表，后端未返回则显示空图
  const cd = Array.isArray(customerData.value) ? customerData.value : []
  const xAxisData = cd.length > 0 ? cd.map(item => item.name ?? item.type ?? item.category ?? '') : []
  const customersSeries = cd.length > 0 ? cd.map(item => item.count ?? item.customers ?? 0) : []
  const amountSeries = cd.length > 0 ? cd.map(item => item.amount ?? item.purchase ?? item.totalAmount ?? 0) : []
  const avgSeries = cd.length > 0 ? cd.map(item => item.avgOrder ?? item.avg ?? (item.amount && item.count ? Math.round((item.amount / item.count) * 100) / 100 : 0)) : []

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      }
    },
    legend: {
      data: ['客户数', '购买金额', '客单价'],
      top: '5%',
      left: 'center'
    },
    grid: {
      left: '3%',
      right: '4%',
      top: '15%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: xAxisData,
        axisPointer: {
          type: 'shadow'
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '客户数/购买金额'
      },
      {
        type: 'value',
        name: '客单价'
      }
    ],
    series: [
      {
        name: '客户数',
        type: 'bar',
        data: customersSeries,
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '购买金额',
        type: 'bar',
        data: amountSeries,
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '客单价',
        type: 'line',
        yAxisIndex: 1,
        data: avgSeries,
        itemStyle: {
          color: '#E6A23C'
        }
      }
    ]
  }

  customerChart.setOption(option)
}

// 更新渠道分析图
const updateChannelChart = () => {
  if (!channelChart) return
  // 使用后端 channelData 构造饼图数据，后端未返回则置空
  const cd = Array.isArray(channelData.value) ? channelData.value : []
  const seriesData = cd.length > 0 ? cd.map(item => ({ value: item.value ?? item.amount ?? 0, name: item.name ?? item.channel ?? item.type })) : []
  const legendData = seriesData.map(d => d.name)

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: legendData
    },
    series: [
      {
        name: '销售渠道',
        type: 'pie',
        radius: '55%',
        center: ['40%', '50%'],
        roseType: 'radius',
        itemStyle: {
          borderRadius: 5
        },
        data: seriesData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  channelChart.setOption(option)
}

// 更新月度对比图
const updateMonthlyChart = () => {
  if (!monthlyChart) return
  // 使用后端提供的 monthlyData 构造月度对比，后端未返回则置空
  const md = monthlyData.value || {}
  const months = md.months ?? md.labels ?? ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
  const currentYearData = Array.isArray(md.currentYear) ? md.currentYear : (Array.isArray(md.current) ? md.current : (Array.isArray(md.thisYear) ? md.thisYear : []))
  const lastYearData = Array.isArray(md.lastYear) ? md.lastYear : (Array.isArray(md.previousYear) ? md.previousYear : (Array.isArray(md.last) ? md.last : []))

  // 如果后端未返回完整数据，保持数组长度一致以防报错
  const safeCurrent = months.map((_, i) => currentYearData[i] ?? 0)
  const safeLast = months.map((_, i) => lastYearData[i] ?? 0)

  const growthSeries = months.map((_, index) => {
    const last = safeLast[index]
    if (!last) return 0
    const g = ((safeCurrent[index] - last) / last) * 100
    return Math.round(g * 10) / 10
  })

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: [compareYear.value + '年', (parseInt(compareYear.value) - 1) + '年', '同比增长'],
      top: '5%',
      left: 'center'
    },
    grid: {
      left: '3%',
      right: '4%',
      top: '15%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: months
    },
    yAxis: [
      {
        type: 'value',
        name: '销售额(元)',
        axisLabel: {
          formatter: '¥{value}'
        }
      },
      {
        type: 'value',
        name: '增长率(%)',
        position: 'right',
        axisLabel: {
          formatter: '{value}%'
        }
      }
    ],
    series: [
      {
        name: compareYear.value + '年',
        type: 'bar',
        data: safeCurrent,
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: (parseInt(compareYear.value) - 1) + '年',
        type: 'bar',
        data: safeLast,
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '同比增长',
        type: 'line',
        yAxisIndex: 1,
        data: growthSeries,
        itemStyle: {
          color: '#E6A23C'
        },
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: {
          width: 3
        }
      }
    ]
  }

  monthlyChart.setOption(option)
}

// 窗口大小变化处理
const handleResize = () => {
  const charts = [trendChart, categoryChart, rankChart, customerChart, channelChart, monthlyChart]
  charts.forEach(chart => {
    if (chart) {
      chart.resize()
    }
  })
}

// 格式化和工具函数
const formatNumber = (num) => {
  return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const getChangeClass = (value) => {
  return value >= 0 ? 'positive' : 'negative'
}

const getPeriodText = () => {
  const map = {
    today: '今日',
    week: '本周',
    month: '本月',
    quarter: '本季度',
    year: '本年',
    custom: `自定义 (${dateRange.startDate} 至 ${dateRange.endDate})`
  }
  return map[periodType.value] || '未知周期'
}

// 事件处理
const handlePeriodChange = () => {
  if (periodType.value !== 'custom') {
    initDateRange()
  }
}


const resetFilter = () => {
  periodType.value = 'month'
  initDateRange()
  generateReport()
}

const refreshData = () => {
  // 重新初始化日期范围，确保使用最新日期
  initDateRange()
  // 重置分页到第一页
  pagination.currentPage = 1
  // 重新加载数据
  loadReport().then(() => {
    ElMessage.success('数据已刷新')
  }).catch(() => {
    // 错误已在 loadReport 中处理
  })
}

const exportReport = () => {
  ElMessageBox.confirm(
    '确认导出销售报表？',
    '导出确认',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    loading.value = true
    try {
      const params = { /* 填入当前筛选条件 */ }
      const res = await reportApi.exportSalesReport(params)
      const blob = new Blob([res.data], { type: res.headers['content-type'] || 'application/octet-stream' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'sales_report.xlsx'
      document.body.appendChild(a)
      a.click()
      a.remove()
      window.URL.revokeObjectURL(url)
      ElMessage.success('报表导出成功')
    } catch (err) {
      console.error('导出失败', err)
      ElMessage.error(err.message || '导出失败')
    } finally {
      loading.value = false
    }
  })
}

const printReport = () => {
  previewDialogVisible.value = true
}

const toggleFullscreen = () => {
  isFullscreen.value = !isFullscreen.value
}

const exportTableData = async () => {
  loading.value = true
  try {
    const params = { /* 当前表格筛选条件 */ }
    const res = await reportApi.exportSalesReport(params)
    const blob = new Blob([res.data], { type: res.headers['content-type'] || 'application/octet-stream' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'sales_table_export.xlsx'
    document.body.appendChild(a)
    a.click()
    a.remove()
    window.URL.revokeObjectURL(url)
    ElMessage.success('表格导出成功')
  } catch (err) {
    console.error('导出失败', err)
    ElMessage.error(err.message || '导出失败')
  } finally {
    loading.value = false
  }
}

const showAllData = () => {
  pagination.pageSize = 100
  loadReport()
}

const viewDetail = (row) => {
  ElMessageBox.alert(
    `销售详情：<br>
    药品：${row.drugName}<br>
    日期：${row.date}<br>
    销售额：¥${formatNumber(row.salesAmount)}<br>
    利润：¥${formatNumber(row.profit)}<br>
    利润率：${row.profitMargin.toFixed(1)}%`,
    '销售详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '确定'
    }
  )
}

const handleSortChange = (sort) => {
  sortConfig.prop = sort.prop
  sortConfig.order = sort.order
  // 这里可以添加排序逻辑
  ElMessage.info('已按' + sort.prop + '排序')
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  loadReport()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadReport()
}

// 组件生命周期
onMounted(() => {
  // 初始化日期范围
  initDateRange()
  
  // 立即加载数据
  generateReport()

  nextTick(() => {
    initCharts()
    // 延迟更新图表，确保DOM已完全渲染
    setTimeout(() => {
      updateCharts()
    }, 500)
  })
})

// 使用 onActivated 确保每次进入页面时都刷新数据（如果使用了 keep-alive）
onActivated(() => {
  // 每次激活页面时刷新数据
  if (dateRange.startDate && dateRange.endDate) {
    loadReport()
  }
})

onBeforeUnmount(() => {
  // 销毁图表实例
  const charts = [trendChart, categoryChart, rankChart, customerChart, channelChart, monthlyChart]
  charts.forEach(chart => {
    if (chart) {
      chart.dispose()
    }
  })

  // 移除事件监听
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.sale-report-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.subtitle {
  margin: 5px 0 0;
  color: #666;
  font-size: 14px;
}

.header-right {
  display: flex;
  gap: 10px;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  margin-top: 10px;
}

.filter-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 10px;
}

.kpi-cards {
  margin-bottom: 20px;
}

.kpi-card {
  margin-bottom: 0;
}

.kpi-content {
  display: flex;
  align-items: center;
  padding: 15px 0;
}

.kpi-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 28px;
}

.kpi-icon.total-sales {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409EFF;
}

.kpi-icon.orders {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

.kpi-icon.profit {
  background-color: rgba(230, 162, 60, 0.1);
  color: #E6A23C;
}

.kpi-icon.avg-order {
  background-color: rgba(157, 103, 255, 0.1);
  color: #8E44AD;
}

.kpi-info {
  flex: 1;
}

.kpi-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.kpi-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.kpi-change {
  font-size: 12px;
  display: flex;
  align-items: center;
}

.kpi-change.positive {
  color: #67C23A;
}

.kpi-change.negative {
  color: #F56C6C;
}

.kpi-change .el-icon {
  margin-right: 4px;
}

.chart-row {
  margin-bottom: 20px;
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
}

.detail-table-card {
  margin-bottom: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profit-positive {
  color: #67C23A;
  font-weight: bold;
}

.profit-negative {
  color: #F56C6C;
  font-weight: bold;
}

.margin-positive {
  color: #67C23A;
  font-weight: bold;
}

.margin-normal {
  color: #E6A23C;
  font-weight: bold;
}

.margin-low {
  color: #F56C6C;
  font-weight: bold;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.report-preview {
  padding: 20px;
  background-color: white;
}

.preview-header {
  text-align: center;
  margin-bottom: 30px;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 20px;
}

.preview-header h2 {
  margin: 0 0 10px 0;
  color: #333;
}

.preview-info {
  color: #666;
  font-size: 14px;
}

.preview-info p {
  margin: 5px 0;
}

.preview-content {
  margin-top: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .header-right {
    width: 100%;
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .kpi-cards .el-col {
    margin-bottom: 15px;
  }

  .chart-card {
    height: 350px;
  }

  .chart-container {
    height: 280px;
  }

  .chart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .chart-actions {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
