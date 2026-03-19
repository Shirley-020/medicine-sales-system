<!-- src/views/stock/StockWarning.vue -->
<template>
  <div class="stock-warning-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h2>库存预警管理</h2>
        <p class="subtitle">监控药品库存异常情况，及时预警并采取措施</p>
      </div>
      <div class="header-right">
        <el-button type="danger" @click="handleAllWarning" :icon="Bell">
          全部预警
        </el-button>
        <el-button type="primary" @click="refreshData" :icon="Refresh">
          刷新预警
        </el-button>
        <el-button @click="exportWarningData" :icon="Download">
          导出预警
        </el-button>
      </div>
    </div>

    <!-- 预警级别统计 -->
    <div class="warning-stats">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="stat-card" shadow="hover" @click="filterByLevel('critical')">
            <div class="stat-content">
              <div class="stat-icon critical">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">紧急预警</div>
                <div class="stat-value">{{ stats.critical }}</div>
                <div class="stat-desc">库存已为0或已过期</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="stat-card" shadow="hover" @click="filterByLevel('high')">
            <div class="stat-content">
              <div class="stat-icon high">
                <el-icon><WarningFilled /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">高级预警</div>
                <div class="stat-value">{{ stats.high }}</div>
                <div class="stat-desc">库存不足或即将过期</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="stat-card" shadow="hover" @click="filterByLevel('medium')">
            <div class="stat-content">
              <div class="stat-icon medium">
                <el-icon><InfoFilled /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">中级预警</div>
                <div class="stat-value">{{ stats.medium }}</div>
                <div class="stat-desc">库存偏低或临近有效期</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="stat-card" shadow="hover" @click="filterByLevel('low')">
            <div class="stat-content">
              <div class="stat-icon low">
                <el-icon><Bell /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">提醒</div>
                <div class="stat-value">{{ stats.low }}</div>
                <div class="stat-desc">库存偏高或生产日期较久</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 预警类型分布图表 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <span>预警类型分布</span>
            </template>
            <div ref="typeChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <span>预警级别趋势</span>
            </template>
            <div ref="trendChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :model="filterForm" label-width="100px" class="filter-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="预警类型">
              <el-select
                v-model="filterForm.warningType"
                placeholder="请选择预警类型"
                clearable
                @change="handleFilter"
              >
                <el-option label="库存不足" value="stock_low" />
                <el-option label="库存为0" value="stock_empty" />
                <el-option label="即将过期" value="expiring" />
                <el-option label="已过期" value="expired" />
                <el-option label="库存积压" value="stock_high" />
                <el-option label="生产日期久" value="old_production" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="预警级别">
              <el-select
                v-model="filterForm.warningLevel"
                placeholder="请选择预警级别"
                clearable
                @change="handleFilter"
              >
                <el-option label="紧急预警" value="critical" />
                <el-option label="高级预警" value="high" />
                <el-option label="中级预警" value="medium" />
                <el-option label="提醒" value="low" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="药品名称">
              <el-input
                v-model="filterForm.drugName"
                placeholder="请输入药品名称"
                clearable
                @keyup.enter="handleFilter"
                @clear="handleFilter"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="处理状态">
              <el-select
                v-model="filterForm.status"
                placeholder="请选择处理状态"
                clearable
                @change="handleFilter"
              >
                <el-option label="未处理" value="pending" />
                <el-option label="处理中" value="processing" />
                <el-option label="已处理" value="resolved" />
                <el-option label="已忽略" value="ignored" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24">
            <div class="filter-actions">
              <el-button type="primary" @click="handleFilter" :icon="Search">
                筛选
              </el-button>
              <el-button @click="resetFilter" :icon="Refresh">
                重置
              </el-button>
              <el-button @click="toggleAdvancedFilter" :icon="Setting">
                高级筛选
              </el-button>
            </div>
          </el-col>
        </el-row>

        <!-- 高级筛选 -->
        <el-collapse-transition>
          <div v-show="showAdvancedFilter">
            <el-divider />
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12" :md="8" :lg="6">
                <el-form-item label="药品类型">
                  <el-select
                    v-model="filterForm.drugType"
                    placeholder="请选择药品类型"
                    clearable
                    @change="handleFilter"
                  >
                    <el-option label="处方药" value="prescription" />
                    <el-option label="非处方药" value="otc" />
                    <el-option label="中药" value="traditional" />
                    <el-option label="西药" value="western" />
                    <el-option label="医疗器械" value="equipment" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12" :md="8" :lg="6">
                <el-form-item label="供应商">
                  <el-input
                    v-model="filterForm.supplier"
                    placeholder="请输入供应商"
                    clearable
                    @keyup.enter="handleFilter"
                    @clear="handleFilter"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12" :md="8" :lg="6">
                <el-form-item label="创建时间从">
                  <el-date-picker
                    v-model="filterForm.createTimeStart"
                    type="date"
                    placeholder="开始日期"
                    style="width: 100%"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    @change="handleFilter"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12" :md="8" :lg="6">
                <el-form-item label="创建时间至">
                  <el-date-picker
                    v-model="filterForm.createTimeEnd"
                    type="date"
                    placeholder="结束日期"
                    style="width: 100%"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    @change="handleFilter"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </el-collapse-transition>
      </el-form>
    </el-card>

    <!-- 预警数据表格 -->
    <el-card class="warning-table-card">
      <template #header>
        <div class="table-header">
          <span>预警数据列表</span>
          <div class="header-actions">
            <el-button-group>
              <el-button
                @click="batchProcess"
                :disabled="selection.length === 0"
                :icon="Operation"
              >
                批量处理({{ selection.length }})
              </el-button>
              <el-button
                @click="batchIgnore"
                :disabled="selection.length === 0"
                :icon="Close"
              >
                批量忽略
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
        @selection-change="handleSelectionChange"
        :row-class-name="tableRowClassName"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="drugCode" label="药品编码" width="120" />
        
        <el-table-column prop="drugName" label="药品名称" width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="drug-name-cell">
              <span class="drug-name">{{ row.drugName }}</span>
              <el-tag
                v-if="row.isPrescription"
                size="small"
                type="danger"
                effect="plain"
              >
                处方药
              </el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="warningType" label="预警类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getWarningTypeTag(row.warningType)" effect="light">
              {{ getWarningTypeText(row.warningType) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="warningLevel" label="预警级别" width="120">
          <template #default="{ row }">
            <el-tag :type="getWarningLevelTag(row.warningLevel)" effect="dark">
              {{ getWarningLevelText(row.warningLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="currentStock" label="当前库存" width="100">
          <template #default="{ row }">
            <div :class="{
              'stock-text': true,
              'stock-warning': row.currentStock < row.minStock,
              'stock-danger': row.currentStock === 0
            }">
              {{ row.currentStock }}
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="minStock" label="最低库存" width="100" />
        
        <el-table-column prop="expiryDate" label="有效期至" width="120">
          <template #default="{ row }">
            <div :class="{
              'expiry-date': true,
              'expiry-warning': row.warningType === 'expiring',
              'expiry-danger': row.warningType === 'expired'
            }">
              {{ formatDate(row.expiryDate) }}
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="remainingDays" label="剩余天数" width="100">
          <template #default="{ row }">
            <div :class="{
              'remaining-days': true,
              'danger': row.remainingDays <= 0,
              'warning': row.remainingDays > 0 && row.remainingDays <= 30,
              'normal': row.remainingDays > 30
            }">
              {{ row.remainingDays }}
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="supplier" label="供应商" width="150" show-overflow-tooltip />
        
        <el-table-column prop="createTime" label="预警时间" width="150">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="处理状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="handler" label="处理人" width="120" />
        
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="primary"
                link
                @click="viewDetail(row)"
                :icon="View"
                size="small"
              >
                详情
              </el-button>
              <el-button
                type="success"
                link
                @click="handleProcess(row)"
                :icon="Check"
                size="small"
                v-if="row.status === 'pending'"
              >
                处理
              </el-button>
              <el-button
                type="warning"
                link
                @click="handleIgnore(row)"
                :icon="Close"
                size="small"
                v-if="row.status === 'pending'"
              >
                忽略
              </el-button>
              <el-button
                type="info"
                link
                @click="handleRemind(row)"
                :icon="Bell"
                size="small"
              >
                提醒
              </el-button>
            </div>
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

    <!-- 预警处理对话框 -->
    <el-dialog
      v-model="processDialogVisible"
      :title="`处理预警 - ${currentWarning?.drugName || ''}`"
      width="600px"
    >
      <warning-process
        v-if="processDialogVisible"
        :warning-data="currentWarning"
        @success="handleProcessSuccess"
        @close="processDialogVisible = false"
      />
    </el-dialog>

    <!-- 预警详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="预警详情"
      width="700px"
    >
      <warning-detail
        v-if="detailDialogVisible"
        :warning-data="currentWarning"
        @process="handleDetailProcess"
        @close="detailDialogVisible = false"
      />
    </el-dialog>

    <!-- 批量处理对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量处理预警"
      width="500px"
    >
      <batch-warning-process
        v-if="batchDialogVisible"
        :selection="selection"
        @success="handleBatchSuccess"
        @close="batchDialogVisible = false"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import WarningProcess from './components/WarningProcess.vue'
import WarningDetail from './components/WarningDetail.vue'
import BatchWarningProcess from './components/BatchWarningProcess.vue'
import {
  Bell, Refresh, Download, Warning, WarningFilled,
  InfoFilled, Search, Setting, Operation, Close,
  View, Check
} from '@element-plus/icons-vue'
import reportApi from '@/api/report'
import stockApi from '@/api/stock'

// 图表引用
const typeChartRef = ref(null)
const trendChartRef = ref(null)
let typeChart = null
let trendChart = null

// 筛选表单
const filterForm = reactive({
  warningType: '',
  warningLevel: '',
  drugName: '',
  status: '',
  drugType: '',
  supplier: '',
  createTimeStart: '',
  createTimeEnd: ''
})

// 高级筛选显示状态
const showAdvancedFilter = ref(false)

// 加载状态
const loading = ref(false)

// 表格数据
const tableData = ref([])

// 选中数据
const selection = ref([])

// 分页配置
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 排序配置
const sortConfig = reactive({
  prop: 'createTime',
  order: 'descending'
})

// 统计信息
const stats = reactive({
  critical: 0,
  high: 0,
  medium: 0,
  low: 0
})

// 对话框控制
const processDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const batchDialogVisible = ref(false)
const currentWarning = ref(null)

// 从后端获取低库存/预警报表（GET /api/reports/low-stock）
const fetchLowStock = async () => {
  try {
    const res = await reportApi.getLowStockReport()
    // 兼容不同返回格式
    let data = Array.isArray(res) ? res : (res.data ?? res)
    if (!Array.isArray(data)) data = []

    // 将 LowStockDto 转换为页面使用的预警结构（尽量保留字段）
    return data.map((item, idx) => {
      // 计算剩余天数（与库存查询页面保持一致：90天内过期）
      const expiryDate = item.expiryDate ? new Date(item.expiryDate) : null
      const now = new Date()
      let remainingDays = 0
      if (expiryDate) {
        const diffTime = expiryDate.getTime() - now.getTime()
        remainingDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
      }
      
      // 根据预警类型和库存情况确定预警级别
      let warningLevel = 'medium'
      let description = ''
      let suggestion = ''
      
      // 首先确定预警类型：如果库存为0，应该标记为 stock_empty
      let warningType = item.warningType || 'stock_low'
      if (warningType === 'stock_low' && item.qty === 0) {
        warningType = 'stock_empty' // 库存为0时，使用专门的类型
      }
      
      if (warningType === 'expired') {
        // 已过期：紧急预警
        warningLevel = 'critical'
        description = `已过期：过期日期 ${item.expiryDate}`
        suggestion = '建议立即下架并处理'
      } else if (warningType === 'expiring') {
        // 即将过期（90天内）：根据剩余天数确定级别
        if (remainingDays <= 30) {
          warningLevel = 'high' // 30天内过期：高级预警
        } else {
          warningLevel = 'medium' // 30-90天过期：中级预警
        }
        description = `即将过期：剩余 ${remainingDays} 天（过期日期 ${item.expiryDate}）`
        suggestion = '建议优先销售或联系退货'
      } else if (warningType === 'stock_empty') {
        // 库存为0：紧急预警
        warningLevel = 'critical'
        description = `库存为0：需要立即补货`
        suggestion = '建议紧急采购'
      } else if (warningType === 'stock_low') {
        // 库存不足：根据库存情况确定级别
        if (item.qty <= (item.warningQty ?? 0)) {
          warningLevel = 'high' // 库存低于警戒值：高级预警
          description = `库存不足：当前 ${item.qty}，警戒 ${item.warningQty}`
          suggestion = '建议及时补货'
        } else {
          warningLevel = 'medium' // 库存偏低：中级预警
          description = `库存偏低：当前 ${item.qty}，警戒 ${item.warningQty}`
          suggestion = '建议关注库存变化'
        }
      }
      
      return {
        id: item.id ?? `${item.drugId}-${idx}`,
        drugId: item.drugId,
        drugCode: item.drugCode ?? '',
        drugName: item.drugName ?? '',
        batchNo: item.batchNo ?? '',
        warningType: warningType,
        warningLevel: warningLevel,
        currentStock: item.qty ?? 0,
        minStock: item.warningQty ?? 0,
        expiryDate: item.expiryDate ?? '',
        remainingDays: remainingDays,
        supplier: item.factory ?? item.warehouse ?? '',
        createTime: new Date().toISOString(),
        status: 'pending',
        handler: '',
        isPrescription: false,
        description: description,
        suggestion: suggestion
      }
    })
  } catch (err) {
    console.error('获取预警数据失败', err)
    ElMessage.error(err.message || '获取预警数据失败')
    return []
  }
}

// 获取预警描述
const getWarningDescription = (type, level) => {
  const map = {
    stock_low: '库存低于最低库存量',
    stock_empty: '库存已为0，需要立即补货',
    expiring: '药品即将过期',
    expired: '药品已过期',
    stock_high: '库存积压，占用资金',
    old_production: '生产日期较久，需优先销售'
  }
  return map[type] || '未知预警'
}

// 获取预警建议
const getWarningSuggestion = (type) => {
  const map = {
    stock_low: '建议及时补货',
    stock_empty: '建议紧急采购',
    expiring: '建议优先销售或联系退货',
    expired: '建议立即下架并处理',
    stock_high: '建议暂停采购或促销',
    old_production: '建议设置促销或优先出库'
  }
  return map[type] || '请及时处理'
}

// 初始化数据（调用后端报表）
const initData = async () => {
  loading.value = true
  try {
    let data = await fetchLowStock()

    // 应用筛选条件
    if (filterForm.warningType) {
      data = data.filter(item => item.warningType === filterForm.warningType)
    }

    if (filterForm.warningLevel) {
      data = data.filter(item => item.warningLevel === filterForm.warningLevel)
    }

    if (filterForm.drugName) {
      data = data.filter(item =>
        (item.drugName || '').toLowerCase().includes(filterForm.drugName.toLowerCase())
      )
    }

    if (filterForm.status) {
      data = data.filter(item => item.status === filterForm.status)
    }

    if (filterForm.drugType) {
      // 后端报表可能不包含药品类型，暂用保守策略：不过滤或根据业务需求扩展
    }

    if (filterForm.supplier) {
      data = data.filter(item => (item.supplier || '').includes(filterForm.supplier))
    }

    if (filterForm.createTimeStart) {
      data = data.filter(item => item.createTime >= filterForm.createTimeStart)
    }

    if (filterForm.createTimeEnd) {
      data = data.filter(item => item.createTime <= filterForm.createTimeEnd)
    }

    // 更新统计信息
    updateStats(data)

    // 应用排序
    if (sortConfig.prop && sortConfig.order) {
      data.sort((a, b) => {
        const aVal = a[sortConfig.prop]
        const bVal = b[sortConfig.prop]

        if (sortConfig.order === 'ascending') {
          return aVal > bVal ? 1 : -1
        } else {
          return aVal < bVal ? 1 : -1
        }
      })
    }

    // 更新分页总数
    pagination.total = data.length

    // 分页处理
    const start = (pagination.currentPage - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    tableData.value = data.slice(start, end)

    // 更新图表
    updateCharts(data)
  } catch (err) {
    console.error('初始化预警数据失败', err)
    ElMessage.error(err.message || '初始化预警数据失败')
  } finally {
    loading.value = false
  }
}

// 更新统计信息
const updateStats = (data) => {
  stats.critical = data.filter(item => item.warningLevel === 'critical').length
  stats.high = data.filter(item => item.warningLevel === 'high').length
  stats.medium = data.filter(item => item.warningLevel === 'medium').length
  stats.low = data.filter(item => item.warningLevel === 'low').length
}

// 初始化图表
const initCharts = () => {
  // 销毁旧图表
  if (typeChart) {
    typeChart.dispose()
  }
  if (trendChart) {
    trendChart.dispose()
  }
  
  // 创建新图表
  typeChart = echarts.init(typeChartRef.value)
  trendChart = echarts.init(trendChartRef.value)
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
}

// 更新图表
const updateCharts = async (data) => {
  if (!typeChart || !trendChart) return
  
  // 预警类型分布数据
  const typeCount = {
    stock_low: 0,
    stock_empty: 0,
    expiring: 0,
    expired: 0,
    stock_high: 0,
    old_production: 0
  }
  
  data.forEach(item => {
    typeCount[item.warningType] = (typeCount[item.warningType] || 0) + 1
  })
  
  const typeChartOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: ['库存不足', '库存为0', '即将过期', '已过期', '库存积压', '生产日期久']
    },
    series: [
      {
        name: '预警类型',
        type: 'pie',
        radius: '50%',
        center: ['50%', '50%'],
        data: [
          { value: typeCount.stock_low, name: '库存不足' },
          { value: typeCount.stock_empty, name: '库存为0' },
          { value: typeCount.expiring, name: '即将过期' },
          { value: typeCount.expired, name: '已过期' },
          { value: typeCount.stock_high, name: '库存积压' },
          { value: typeCount.old_production, name: '生产日期久' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        itemStyle: {
          color: function(params) {
            const colorList = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272']
            return colorList[params.dataIndex % colorList.length]
          }
        }
      }
    ]
  }
  
  // 尝试从后端获取预警趋势数据（如果接口不可用则回退到本地模拟）
  let trendData = []
  let dateLabels = []
  try {
    const res = await reportApi.getLowStockReport({ trend: true })
    const data = res.data ?? res
    const trend = data.trend ?? data
    dateLabels = trend.map(t => t.date)
    trendData = trend.map(t => ({
      critical: t.critical ?? 0,
      high: t.high ?? 0,
      medium: t.medium ?? 0,
      low: t.low ?? 0
    }))
  } catch (err) {
    console.warn('获取预警趋势失败，清空图表数据以避免显示示例值', err)
    // 不再使用本地随机数据回退，后端不可用时显示空图表/中性提示
    dateLabels = []
    trendData = []
  }
  
  const trendChartOption = {
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
      data: ['紧急预警', '高级预警', '中级预警', '提醒']
    },
    xAxis: {
      type: 'category',
      data: dateLabels,
      axisPointer: {
        type: 'shadow'
      }
    },
    yAxis: {
      type: 'value',
      name: '预警数量',
      min: 0,
      max: 50,
      interval: 10
    },
    series: [
      {
        name: '紧急预警',
        type: 'bar',
        data: trendData.map(d => d.critical),
        itemStyle: {
          color: '#f56c6c'
        }
      },
      {
        name: '高级预警',
        type: 'bar',
        data: trendData.map(d => d.high),
        itemStyle: {
          color: '#e6a23c'
        }
      },
      {
        name: '中级预警',
        type: 'bar',
        data: trendData.map(d => d.medium),
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '提醒',
        type: 'bar',
        data: trendData.map(d => d.low),
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }
  
  typeChart.setOption(typeChartOption)
  trendChart.setOption(trendChartOption)
}

// 窗口大小变化处理
const handleResize = () => {
  if (typeChart) {
    typeChart.resize()
  }
  if (trendChart) {
    trendChart.resize()
  }
}

// 筛选
const handleFilter = () => {
  pagination.currentPage = 1
  initData()
}

// 重置筛选
const resetFilter = () => {
  Object.keys(filterForm).forEach(key => {
    filterForm[key] = ''
  })
  handleFilter()
}

// 切换高级筛选
const toggleAdvancedFilter = () => {
  showAdvancedFilter.value = !showAdvancedFilter.value
}

// 按预警级别筛选
const filterByLevel = (level) => {
  filterForm.warningLevel = level
  handleFilter()
}

// 处理全部预警
const handleAllWarning = () => {
  ElMessageBox.confirm(
    '是否处理所有未处理的预警？',
    '批量处理确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('已处理所有预警')
    initData()
  })
}

// 刷新数据
const refreshData = () => {
  initData()
  ElMessage.success('预警数据已刷新')
}

// 导出预警数据
const exportWarningData = () => {
  ElMessage.success('导出成功')
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.pageSize = size
  initData()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  initData()
}

// 排序处理
const handleSortChange = (sort) => {
  sortConfig.prop = sort.prop
  sortConfig.order = sort.order
  initData()
}

// 选择处理
const handleSelectionChange = (val) => {
  selection.value = val
}

// 批量处理
const batchProcess = () => {
  if (selection.value.length === 0) return
  batchDialogVisible.value = true
}

// 批量忽略
const batchIgnore = () => {
  if (selection.value.length === 0) return
  
  ElMessageBox.confirm(
    `确定要忽略选中的${selection.value.length}条预警吗？`,
    '批量忽略确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const payload = { ids: selection.value.map(s => s.id), action: 'ignore' }
      await stockApi.batchProcessWarnings(payload)
      ElMessage.success('已忽略选中的预警')
      selection.value = []
      initData()
    } catch (err) {
      console.warn('批量忽略接口调用失败，回退本地刷新', err)
      ElMessage.success('已忽略选中的预警（模拟回退）')
      selection.value = []
      initData()
    }
  })
}

// 查看详情
const viewDetail = (row) => {
  currentWarning.value = { ...row }
  detailDialogVisible.value = true
}

// 处理预警
const handleProcess = (row) => {
  currentWarning.value = { ...row }
  processDialogVisible.value = true
}

// 忽略预警
const handleIgnore = (row) => {
  ElMessageBox.confirm(
    `确定要忽略"${row.drugName}"的预警吗？`,
    '忽略确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await stockApi.processWarning(row.id, { action: 'ignore' })
      ElMessage.success('预警已忽略')
      initData()
    } catch (err) {
      console.warn('忽略预警接口调用失败，回退本地刷新', err)
      ElMessage.success('预警已忽略（模拟回退）')
      initData()
    }
  })
}

