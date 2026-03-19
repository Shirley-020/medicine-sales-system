<!-- src/views/stock/StockQuery.vue -->
<template>
  <div class="stock-query-container">
    <!-- 页面标题和操作按钮 -->
    <div class="page-header">
      <div class="header-left">
        <h2>库存查询</h2>
        <p class="subtitle">查看药品库存信息，支持多条件筛选和查询</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="exportInventory" :icon="Download">
          导出库存
        </el-button>
        <el-button @click="printInventory" :icon="Printer">
          打印库存
        </el-button>
        <el-button @click="refreshData" :icon="Refresh">
          刷新
        </el-button>
      </div>
    </div>

    <!-- 库存统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon total">
                <el-icon><Box /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">总库存品种</div>
                <div class="stat-value">{{ stats.totalVarieties }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon normal">
                <el-icon><Check /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">库存正常</div>
                <div class="stat-value">{{ stats.normalStock }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon warning">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">库存不足</div>
                <div class="stat-value">{{ stats.lowStock }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon expired">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">即将过期</div>
                <div class="stat-value">{{ stats.expiringSoon }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form
        :model="searchForm"
        ref="searchFormRef"
        label-width="100px"
        class="search-form"
      >
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="药品名称">
              <el-input
                v-model="searchForm.drugName"
                placeholder="请输入药品名称"
                clearable
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="药品编码">
              <el-input
                v-model="searchForm.drugCode"
                placeholder="请输入药品编码"
                clearable
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="库存状态">
              <el-select
                v-model="searchForm.stockStatus"
                placeholder="请选择库存状态"
                clearable
              >
                <el-option label="库存正常" value="normal" />
                <el-option label="库存不足" value="low" />
                <el-option label="库存充足" value="high" />
                <el-option label="无库存" value="empty" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="存放位置">
              <el-select
                v-model="searchForm.location"
                placeholder="请选择存放位置"
                clearable
              >
                <el-option label="主仓库A区" value="A" />
                <el-option label="主仓库B区" value="B" />
                <el-option label="冷库" value="cold" />
                <el-option label="货架1层" value="shelf1" />
                <el-option label="货架2层" value="shelf2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="批次号">
              <el-input
                v-model="searchForm.batchNo"
                placeholder="请输入批次号"
                clearable
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="供应商">
              <el-input
                v-model="searchForm.supplier"
                placeholder="请输入供应商"
                clearable
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="库存范围">
              <div class="stock-range">
                <el-input-number
                  v-model="searchForm.minStock"
                  placeholder="最低"
                  :min="0"
                  style="width: 48%"
                />
                <span class="range-separator">-</span>
                <el-input-number
                  v-model="searchForm.maxStock"
                  placeholder="最高"
                  :min="0"
                  style="width: 48%"
                />
              </div>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24">
            <div class="form-actions">
              <el-button
                type="primary"
                @click="handleSearch"
                :icon="Search"
              >
                查询
              </el-button>
              <el-button @click="resetSearch" :icon="Refresh">
                重置
              </el-button>
              <el-button @click="toggleAdvancedSearch" :icon="Setting">
                高级搜索
              </el-button>
            </div>
          </el-col>
        </el-row>

        <!-- 高级搜索区域 -->
        <el-collapse-transition>
          <div v-show="showAdvancedSearch">
            <el-divider />
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12" :md="8" :lg="6">
                <el-form-item label="生产日期从">
                  <el-date-picker
                    v-model="searchForm.productionDateStart"
                    type="date"
                    placeholder="开始日期"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12" :md="8" :lg="6">
                <el-form-item label="生产日期至">
                  <el-date-picker
                    v-model="searchForm.productionDateEnd"
                    type="date"
                    placeholder="结束日期"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12" :md="8" :lg="6">
                <el-form-item label="有效期从">
                  <el-date-picker
                    v-model="searchForm.expiryDateStart"
                    type="date"
                    placeholder="开始日期"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12" :md="8" :lg="6">
                <el-form-item label="有效期至">
                  <el-date-picker
                    v-model="searchForm.expiryDateEnd"
                    type="date"
                    placeholder="结束日期"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </el-collapse-transition>
      </el-form>
    </el-card>

    <!-- 库存数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <span>库存数据</span>
          <div class="header-actions">
            <el-button-group>
              <el-button
                @click="changeViewMode('table')"
                :type="viewMode === 'table' ? 'primary' : ''"
              >
                <el-icon><Grid /></el-icon> 表格
              </el-button>
              <el-button
                @click="changeViewMode('list')"
                :type="viewMode === 'list' ? 'primary' : ''"
              >
                <el-icon><List /></el-icon> 列表
              </el-button>
            </el-button-group>
          </div>
        </div>
      </template>

      <!-- 表格视图 -->
      <div v-if="viewMode === 'table'">
        <el-table
          v-loading="loading"
          :data="tableData"
          style="width: 100%"
          @sort-change="handleSortChange"
          @selection-change="handleSelectionChange"
          :row-class-name="tableRowClassName"
        >
          <el-table-column type="selection" width="55" />

          <el-table-column prop="drugCode" label="药品编码" width="120" sortable />

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

          <el-table-column prop="specification" label="规格" width="120" />

          <el-table-column prop="unit" label="单位" width="80" />

          <el-table-column prop="batchNo" label="批次号" width="120" />

          <el-table-column prop="productionDate" label="生产日期" width="120" sortable>
            <template #default="{ row }">
              {{ formatDate(row.productionDate) }}
            </template>
          </el-table-column>

          <el-table-column prop="expiryDate" label="有效期至" width="120" sortable>
            <template #default="{ row }">
              <div :class="{
                'expiry-date': true,
                'expiry-warning': isExpiringSoon(row.expiryDate),
                'expiry-danger': isExpired(row.expiryDate)
              }">
                {{ formatDate(row.expiryDate) }}
                <el-tooltip v-if="isExpiringSoon(row.expiryDate)" content="即将过期">
                  <el-icon class="warning-icon"><Timer /></el-icon>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="currentStock" label="当前库存" width="120" sortable>
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

          <el-table-column prop="maxStock" label="最高库存" width="100" />

          <el-table-column prop="location" label="存放位置" width="120">
            <template #default="{ row }">
              <el-tag :type="getLocationTag(row.location)">
                {{ getLocationText(row.location) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="supplier" label="供应商" width="150" show-overflow-tooltip />

          <el-table-column prop="unitPrice" label="单价(元)" width="100" sortable>
            <template #default="{ row }">
              ¥{{ (row.unitPrice || 0).toFixed(2) }}
            </template>
          </el-table-column>

          <el-table-column prop="totalValue" label="库存价值" width="120" sortable>
            <template #default="{ row }">
              ¥{{ (row.currentStock * (row.unitPrice || 0)).toFixed(2) }}
            </template>
          </el-table-column>

          <el-table-column prop="lastUpdated" label="最后更新" width="150" sortable>
            <template #default="{ row }">
              {{ formatDateTime(row.lastUpdated) }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button
                  type="primary"
                  link
                  @click="viewStockDetail(row)"
                  :icon="View"
                  size="small"
                >
                  详情
                </el-button>
                <el-button
                  type="success"
                  link
                  @click="adjustStock(row)"
                  :icon="Edit"
                  size="small"
                >
                  调整
                </el-button>
                <el-button
                  type="warning"
                  link
                  @click="transferStock(row)"
                  :icon="Position"
                  size="small"
                >
                  调拨
                </el-button>
                <el-button
                  type="danger"
                  link
                  @click="deleteStock(row)"
                  :icon="Delete"
                  size="small"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 批量操作 -->
        <div class="batch-actions" v-if="selection.length > 0">
          <el-button type="warning" @click="batchExport" :icon="Download" size="small">
            导出选中({{ selection.length }})
          </el-button>
          <el-button type="info" @click="batchPrint" :icon="Printer" size="small">
            打印选中
          </el-button>
        </div>
      </div>

      <!-- 列表视图 -->
      <div v-else class="list-view">
        <div class="stock-list">
          <div
            v-for="item in tableData"
            :key="item.id"
            class="stock-item"
            :class="{
              'item-warning': item.currentStock < item.minStock,
              'item-danger': item.currentStock === 0,
              'item-expiring': isExpiringSoon(item.expiryDate)
            }"
          >
            <div class="item-header">
              <div class="item-title">
                <h4>{{ item.drugName }}</h4>
                <div class="item-tags">
                  <el-tag :type="getStockStatusTag(item)" size="small">
                    {{ getStockStatusText(item) }}
                  </el-tag>
                  <el-tag v-if="item.isPrescription" type="danger" size="small">
                    处方药
                  </el-tag>
                </div>
              </div>
              <div class="item-code">{{ item.drugCode }}</div>
            </div>

            <div class="item-content">
              <div class="item-row">
                <span class="label">规格：</span>
                <span class="value">{{ item.specification }}</span>
              </div>
              <div class="item-row">
                <span class="label">批次号：</span>
                <span class="value">{{ item.batchNo }}</span>
              </div>
              <div class="item-row">
                <span class="label">有效期：</span>
                <span class="value" :class="{
                  'expiry-warning': isExpiringSoon(item.expiryDate),
                  'expiry-danger': isExpired(item.expiryDate)
                }">
                  {{ formatDate(item.expiryDate) }}
                </span>
              </div>
              <div class="item-row">
                <span class="label">库存：</span>
                <span class="value stock-value" :class="{
                  'stock-warning': item.currentStock < item.minStock,
                  'stock-danger': item.currentStock === 0
                }">
                  {{ item.currentStock }} {{ item.unit }}
                  <span class="stock-range">(最低:{{ item.minStock }})</span>
                </span>
              </div>
              <div class="item-row">
                <span class="label">位置：</span>
                <span class="value">{{ getLocationText(item.location) }}</span>
              </div>
              <div class="item-row">
                <span class="label">供应商：</span>
                <span class="value">{{ item.supplier }}</span>
              </div>
              <div class="item-row">
                <span class="label">库存价值：</span>
                <span class="value price">¥{{ (item.currentStock * (item.unitPrice || 0)).toFixed(2) }}</span>
              </div>
            </div>

            <div class="item-footer">
              <el-button-group size="small">
                <el-button @click="viewStockDetail(item)" type="primary" link>
                  详情
                </el-button>
                <el-button @click="adjustStock(item)" type="success" link>
                  调整
                </el-button>
                <el-button @click="transferStock(item)" type="warning" link>
                  调拨
                </el-button>
                <el-button @click="deleteStock(item)" type="danger" link>
                  删除
                </el-button>
              </el-button-group>
            </div>
          </div>
        </div>
      </div>

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

    <!-- 库存详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="库存详情"
      width="700px"
    >
      <stock-detail
        v-if="detailDialogVisible"
        :stock-data="currentStock"
        @close="detailDialogVisible = false"
      />
    </el-dialog>

    <!-- 库存调整对话框 -->
    <el-dialog
      v-model="adjustDialogVisible"
      :title="`库存调整 - ${currentStock?.drugName || ''}`"
      width="500px"
    >
      <stock-adjust
        v-if="adjustDialogVisible"
        :stock-data="currentStock"
        @success="handleAdjustSuccess"
        @close="adjustDialogVisible = false"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import stockApi from '@/api/stock'
import StockDetail from './components/StockDetail.vue'
import StockAdjust from './components/StockAdjust.vue'
import {
  Download, Printer, Refresh, Search, Setting,
  Box, Check, Warning, Clock,
  Grid, List, View, Edit, Position, Timer, Delete
} from '@element-plus/icons-vue'

// 搜索表单引用
const searchFormRef = ref(null)

// 搜索表单数据
const searchForm = reactive({
  drugName: '',
  drugCode: '',
  stockStatus: '',
  location: '',
  batchNo: '',
  supplier: '',
  minStock: null,
  maxStock: null,
  productionDateStart: '',
  productionDateEnd: '',
  expiryDateStart: '',
  expiryDateEnd: ''
})

// 高级搜索显示状态
const showAdvancedSearch = ref(false)

// 视图模式：table 或 list
const viewMode = ref('table')

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
  prop: 'drugName',
  order: 'ascending'
})

// 统计信息
const stats = reactive({
  totalVarieties: 0,
  normalStock: 0,
  lowStock: 0,
  expiringSoon: 0
})

// 对话框控制
const detailDialogVisible = ref(false)
const adjustDialogVisible = ref(false)
const currentStock = ref(null)

// 获取详细库存列表（后端 /stock/all 返回 List<StockDetailDto>）
const fetchStockDetails = async () => {
  try {
    // 后端 /stock/all 接口现在直接返回详细库存列表：List<StockDetailDto>
    const resp = await stockApi.getAllStock()
    const list = resp?.data ?? resp

    if (!Array.isArray(list)) {
      console.error('获取库存明细失败：后端返回非数组', list)
      ElMessage.error('获取库存数据格式不正确')
      return []
    }

    // 字段命名已与表格 prop 对齐（drugName/specification/batchNo/productionDate/expiryDate/location...）
    return list
  } catch (err) {
    console.error('获取库存明细失败', err)
    ElMessage.error(err.message || '获取库存明细失败')
    return []
  }
}

// 初始化数据（合成自 /drug/list 与 /stock/all）
const initData = async () => {
  loading.value = true
  try {
    let data = await fetchStockDetails()

    // 应用搜索条件
    if (searchForm.drugName) {
      data = data.filter(item =>
        (item.drugName || '').toLowerCase().includes(searchForm.drugName.toLowerCase())
      )
    }

    if (searchForm.drugCode) {
      data = data.filter(item => (item.drugCode || '').includes(searchForm.drugCode))
    }

    if (searchForm.stockStatus) {
      data = data.filter(item => {
        const status = getStockStatus(item)
        return status === searchForm.stockStatus
      })
    }

    if (searchForm.location) {
      data = data.filter(item => item.location === searchForm.location)
    }

    if (searchForm.batchNo) {
      data = data.filter(item => (item.batchNo || '').includes(searchForm.batchNo))
    }

    if (searchForm.supplier) {
      data = data.filter(item => (item.supplier || '').includes(searchForm.supplier))
    }

    if (searchForm.minStock !== null) {
      data = data.filter(item => item.currentStock >= searchForm.minStock)
    }

    if (searchForm.maxStock !== null) {
      data = data.filter(item => item.currentStock <= searchForm.maxStock)
    }

    if (searchForm.productionDateStart) {
      data = data.filter(item => item.productionDate >= searchForm.productionDateStart)
    }

    if (searchForm.productionDateEnd) {
      data = data.filter(item => item.productionDate <= searchForm.productionDateEnd)
    }

    if (searchForm.expiryDateStart) {
      data = data.filter(item => item.expiryDate >= searchForm.expiryDateStart)
    }

    if (searchForm.expiryDateEnd) {
      data = data.filter(item => item.expiryDate <= searchForm.expiryDateEnd)
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
  } catch (err) {
    console.error('初始化库存查询失败', err)
    ElMessage.error(err.message || '初始化库存查询失败')
  } finally {
    loading.value = false
  }
}

// 更新统计信息
const updateStats = (data) => {
  stats.totalVarieties = data.length

  // 统计各种状态，用于调试
  const statusCounts = {
    normal: 0,
    high: 0,
    low: 0,
    empty: 0,
    expired: 0,
    expiring: 0
  }

  data.forEach(item => {
    const status = getStockStatus(item)
    statusCounts[status] = (statusCounts[status] || 0) + 1

    // 额外检查：如果状态是 normal 或 high，但已过期或即将过期，说明判断有问题
    if ((status === 'normal' || status === 'high') && item.expiryDate) {
      if (isExpired(item.expiryDate)) {
        console.warn('发现状态判断错误：状态为', status, '但已过期', {
          drugName: item.drugName,
          batchNo: item.batchNo,
          expiryDate: item.expiryDate,
          currentStock: item.currentStock
        })
      } else if (isExpiringSoon(item.expiryDate)) {
        console.warn('发现状态判断错误：状态为', status, '但即将过期', {
          drugName: item.drugName,
          batchNo: item.batchNo,
          expiryDate: item.expiryDate,
          currentStock: item.currentStock
        })
      }
    }
  })

  console.debug('库存状态统计:', statusCounts)
  console.debug('总批次:', stats.totalVarieties, '正常:', statusCounts.normal + statusCounts.high, '不正常:', stats.totalVarieties - (statusCounts.normal + statusCounts.high))

  // 库存正常：既不是库存不足，也不是库存为0，也不是即将过期，也不是已过期
  stats.normalStock = data.filter(item => {
    const status = getStockStatus(item)
    // 只统计状态为 'normal' 或 'high' 的，明确排除 'empty'、'low'、'expired'、'expiring'
    return status === 'normal' || status === 'high'
  }).length

  // 库存不足：包括库存为0和库存低于最低库存的
  stats.lowStock = data.filter(item => {
    const status = getStockStatus(item)
    return status === 'low' || status === 'empty'
  }).length

  // 即将过期：90天内过期但未过期的
  stats.expiringSoon = data.filter(item => {
    if (isExpired(item.expiryDate)) return false // 已过期不算即将过期
    return isExpiringSoon(item.expiryDate)
  }).length
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  initData()
}

// 重置搜索
const resetSearch = () => {
  if (searchFormRef.value) {
    searchFormRef.value.resetFields()
  }
  Object.keys(searchForm).forEach(key => {
    if (typeof searchForm[key] === 'number') {
      searchForm[key] = null
    } else {
      searchForm[key] = ''
    }
  })
  handleSearch()
}

// 切换高级搜索
const toggleAdvancedSearch = () => {
  showAdvancedSearch.value = !showAdvancedSearch.value
}

// 切换视图模式
const changeViewMode = (mode) => {
  viewMode.value = mode
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

// 导出库存
const exportInventory = async () => {
  try {
    const params = {
      ...searchForm,
      page: pagination.currentPage,
      limit: pagination.pageSize,
      sortField: sortConfig.prop,
      sortOrder: sortConfig.order
    }
    const res = await stockApi.exportInventory(params)
    const data = res.data ?? res
    if (data && (data.url || data.downloadUrl || data.link)) {
      window.open(data.url ?? data.downloadUrl ?? data.link, '_blank')
      ElMessage.success('导出开始，请检查下载')
    } else {
      throw new Error('后端未返回导出链接')
    }
  } catch (err) {
    console.warn('导出接口不可用，回退为提示', err)
    ElMessage.info('导出功能暂不可用')
  }
}

// 打印库存
const printInventory = async () => {
  try {
    const params = { ...searchForm }
    const res = await stockApi.exportInventory(params)
    const data = res.data ?? res
    if (data && (data.url || data.downloadUrl || data.link)) {
      window.open(data.url ?? data.downloadUrl ?? data.link, '_blank')
      ElMessage.success('正在生成打印内容')
    } else {
      throw new Error('后端未返回打印链接')
    }
  } catch (err) {
    console.warn('打印接口不可用', err)
    ElMessage.info('打印功能暂不可用')
  }
}

// 刷新数据
const refreshData = () => {
  initData()
  ElMessage.success('数据已刷新')
}

// 批量导出
const batchExport = async () => {
  if (selection.value.length === 0) return
  try {
    const payload = { ids: selection.value.map(s => s.id) }
    const res = await stockApi.exportInventoryBatch(payload)
    const data = res.data ?? res
    if (data && (data.url || data.downloadUrl || data.link)) {
      window.open(data.url ?? data.downloadUrl ?? data.link, '_blank')
      ElMessage.success('批量导出开始，请检查下载')
    } else {
      throw new Error('后端未返回导出链接')
    }
  } catch (err) {
    console.warn('批量导出接口不可用，回退为提示', err)
    ElMessage.info('批量导出暂不可用')
  }
}

// 批量打印
const batchPrint = async () => {
  if (selection.value.length === 0) return
  try {
    const payload = { ids: selection.value.map(s => s.id), exportType: 'print' }
    const res = await stockApi.exportInventoryBatch(payload)
    const data = res.data ?? res
    if (data && (data.url || data.downloadUrl || data.link)) {
      window.open(data.url ?? data.downloadUrl ?? data.link, '_blank')
      ElMessage.success('批量打印内容已生成')
    } else {
      throw new Error('后端未返回打印链接')
    }
  } catch (err) {
    console.warn('批量打印接口不可用，回退为提示', err)
    ElMessage.info('批量打印暂不可用')
  }
}

// 查看库存详情
const viewStockDetail = (row) => {
  currentStock.value = { ...row }
  detailDialogVisible.value = true
}

// 调整库存
const adjustStock = (row) => {
  currentStock.value = { ...row }
  adjustDialogVisible.value = true
}

// 库存调拨
const transferStock = async (row) => {
  try {
    // 简单示例：默认调拨到 B 区，实际应打开调拨对话框以收集参数
    await stockApi.transferStock(row.id, { toLocation: 'B' })
    ElMessage.success(`库存调拨已提交: ${row.drugName}`)
    initData()
  } catch (err) {
    console.warn('库存调拨接口不可用，回退为提示', err)
    ElMessage.info(`库存调拨暂不可用: ${row.drugName}`)
  }
}

// 删除库存
const deleteStock = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除库存记录吗？\n药品：${row.drugName}\n批次：${row.batchNo || '无'}\n数量：${row.currentStock}`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: false
      }
    )
    
    await stockApi.deleteStock(row.id)
    ElMessage.success('删除成功')
    initData()
  } catch (err) {
    if (err !== 'cancel') {
      console.error('删除库存失败', err)
      ElMessage.error(err.message || '删除失败')
    }
  }
}

// 库存调整成功
const handleAdjustSuccess = (payload) => {
  adjustDialogVisible.value = false;
  if (payload && payload.stockId !== undefined) {
    const item = tableData.value.find(i => i.id === payload.stockId);
    if (item) {
      item.currentStock = payload.newQty;
      // 可选：同时更新统计信息，避免全量刷新
      updateStats(tableData.value);
      ElMessage.success('库存已在界面更新');
    } else {
      // 如果在当前页找不到（例如，分页或排序后），则回退到重新加载
      initData();
    }
  } else {
    // 如果没有 payload，作为兼容回退
    initData();
  }
}

// 表格行样式
const tableRowClassName = ({ row }) => {
  if (row.currentStock === 0) {
    return 'row-danger'
  } else if (row.currentStock < row.minStock) {
    return 'row-warning'
  } else if (isExpiringSoon(row.expiryDate)) {
    return 'row-expiring'
  }
  return ''
}

// 工具函数
const getStockStatus = (item) => {
  // 首先检查是否已过期，已过期的药品不应该被归类为正常
  if (item.expiryDate && isExpired(item.expiryDate)) {
    return 'expired'
  }

  // 然后检查是否即将过期（90天内），即将过期的也不应该被归类为正常
  if (item.expiryDate && isExpiringSoon(item.expiryDate)) {
    return 'expiring' // 返回一个特殊状态，表示即将过期
  }

  // 然后检查库存数量（确保处理 null/undefined 的情况）
  const currentStock = item.currentStock ?? 0
  const minStock = item.minStock ?? 0

  // 库存为0或负数，都是不正常的
  if (currentStock <= 0) return 'empty'
  // 库存低于最低库存，也是不正常的
  if (currentStock < minStock) return 'low'
  // 库存超过最高库存的80%，认为是充足的
  if (item.maxStock && currentStock > item.maxStock * 0.8) return 'high'
  // 其他情况才是正常
  return 'normal'
}

const getStockStatusText = (item) => {
  const status = getStockStatus(item)
  const map = {
    normal: '正常',
    low: '不足',
    high: '充足',
    empty: '无库存',
    expired: '已过期',
    expiring: '即将过期'
  }
  return map[status] || status
}

const getStockStatusTag = (item) => {
  const status = getStockStatus(item)
  const map = {
    normal: 'success',
    low: 'warning',
    high: 'info',
    empty: 'danger',
    expired: 'danger',
    expiring: 'warning'
  }
  return map[status] || 'info'
}

const getLocationText = (location) => {
  const map = {
    A: '主仓库A区',
    B: '主仓库B区',
    cold: '冷库',
    shelf1: '货架1层',
    shelf2: '货架2层'
  }
  return map[location] || location
}

const getLocationTag = (location) => {
  const map = {
    A: 'info',
    B: 'success',
    cold: 'info',
    shelf1: 'warning',
    shelf2: 'danger'
  }
  return map[location] || 'info'
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

const isExpiringSoon = (dateString) => {
  if (!dateString) return false
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return false
    // 只比较日期部分，忽略时间
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const expiryDate = new Date(date)
    expiryDate.setHours(0, 0, 0, 0)
    const diffTime = expiryDate.getTime() - today.getTime()
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return diffDays > 0 && diffDays <= 90
  } catch (e) {
    console.warn('日期解析失败:', dateString, e)
    return false
  }
}

const isExpired = (dateString) => {
  if (!dateString) return false
  try {
    // 处理各种日期格式：LocalDate (YYYY-MM-DD) 或 ISO 字符串
    // 如果是 YYYY-MM-DD 格式，直接解析
    let date
    if (typeof dateString === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(dateString)) {
      // LocalDate 格式：YYYY-MM-DD
      const [year, month, day] = dateString.split('-').map(Number)
      date = new Date(year, month - 1, day)
    } else {
      date = new Date(dateString)
    }

    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      console.warn('无效的过期日期:', dateString)
      return false
    }

    // 只比较日期部分，忽略时间
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const expiryDate = new Date(date)
    expiryDate.setHours(0, 0, 0, 0)

    const isExpiredResult = expiryDate < today
    // 调试日志：如果过期，输出信息
    if (isExpiredResult) {
      console.debug('发现过期批次:', { expiryDate: dateString, today: today.toISOString().split('T')[0] })
    }

    return isExpiredResult
  } catch (e) {
    console.warn('日期解析失败:', dateString, e)
    return false
  }
}

// 初始化
onMounted(() => {
  initData()
})
</script>

<style scoped>
.stock-query-container {
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

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 0;
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

.stat-icon.total {
  background-color: #ecf5ff;
  color: #409EFF;
}

.stat-icon.normal {
  background-color: #f0f9eb;
  color: #67C23A;
}

.stat-icon.warning {
  background-color: #fdf6ec;
  color: #E6A23C;
}

.stat-icon.expired {
  background-color: #fef0f0;
  color: #F56C6C;
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
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-top: 10px;
}

.stock-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.range-separator {
  color: #666;
  font-weight: bold;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 10px;
}

.table-card {
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
  display: flex;
  align-items: center;
  gap: 5px;
}

.expiry-warning {
  color: #e6a23c;
}

.expiry-danger {
  color: #f56c6c;
}

.warning-icon {
  font-size: 14px;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.batch-actions {
  display: flex;
  gap: 10px;
  margin: 15px 0;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

/* 列表视图样式 */
.list-view {
  margin-top: 10px;
}

.stock-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.stock-item {
  background: white;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 15px;
  transition: all 0.3s;
}

.stock-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.item-warning {
  border-left: 4px solid #e6a23c;
}

.item-danger {
  border-left: 4px solid #f56c6c;
}

.item-expiring {
  border-left: 4px solid #f56c6c;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.item-title {
  flex: 1;
}

.item-title h4 {
  margin: 0 0 8px;
  font-size: 16px;
  color: #333;
}

.item-tags {
  display: flex;
  gap: 5px;
}

.item-code {
  font-size: 12px;
  color: #999;
  background: #f5f7fa;
  padding: 2px 8px;
  border-radius: 10px;
}

.item-content {
  margin-bottom: 15px;
}

.item-row {
  display: flex;
  margin-bottom: 8px;
  font-size: 13px;
}

.item-row .label {
  color: #666;
  width: 70px;
  flex-shrink: 0;
}

.item-row .value {
  color: #333;
  flex: 1;
}

.stock-value {
  font-weight: bold;
}

.stock-range {
  font-size: 12px;
  color: #999;
  margin-left: 5px;
}

.price {
  color: #e6a23c;
  font-weight: bold;
}

.item-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 表格行样式 */
:deep(.row-danger) {
  background-color: #fef0f0 !important;
}

:deep(.row-warning) {
  background-color: #fdf6ec !important;
}

:deep(.row-expiring) {
  background-color: #fef0f0 !important;
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

  .search-form .el-col {
    margin-bottom: 10px;
  }

  .stock-range {
    flex-direction: column;
    gap: 5px;
  }

  .stock-list {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions .el-button {
    width: 100%;
  }
}
</style>
