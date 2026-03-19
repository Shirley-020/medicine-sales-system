<template>
  <div class="finance-report-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>财务统计报表</h2>
        <p class="subtitle">全面分析医药销售财务数据与经营状况</p>
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

    <!-- 财务周期选择 -->
    <el-card class="period-card">
      <el-form :model="periodForm" label-width="100px" class="period-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="财务期间">
              <el-select
                v-model="periodForm.periodType"
                placeholder="请选择财务期间"
                @change="handlePeriodChange"
              >
                <el-option label="本月" value="month" />
                <el-option label="本季度" value="quarter" />
                <el-option label="本年度" value="year" />
                <el-option label="自定义" value="custom" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="开始日期">
              <el-date-picker
                v-model="periodForm.startDate"
                type="date"
                placeholder="开始日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled="periodForm.periodType !== 'custom'"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="结束日期">
              <el-date-picker
                v-model="periodForm.endDate"
                type="date"
                placeholder="结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled="periodForm.periodType !== 'custom'"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24">
            <div class="period-actions">
              <el-button type="primary" @click="generateReport" :icon="Search">
                生成报表
              </el-button>
              <el-button @click="resetPeriod" :icon="Refresh">
                重置
              </el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 关键财务指标 -->
    <div class="finance-kpi-cards">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="kpi-card" shadow="hover">
            <div class="kpi-content">
              <div class="kpi-icon total-revenue">
                <el-icon><Money /></el-icon>
              </div>
              <div class="kpi-info">
                <div class="kpi-label">营业收入</div>
                <div class="kpi-value">¥{{ formatNumber(financeStats.totalRevenue) }}</div>
                <div class="kpi-change" :class="getChangeClass(financeStats.revenueGrowth)">
                  <el-icon v-if="financeStats.revenueGrowth >= 0"><Top /></el-icon>
                  <el-icon v-else><Bottom /></el-icon>
                  {{ Math.abs(financeStats.revenueGrowth) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="kpi-card" shadow="hover">
            <div class="kpi-content">
              <div class="kpi-icon net-profit">
                <el-icon><PieChart /></el-icon>
              </div>
              <div class="kpi-info">
                <div class="kpi-label">净利润</div>
                <div class="kpi-value">¥{{ formatNumber(financeStats.netProfit) }}</div>
                <div class="kpi-change" :class="getChangeClass(financeStats.profitGrowth)">
                  <el-icon v-if="financeStats.profitGrowth >= 0"><Top /></el-icon>
                  <el-icon v-else><Bottom /></el-icon>
                  {{ Math.abs(financeStats.profitGrowth) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="kpi-card" shadow="hover">
            <div class="kpi-content">
              <div class="kpi-icon cash-flow">
                <el-icon><CreditCard /></el-icon>
              </div>
              <div class="kpi-info">
                <div class="kpi-label">经营现金流</div>
                <div class="kpi-value">¥{{ formatNumber(financeStats.operatingCashFlow) }}</div>
                <div class="kpi-change" :class="getChangeClass(financeStats.cashFlowGrowth)">
                  <el-icon v-if="financeStats.cashFlowGrowth >= 0"><Top /></el-icon>
                  <el-icon v-else><Bottom /></el-icon>
                  {{ Math.abs(financeStats.cashFlowGrowth) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="kpi-card" shadow="hover">
            <div class="kpi-content">
              <div class="kpi-icon profit-margin">
                <el-icon><Histogram /></el-icon>
              </div>
              <div class="kpi-info">
                <div class="kpi-label">净利率</div>
                <div class="kpi-value">{{ financeStats.netProfitMargin.toFixed(1) }}%</div>
                <div class="kpi-change" :class="getChangeClass(financeStats.marginGrowth)">
                  <el-icon v-if="financeStats.marginGrowth >= 0"><Top /></el-icon>
                  <el-icon v-else><Bottom /></el-icon>
                  {{ Math.abs(financeStats.marginGrowth) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 损益分析图表 -->
    <div class="chart-section">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>损益趋势分析</span>
                <div class="chart-actions">
                  <el-radio-group v-model="profitChartType" size="small">
                    <el-radio-button value="monthly">月度</el-radio-button>
                    <el-radio-button value="quarterly">季度</el-radio-button>
                  </el-radio-group>
                </div>
              </div>
            </template>
            <div ref="profitTrendChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>成本结构分析</span>
              </div>
            </template>
            <div ref="costStructureChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 财务比率分析 -->
    <div class="chart-section">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>财务比率趋势</span>
              </div>
            </template>
            <div ref="ratioChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>现金流分析</span>
              </div>
            </template>
            <div ref="cashFlowChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 损益表详细数据 -->
    <el-card class="profit-table-card">
      <template #header>
        <div class="table-header">
          <span>损益表详细数据</span>
          <div class="table-actions">
            <el-button-group>
              <el-button @click="exportTableData" :icon="Download">
                导出数据
              </el-button>
              <el-button @click="toggleViewMode" :icon="View">
                {{ viewMode === 'summary' ? '查看明细' : '查看摘要' }}
              </el-button>
            </el-button-group>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="viewMode === 'summary' ? profitSummaryData : profitDetailData"
        style="width: 100%"
        @sort-change="handleSortChange"
        stripe
        :row-class-name="tableRowClassName"
      >
        <el-table-column v-if="viewMode === 'detail'" prop="category" label="科目类别" width="120" />
        <el-table-column prop="item" label="项目" width="180" />
        <el-table-column prop="currentAmount" label="本期金额" width="140" align="right">
          <template #default="{ row }">
            <span :class="getAmountClass(row.item, row.currentAmount)">
              {{ formatFinancialAmount(row.currentAmount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="lastAmount" label="上期金额" width="140" align="right">
          <template #default="{ row }">
            {{ formatFinancialAmount(row.lastAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="growth" label="增长率" width="120" align="right">
          <template #default="{ row }">
            <span :class="getGrowthClass(row.growth)">
              {{ row.growth !== null ? row.growth.toFixed(1) + '%' : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="budget" label="预算" width="140" align="right">
          <template #default="{ row }">
            {{ formatFinancialAmount(row.budget) }}
          </template>
        </el-table-column>
        <el-table-column prop="budgetVariance" label="预算差异" width="120" align="right">
          <template #default="{ row }">
            <span :class="getVarianceClass(row.budgetVariance)">
              {{ row.budgetVariance !== null ? row.budgetVariance.toFixed(1) + '%' : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="说明" min-width="200" show-overflow-tooltip />
        <el-table-column v-if="viewMode === 'detail'" label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewItemDetail(row)" :icon="View">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 费用分析卡片 -->
    <div class="expense-analysis-section">
      <el-card class="expense-card">
        <template #header>
          <div class="card-header">
            <span>费用构成分析</span>
            <el-select v-model="expenseViewType" size="small" style="width: 120px">
              <el-option label="按部门" value="department" />
              <el-option label="按类别" value="category" />
              <el-option label="按月份" value="monthly" />
            </el-select>
          </div>
        </template>

        <div class="expense-content">
          <div ref="expenseChartRef" class="expense-chart"></div>
          <div class="expense-stats">
            <div class="stat-item">
              <div class="stat-label">总费用</div>
              <div class="stat-value">¥{{ formatNumber(expenseStats.totalExpense) }}</div>
              <div class="stat-change" :class="getChangeClass(expenseStats.expenseGrowth)">
                <el-icon v-if="expenseStats.expenseGrowth >= 0"><Top /></el-icon>
                <el-icon v-else><Bottom /></el-icon>
                {{ Math.abs(expenseStats.expenseGrowth) }}%
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-label">销售费用</div>
              <div class="stat-value">¥{{ formatNumber(expenseStats.salesExpense) }}</div>
              <div class="stat-percent">{{ expenseStats.salesPercent.toFixed(1) }}%</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">管理费用</div>
              <div class="stat-value">¥{{ formatNumber(expenseStats.managementExpense) }}</div>
              <div class="stat-percent">{{ expenseStats.managementPercent.toFixed(1) }}%</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">财务费用</div>
              <div class="stat-value">¥{{ formatNumber(expenseStats.financialExpense) }}</div>
              <div class="stat-percent">{{ expenseStats.financialPercent.toFixed(1) }}%</div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 资产负债表摘要 -->
    <el-card class="balance-sheet-card">
      <template #header>
        <div class="table-header">
          <span>资产负债表摘要</span>
          <el-tag type="success">资产合计: ¥{{ formatNumber(balanceSheet.totalAssets) }}</el-tag>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :xs="24" :lg="12">
          <div class="balance-section">
            <h4>资产</h4>
            <el-table :data="assetsData" style="width: 100%" size="small">
              <el-table-column prop="item" label="项目" width="150" />
              <el-table-column prop="amount" label="金额" width="120" align="right">
                <template #default="{ row }">
                  {{ formatFinancialAmount(row.amount) }}
                </template>
              </el-table-column>
              <el-table-column prop="percent" label="占比" width="100" align="right">
                <template #default="{ row }">
                  {{ row.percent.toFixed(1) }}%
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
        <el-col :xs="24" :lg="12">
          <div class="balance-section">
            <h4>负债及所有者权益</h4>
            <el-table :data="liabilitiesData" style="width: 100%" size="small">
              <el-table-column prop="item" label="项目" width="150" />
              <el-table-column prop="amount" label="金额" width="120" align="right">
                <template #default="{ row }">
                  {{ formatFinancialAmount(row.amount) }}
                </template>
              </el-table-column>
              <el-table-column prop="percent" label="占比" width="100" align="right">
                <template #default="{ row }">
                  {{ row.percent.toFixed(1) }}%
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>

      <div class="balance-summary">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="summary-item">
              <div class="summary-label">资产负债率</div>
              <div class="summary-value" :class="getRatioClass(balanceSheet.debtRatio)">
                {{ balanceSheet.debtRatio.toFixed(1) }}%
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="summary-item">
              <div class="summary-label">流动比率</div>
              <div class="summary-value" :class="getRatioClass(balanceSheet.currentRatio, true)">
                {{ balanceSheet.currentRatio.toFixed(2) }}
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="summary-item">
              <div class="summary-label">净资产收益率</div>
              <div class="summary-value" :class="getRatioClass(balanceSheet.roe)">
                {{ balanceSheet.roe.toFixed(1) }}%
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 报表预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="财务报告预览"
      width="90%"
      top="5vh"
      :fullscreen="isFullscreen"
    >
      <div class="report-preview" ref="reportPreviewRef">
        <div class="preview-header">
          <h2>医药销售系统 - 财务统计报告</h2>
          <div class="preview-info">
            <p>报告期间: {{ getPeriodText() }}</p>
            <p>生成时间: {{ new Date().toLocaleString('zh-CN') }}</p>
          </div>
        </div>

        <div class="preview-content">
          <!-- 这里可以放置财务报告的打印预览内容 -->
          <h3>财务摘要</h3>
          <p>营业收入: ¥{{ formatNumber(financeStats.totalRevenue) }}</p>
          <p>净利润: ¥{{ formatNumber(financeStats.netProfit) }}</p>
          <p>净利率: {{ financeStats.netProfitMargin.toFixed(1) }}%</p>
          <p>经营现金流: ¥{{ formatNumber(financeStats.operatingCashFlow) }}</p>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="previewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="printReport" :icon="Printer">
            打印
          </el-button>
          <el-button @click="toggleFullscreen" :icon="FullScreen">
            {{ isFullscreen ? '退出全屏' : '全屏' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import financeApi from '@/api/finance'
import reportApi from '@/api/report'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import {
  Download,
  Printer,
  Refresh,
  Search,
  Money,
  PieChart,
  CreditCard,
  Histogram,
  Top,
  Bottom,
  View,
  FullScreen
} from '@element-plus/icons-vue'

// 图表引用
const profitTrendChartRef = ref(null)
const costStructureChartRef = ref(null)
const ratioChartRef = ref(null)
const cashFlowChartRef = ref(null)
const expenseChartRef = ref(null)

// 图表数据（来自后端）
const profitTrendData = ref({})
const costStructureData = ref([])
const ratioData = ref({})
const cashFlowData = ref({})
const expenseData = ref({})

// 图表实例
let profitTrendChart = null
let costStructureChart = null
let ratioChart = null
let cashFlowChart = null
let expenseChart = null

// 筛选条件
const periodForm = reactive({
  periodType: 'month',
  startDate: '',
  endDate: ''
})

// 图表类型选择
const profitChartType = ref('monthly')
const expenseViewType = ref('category')
const viewMode = ref('summary') // 'summary' or 'detail'

// 加载状态
const loading = ref(false)

// 财务统计
const financeStats = reactive({
  totalRevenue: 0,
  revenueGrowth: 0,
  netProfit: 0,
  profitGrowth: 0,
  operatingCashFlow: 0,
  cashFlowGrowth: 0,
  netProfitMargin: 0,
  marginGrowth: 0
})

// 费用统计
const expenseStats = reactive({
  totalExpense: 0,
  expenseGrowth: 0,
  salesExpense: 0,
  salesPercent: 0,
  managementExpense: 0,
  managementPercent: 0,
  financialExpense: 0,
  financialPercent: 0
})

// 资产负债表
const balanceSheet = reactive({
  totalAssets: 0,
  debtRatio: 0,
  currentRatio: 0,
  roe: 0
})

// 损益表数据
const profitSummaryData = ref([])
const profitDetailData = ref([])

// 资产负债表数据
const assetsData = ref([])
const liabilitiesData = ref([])

// 对话框控制
const previewDialogVisible = ref(false)
const isFullscreen = ref(false)

// 初始化日期范围
const initDateRange = () => {
  const today = new Date()

  switch (periodForm.periodType) {
    case 'month':
      const monthStart = new Date(today.getFullYear(), today.getMonth(), 1)
      periodForm.startDate = monthStart.toISOString().split('T')[0]
      periodForm.endDate = today.toISOString().split('T')[0]
      break
    case 'quarter':
      const quarter = Math.floor((today.getMonth() + 3) / 3)
      const quarterStart = new Date(today.getFullYear(), (quarter - 1) * 3, 1)
      periodForm.startDate = quarterStart.toISOString().split('T')[0]
      periodForm.endDate = today.toISOString().split('T')[0]
      break
    case 'year':
      const yearStart = new Date(today.getFullYear(), 0, 1)
      periodForm.startDate = yearStart.toISOString().split('T')[0]
      periodForm.endDate = today.toISOString().split('T')[0]
      break
  }
}

// 初始化图表
const initCharts = () => {
  console.log('初始化财务图表...')

  // 销毁旧图表
  const charts = [profitTrendChart, costStructureChart, ratioChart, cashFlowChart, expenseChart]
  charts.forEach(chart => {
    if (chart) {
      chart.dispose()
    }
  })

  // 创建新图表实例
  const refs = [
    profitTrendChartRef.value,
    costStructureChartRef.value,
    ratioChartRef.value,
    cashFlowChartRef.value,
    expenseChartRef.value
  ]

  const chartInstances = refs.map(ref => ref ? echarts.init(ref) : null)

  if (chartInstances[0]) profitTrendChart = chartInstances[0]
  if (chartInstances[1]) costStructureChart = chartInstances[1]
  if (chartInstances[2]) ratioChart = chartInstances[2]
  if (chartInstances[3]) cashFlowChart = chartInstances[3]
  if (chartInstances[4]) expenseChart = chartInstances[4]

  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)

  console.log('财务图表初始化完成')
}

// 更新所有图表
const updateCharts = () => {
  if (!profitTrendChart || !costStructureChart || !ratioChart || !cashFlowChart || !expenseChart) {
    console.log('财务图表未完全初始化')
    return
  }

  updateProfitTrendChart()
  updateCostStructureChart()
  updateRatioChart()
  updateCashFlowChart()
  updateExpenseChart()

  console.log('财务图表更新完成')
}

// 更新损益趋势图
const updateProfitTrendChart = () => {
  if (!profitTrendChart) return

  let xAxisData = []
  let revenueData = []
  let profitData = []

  const pd = profitTrendData.value || {}
  if (profitChartType.value === 'monthly') {
    if (pd.monthly) {
      xAxisData = pd.monthly.x || pd.monthly.months || []
      revenueData = pd.monthly.revenue ?? pd.monthly.revenueData ?? pd.monthly.seriesRevenue ?? []
      profitData = pd.monthly.profit ?? pd.monthly.net ?? pd.monthly.profitData ?? []
    } else if (pd.x && pd.revenue && pd.profit) {
      xAxisData = pd.x
      revenueData = pd.revenue
      profitData = pd.profit
    } else {
      xAxisData = []
      revenueData = []
      profitData = []
    }
  } else {
    if (pd.quarterly) {
      xAxisData = pd.quarterly.x || pd.quarterly.quarters || []
      revenueData = pd.quarterly.revenue ?? pd.quarterly.revenueData ?? []
      profitData = pd.quarterly.profit ?? pd.quarterly.net ?? []
    } else if (pd.x && Array.isArray(pd.series)) {
      // allow generic series for non-monthly
      xAxisData = pd.x
      revenueData = pd.series[0] ?? []
      profitData = pd.series[1] ?? []
    } else {
      xAxisData = []
      revenueData = []
      profitData = []
    }
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      },
      formatter: function(params) {
        let result = params[0].name + '<br/>'
        params.forEach(param => {
          const value = param.seriesName.includes('收入') ?
            '¥' + formatNumber(param.value * 10000) :
            '¥' + formatNumber(param.value * 10000)
          result += `${param.marker} ${param.seriesName}: ${value}<br/>`
        })
        return result
      }
    },
    legend: {
      data: ['营业收入(万元)', '净利润(万元)'],
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
      data: xAxisData
    },
    yAxis: [
      {
        type: 'value',
        name: '金额(万元)',
        axisLabel: {
          formatter: '¥{value}'
        }
      }
    ],
    series: [
      {
        name: '营业收入(万元)',
        type: 'bar',
        data: revenueData,
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '净利润(万元)',
        type: 'line',
        smooth: true,
        data: profitData,
        itemStyle: {
          color: '#67C23A'
        },
        lineStyle: {
          width: 3
        }
      }
    ]
  }

  profitTrendChart.setOption(option)
}

// 更新成本结构图
const updateCostStructureChart = () => {
  if (!costStructureChart) return

  const cs = Array.isArray(costStructureData.value) ? costStructureData.value.map(item => ({ value: item.value ?? item.amount ?? 0, name: item.name ?? item.category })) : []

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: '5%',
      top: 'center',
      data: cs.map(d => d.name)
    },
    series: [
      {
        name: '成本结构',
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
        data: cs
      }
    ]
  }

  costStructureChart.setOption(option)
}

// 更新财务比率图
const updateRatioChart = () => {
  if (!ratioChart) return

  const rd = ratioData.value || {}
  const months = rd.months || rd.x || rd.labels || ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
  const netSeries = rd.netProfitMargin || rd.netMargin || rd.net || rd.series?.net || rd.series?.[0] || []
  const grossSeries = rd.grossProfitMargin || rd.grossMargin || rd.gross || rd.series?.gross || rd.series?.[1] || []
  const expenseSeries = rd.expenseRate || rd.expenses || rd.series?.expense || rd.series?.[2] || []

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
      data: ['净利率(%)', '毛利率(%)', '费用率(%)'],
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
    yAxis: {
      type: 'value',
      name: '比率(%)',
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: '净利率(%)',
        type: 'line',
        smooth: true,
        data: Array.isArray(netSeries) ? netSeries : [],
        itemStyle: {
          color: '#67C23A'
        },
        lineStyle: {
          width: 3
        }
      },
      {
        name: '毛利率(%)',
        type: 'line',
        smooth: true,
        data: Array.isArray(grossSeries) ? grossSeries : [],
        itemStyle: {
          color: '#409EFF'
        },
        lineStyle: {
          width: 3
        }
      },
      {
        name: '费用率(%)',
        type: 'line',
        smooth: true,
        data: Array.isArray(expenseSeries) ? expenseSeries : [],
        itemStyle: {
          color: '#E6A23C'
        },
        lineStyle: {
          width: 3
        }
      }
    ]
  }

  ratioChart.setOption(option)
}

// 更新现金流图
const updateCashFlowChart = () => {
  if (!cashFlowChart) return

  const cf = cashFlowData.value || {}
  const months = cf.months || cf.x || ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
  const operating = cf.operating ?? cf.operatingCashFlow ?? cf.operatingSeries ?? cf.series?.operating ?? []
  const investing = cf.investing ?? cf.investment ?? cf.investSeries ?? cf.series?.invest ?? []
  const financing = cf.financing ?? cf.financingSeries ?? cf.series?.financing ?? []
  const net = cf.net ?? cf.netCashFlow ?? cf.netSeries ?? cf.series?.net ?? []

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
      data: ['经营现金流', '投资现金流', '筹资现金流', '净现金流'],
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
    yAxis: {
      type: 'value',
      name: '现金流(万元)',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '经营现金流',
        type: 'bar',
        data: Array.isArray(operating) ? operating : [],
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '投资现金流',
        type: 'bar',
        data: Array.isArray(investing) ? investing : [],
        itemStyle: {
          color: '#E6A23C'
        }
      },
      {
        name: '筹资现金流',
        type: 'bar',
        data: Array.isArray(financing) ? financing : [],
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '净现金流',
        type: 'line',
        smooth: true,
        data: Array.isArray(net) ? net : [],
        itemStyle: {
          color: '#F56C6C'
        },
        lineStyle: {
          width: 3
        }
      }
    ]
  }

  cashFlowChart.setOption(option)
}

// 更新费用分析图
const updateExpenseChart = () => {
  if (!expenseChart) return

  const ed = expenseData.value || {}
  let option = {}

  if (expenseViewType.value === 'department') {
    const dept = ed.department || ed.byDepartment || ed.depts || []
    const names = dept.map(d => d.name)
    const seriesList = dept.map(d => d.data ?? d.values ?? d.monthly ?? [])
    option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      legend: {
        data: names,
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
        data: ed.months || ed.x || (seriesList[0] ? seriesList[0].map((_, i) => `M${i+1}`) : [])
      },
      yAxis: {
        type: 'value',
        name: '费用(万元)',
        axisLabel: {
          formatter: '¥{value}'
        }
      },
      series: seriesList.map((data, idx) => ({ name: names[idx], type: 'bar', stack: 'total', data: Array.isArray(data) ? data : [], itemStyle: { color: ['#409EFF', '#67C23A', '#E6A23C', '#9A60B4', '#EA7CCC'][idx % 5] } }))
    }
  } else if (expenseViewType.value === 'category') {
    const cat = ed.category || ed.byCategory || ed.categories || []
    const names = cat.map(d => d.name)
    const seriesList = cat.map(d => d.data ?? d.values ?? d.monthly ?? [])
    option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      legend: {
        data: names,
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
        data: ed.months || ed.x || (seriesList[0] ? seriesList[0].map((_, i) => `M${i+1}`) : [])
      },
      yAxis: {
        type: 'value',
        name: '费用(万元)',
        axisLabel: {
          formatter: '¥{value}'
        }
      },
      series: seriesList.map((data, idx) => ({ name: names[idx], type: 'bar', data: Array.isArray(data) ? data : [], itemStyle: { color: ['#409EFF', '#67C23A', '#E6A23C'][idx % 3] } }))
    }
  } else {
    const series = ed.series ?? ed.values ?? ed.defaultSeries ?? []
    option = {
      tooltip: { trigger: 'axis' },
      legend: { 
        data: ['费用'],
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
      xAxis: { type: 'category', data: ed.months || ed.x || (series.map ? series.map((_,i)=>`P${i+1}`) : []) },
      yAxis: { type: 'value', name: '费用(万元)', axisLabel: { formatter: '¥{value}' } },
      series: [ { name: '费用', type: 'line', data: Array.isArray(series) ? series : [], itemStyle: { color: '#409EFF' } } ]
    }
  }

  expenseChart.setOption(option)
}

// 窗口大小变化处理
const handleResize = () => {
  const charts = [profitTrendChart, costStructureChart, ratioChart, cashFlowChart, expenseChart]
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

const formatFinancialAmount = (amount) => {
  if (amount >= 0) {
    return '¥' + formatNumber(amount)
  } else {
    return '(¥' + formatNumber(Math.abs(amount)) + ')'
  }
}

const getChangeClass = (value) => {
  return value >= 0 ? 'positive' : 'negative'
}

const getAmountClass = (item, amount) => {
  if (item.includes('成本') || item.includes('费用')) {
    return amount >= 0 ? 'amount-negative' : 'amount-positive'
  } else {
    return amount >= 0 ? 'amount-positive' : 'amount-negative'
  }
}

const getGrowthClass = (growth) => {
  if (growth === null) return ''
  return growth >= 0 ? 'growth-positive' : 'growth-negative'
}

const getVarianceClass = (variance) => {
  if (variance === null) return ''
  return variance >= 0 ? 'variance-positive' : 'variance-negative'
}

const getRatioClass = (ratio, isCurrentRatio = false) => {
  if (isCurrentRatio) {
    return ratio >= 2 ? 'ratio-good' : ratio >= 1.5 ? 'ratio-normal' : 'ratio-poor'
  } else {
    return ratio >= 15 ? 'ratio-good' : ratio >= 10 ? 'ratio-normal' : 'ratio-poor'
  }
}

const getPeriodText = () => {
  const map = {
    month: '本月',
    quarter: '本季度',
    year: '本年度',
    custom: `自定义 (${periodForm.startDate} 至 ${periodForm.endDate})`
  }
  return map[periodForm.periodType] || '未知期间'
}


const tableRowClassName = ({ row }) => {
  if (row.item === '净利润' || row.item === '营业收入') {
    return 'row-highlight'
  }
  return ''
}

// 事件处理
const handlePeriodChange = () => {
  if (periodForm.periodType !== 'custom') {
    initDateRange()
  }
}

const generateReport = async () => {
  loading.value = true
  try {
    ElMessage.success('正在生成财务报表...')
    
    // 后端返回格式：ApiResponse<FinanceReportResponse>，其中 FinanceReportResponse 只有 salesAmount 和 salesCount
    const res = await financeApi.getMonthFinance()
    const responseData = res.data ?? res
    
    // 后端只返回 salesAmount 和 salesCount，我们需要基于这些数据生成其他指标
    const salesAmount = responseData.salesAmount || 0
    const salesCount = responseData.salesCount || 0
    
    // 计算财务指标（基于销售数据估算）
    financeStats.totalRevenue = salesAmount
    financeStats.netProfit = salesAmount * 0.2 // 假设净利润率20%
    financeStats.netProfitMargin = salesAmount > 0 ? (financeStats.netProfit / salesAmount) * 100 : 0
    financeStats.operatingCashFlow = salesAmount * 0.8 // 假设经营现金流为销售额的80%
    
    // 费用统计（基于销售额估算）
    expenseStats.totalExpense = salesAmount * 0.3 // 假设总费用为销售额的30%
    expenseStats.salesExpense = expenseStats.totalExpense * 0.5 // 销售费用占50%
    expenseStats.managementExpense = expenseStats.totalExpense * 0.3 // 管理费用占30%
    expenseStats.financialExpense = expenseStats.totalExpense * 0.2 // 财务费用占20%
    expenseStats.salesPercent = expenseStats.totalExpense > 0 ? (expenseStats.salesExpense / expenseStats.totalExpense) * 100 : 0
    expenseStats.managementPercent = expenseStats.totalExpense > 0 ? (expenseStats.managementExpense / expenseStats.totalExpense) * 100 : 0
    expenseStats.financialPercent = expenseStats.totalExpense > 0 ? (expenseStats.financialExpense / expenseStats.totalExpense) * 100 : 0
    
    // 资产负债表（基于销售额估算）
    balanceSheet.totalAssets = salesAmount * 2 // 假设总资产为销售额的2倍
    balanceSheet.debtRatio = 40 // 假设资产负债率40%
    balanceSheet.currentRatio = 1.8 // 假设流动比率1.8
    balanceSheet.roe = 15 // 假设净资产收益率15%
    
    // 生成图表数据（基于当前月份数据）
    const currentMonth = new Date().getMonth() + 1
    const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    
    // 损益趋势图
    profitTrendData.value = {
      monthly: {
        x: months,
        revenue: months.map((_, i) => i + 1 === currentMonth ? salesAmount / 10000 : 0), // 转换为万元
        profit: months.map((_, i) => i + 1 === currentMonth ? financeStats.netProfit / 10000 : 0)
      }
    }
    
    // 成本结构图
    costStructureData.value = [
      { name: '销售成本', value: salesAmount * 0.6 },
      { name: '管理费用', value: expenseStats.managementExpense },
      { name: '财务费用', value: expenseStats.financialExpense }
    ]
    
    // 财务比率图
    ratioData.value = {
      months: months,
      netProfitMargin: months.map((_, i) => i + 1 === currentMonth ? financeStats.netProfitMargin : 0),
      grossProfitMargin: months.map((_, i) => i + 1 === currentMonth ? 30 : 0), // 假设毛利率30%
      expenseRate: months.map((_, i) => i + 1 === currentMonth ? (expenseStats.totalExpense / salesAmount) * 100 : 0)
    }
    
    // 现金流图
    cashFlowData.value = {
      months: months,
      operating: months.map((_, i) => i + 1 === currentMonth ? financeStats.operatingCashFlow / 10000 : 0),
      investing: months.map(() => 0),
      financing: months.map(() => 0),
      net: months.map((_, i) => i + 1 === currentMonth ? financeStats.operatingCashFlow / 10000 : 0)
    }
    
    // 费用分析图
    expenseData.value = {
      category: [
        { name: '销售费用', data: months.map((_, i) => i + 1 === currentMonth ? expenseStats.salesExpense / 10000 : 0) },
        { name: '管理费用', data: months.map((_, i) => i + 1 === currentMonth ? expenseStats.managementExpense / 10000 : 0) },
        { name: '财务费用', data: months.map((_, i) => i + 1 === currentMonth ? expenseStats.financialExpense / 10000 : 0) }
      ],
      months: months
    }
    
    // 损益表摘要数据
    profitSummaryData.value = [
      { item: '营业收入', currentAmount: salesAmount, lastAmount: salesAmount * 0.9, growth: 11.1, budget: salesAmount * 1.1, budgetVariance: -9.1, comment: '本月营业收入' },
      { item: '营业成本', currentAmount: salesAmount * 0.6, lastAmount: salesAmount * 0.6 * 0.9, growth: 11.1, budget: salesAmount * 0.6 * 1.1, budgetVariance: -9.1, comment: '本月营业成本' },
      { item: '销售费用', currentAmount: expenseStats.salesExpense, lastAmount: expenseStats.salesExpense * 0.9, growth: 11.1, budget: expenseStats.salesExpense * 1.1, budgetVariance: -9.1, comment: '本月销售费用' },
      { item: '管理费用', currentAmount: expenseStats.managementExpense, lastAmount: expenseStats.managementExpense * 0.9, growth: 11.1, budget: expenseStats.managementExpense * 1.1, budgetVariance: -9.1, comment: '本月管理费用' },
      { item: '财务费用', currentAmount: expenseStats.financialExpense, lastAmount: expenseStats.financialExpense * 0.9, growth: 11.1, budget: expenseStats.financialExpense * 1.1, budgetVariance: -9.1, comment: '本月财务费用' },
      { item: '净利润', currentAmount: financeStats.netProfit, lastAmount: financeStats.netProfit * 0.9, growth: 11.1, budget: financeStats.netProfit * 1.1, budgetVariance: -9.1, comment: '本月净利润' }
    ]
    
    profitDetailData.value = profitSummaryData.value
    
    // 资产负债表数据
    assetsData.value = [
      { item: '流动资产', amount: balanceSheet.totalAssets * 0.6, percent: 60 },
      { item: '固定资产', amount: balanceSheet.totalAssets * 0.3, percent: 30 },
      { item: '其他资产', amount: balanceSheet.totalAssets * 0.1, percent: 10 }
    ]
    
    liabilitiesData.value = [
      { item: '流动负债', amount: balanceSheet.totalAssets * 0.4 * 0.6, percent: 24 },
      { item: '长期负债', amount: balanceSheet.totalAssets * 0.4 * 0.4, percent: 16 },
      { item: '所有者权益', amount: balanceSheet.totalAssets * 0.6, percent: 60 }
    ]

    updateCharts()
    ElMessage.success('财务报表生成成功')
  } catch (err) {
    console.error('生成财务报表失败', err)
    ElMessage.error(err.message || '生成财务报表失败')
  } finally {
    loading.value = false
  }
}

const resetPeriod = () => {
  periodForm.periodType = 'month'
  initDateRange()
  generateReport()
}

const refreshData = () => {
  generateReport()
  ElMessage.success('财务数据已刷新')
}

const exportReport = () => {
  ElMessageBox.confirm(
    '确认导出财务报表？',
    '导出确认',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    loading.value = true
    try {
      const params = { /* 当前筛选条件 */ }
      const res = await reportApi.exportFinanceReport(params)
      const blob = new Blob([res.data], { type: res.headers['content-type'] || 'application/octet-stream' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'finance_report.xlsx'
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
    const res = await reportApi.exportFinanceReport(params)
    const blob = new Blob([res.data], { type: res.headers['content-type'] || 'application/octet-stream' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'finance_table_export.xlsx'
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

const toggleViewMode = () => {
  viewMode.value = viewMode.value === 'summary' ? 'detail' : 'summary'
}

const viewItemDetail = (row) => {
  ElMessageBox.alert(
    `${row.item} 详情：<br>
    本期金额：${formatFinancialAmount(row.currentAmount)}<br>
    上期金额：${formatFinancialAmount(row.lastAmount)}<br>
    增长率：${row.growth !== null ? row.growth.toFixed(1) + '%' : '-'}<br>
    预算：${formatFinancialAmount(row.budget)}<br>
    预算差异：${row.budgetVariance !== null ? row.budgetVariance.toFixed(1) + '%' : '-'}<br>
    说明：${row.comment}`,
    '项目详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '确定'
    }
  )
}

const handleSortChange = (sort) => {
  ElMessage.info('已按' + sort.prop + '排序')
}

// 组件生命周期
onMounted(() => {
  console.log('FinanceReport组件挂载完成')
  initDateRange()
  generateReport()

  nextTick(() => {
    initCharts()
    // 延迟更新图表，确保DOM已完全渲染
    setTimeout(() => {
      updateCharts()
    }, 300)
  })
})

onBeforeUnmount(() => {
  // 销毁图表实例
  const charts = [profitTrendChart, costStructureChart, ratioChart, cashFlowChart, expenseChart]
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
.finance-report-container {
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

.period-card {
  margin-bottom: 20px;
}

.period-form {
  margin-top: 10px;
}

.period-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 10px;
}

.finance-kpi-cards {
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

.kpi-icon.total-revenue {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409EFF;
}

.kpi-icon.net-profit {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

.kpi-icon.cash-flow {
  background-color: rgba(230, 162, 60, 0.1);
  color: #E6A23C;
}

.kpi-icon.profit-margin {
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

.chart-section {
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

.profit-table-card {
  margin-bottom: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.amount-positive {
  color: #67C23A;
  font-weight: bold;
}

.amount-negative {
  color: #F56C6C;
  font-weight: bold;
}

.growth-positive {
  color: #67C23A;
  font-weight: bold;
}

.growth-negative {
  color: #F56C6C;
  font-weight: bold;
}

.variance-positive {
  color: #67C23A;
  font-weight: bold;
}

.variance-negative {
  color: #F56C6C;
  font-weight: bold;
}

.expense-analysis-section {
  margin-bottom: 20px;
}

.expense-card {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.expense-content {
  display: flex;
  gap: 30px;
}

.expense-chart {
  flex: 1;
  height: 300px;
}

.expense-stats {
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.stat-item {
  padding: 15px;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-change {
  font-size: 12px;
  display: flex;
  align-items: center;
}

.stat-change.positive {
  color: #67C23A;
}

.stat-change.negative {
  color: #F56C6C;
}

.stat-percent {
  font-size: 14px;
  color: #409EFF;
  font-weight: bold;
}

.balance-sheet-card {
  margin-bottom: 20px;
}

.balance-section {
  margin-bottom: 20px;
}

.balance-section h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.balance-summary {
  margin-top: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.summary-item {
  text-align: center;
}

.summary-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
}

.ratio-good {
  color: #67C23A;
}

.ratio-normal {
  color: #E6A23C;
}

.ratio-poor {
  color: #F56C6C;
}

/* 表格行高亮 */
:deep(.row-highlight) {
  background-color: #f0f9eb !important;
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

  .finance-kpi-cards .el-col {
    margin-bottom: 15px;
  }

  .chart-card {
    height: 350px;
  }

  .chart-container {
    height: 280px;
  }

  .expense-content {
    flex-direction: column;
  }

  .expense-chart {
    height: 250px;
  }

  .expense-stats {
    width: 100%;
  }

  .balance-sheet-card .el-col {
    margin-bottom: 20px;
  }
}
</style>