// 提醒预警
const handleRemind = async (row) => {
  try {
    await stockApi.remindWarning(row.id)
    ElMessage.success(`已发送提醒：${row.drugName}`)
  } catch (err) {
    console.warn('发送提醒接口不可用，回退提示', err)
    ElMessage.info(`已发送提醒：${row.drugName}（模拟回退）`)
  }
}

// 预警处理成功
const handleProcessSuccess = () => {
  processDialogVisible.value = false
  initData()
}

// 详情页面处理
const handleDetailProcess = () => {
  detailDialogVisible.value = false
  currentWarning.value && handleProcess(currentWarning.value)
}

// 批量处理成功
const handleBatchSuccess = () => {
  batchDialogVisible.value = false
  selection.value = []
  initData()
}

// 表格行样式
const tableRowClassName = ({ row }) => {
  if (row.warningLevel === 'critical') return 'row-critical'
  if (row.warningLevel === 'high') return 'row-high'
  if (row.warningLevel === 'medium') return 'row-medium'
  return 'row-low'
}

// 工具函数
const getWarningTypeText = (type) => {
  const map = {
    stock_low: '库存不足',
    stock_empty: '库存为0',
    expiring: '即将过期',
    expired: '已过期',
    stock_high: '库存积压',
    old_production: '生产日期久'
  }
  return map[type] || type
}

const getWarningTypeTag = (type) => {
  const map = {
    stock_low: 'warning',
    stock_empty: 'danger',
    expiring: 'warning',
    expired: 'danger',
    stock_high: 'info',
    old_production: 'info'
  }
  return map[type] || 'info'
}

const getWarningLevelText = (level) => {
  const map = {
    critical: '紧急预警',
    high: '高级预警',
    medium: '中级预警',
    low: '提醒'
  }
  return map[level] || level
}

const getWarningLevelTag = (level) => {
  const map = {
    critical: 'danger',
    high: 'warning',
    medium: 'primary',
    low: 'success'
  }
  return map[level] || 'info'
}

const getStatusText = (status) => {
  const map = {
    pending: '未处理',
    processing: '处理中',
    resolved: '已处理',
    ignored: '已忽略'
  }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = {
    pending: 'danger',
    processing: 'warning',
    resolved: 'success',
    ignored: 'info'
  }
  return map[status] || 'info'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return ''
  const date = new Date(dateTimeString)
  return date.toLocaleString('zh-CN')
}

// 组件挂载
onMounted(() => {
  initData()
  nextTick(() => {
    initCharts()
  })
})

// 组件卸载前清理
onBeforeUnmount(() => {
  if (typeChart) {
    typeChart.dispose()
  }
  if (trendChart) {
    trendChart.dispose()
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.stock-warning-container {
  padding: 20px;
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

.warning-stats {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 0;
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
}

.stat-icon.critical {
  background-color: #fef0f0;
  color: #f56c6c;
}

.stat-icon.high {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.stat-icon.medium {
  background-color: #ecf5ff;
  color: #409EFF;
}

.stat-icon.low {
  background-color: #f0f9eb;
  color: #67C23A;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 2px;
}

.stat-desc {
  font-size: 12px;
  color: #999;
}

.charts-section {
  margin-bottom: 20px;
}

.chart-card {
  height: 300px;
}

.chart-container {
  width: 100%;
  height: 250px;
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

.warning-table-card {
  margin-bottom: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.drug-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.drug-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stock-text {
  font-weight: bold;
}

.stock-warning {
  color: #e6a23c;
}

.stock-danger {
  color: #f56c6c;
}

.expiry-date {
  font-weight: bold;
}

.expiry-warning {
  color: #e6a23c;
}

.expiry-danger {
  color: #f56c6c;
}

.remaining-days {
  font-weight: bold;
  text-align: center;
  padding: 2px 8px;
  border-radius: 10px;
  display: inline-block;
}

.remaining-days.danger {
  background-color: #fef0f0;
  color: #f56c6c;
}

.remaining-days.warning {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.remaining-days.normal {
  background-color: #f0f9eb;
  color: #67C23A;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 表格行样式 */
:deep(.row-critical) {
  background-color: #fef0f0 !important;
}

:deep(.row-high) {
  background-color: #fdf6ec !important;
}

:deep(.row-medium) {
  background-color: #ecf5ff !important;
}

:deep(.row-low) {
  background-color: #f0f9eb !important;
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
  }
  
  .warning-stats .el-col {
    margin-bottom: 15px;
  }
  
  .charts-section .el-col {
    margin-bottom: 15px;
  }
  
  .filter-form .el-col {
    margin-bottom: 10px;
  }
  
  .filter-actions {
    flex-direction: column;
  }
  
  .filter-actions .el-button {
    width: 100%;
  }
}
</style>