<!-- src/views/drug/DrugList.vue -->
<template>
  <div class="drug-list-container">
    <!-- 页面标题和操作按钮 -->
    <div class="page-header">
      <div class="header-left">
        <h2>药品列表管理</h2>
        <p class="subtitle">管理所有药品信息，支持增删改查操作</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleAdd" :icon="Plus">
          新增药品
        </el-button>
        <el-button @click="exportData" :icon="Download">
          导出数据
        </el-button>
        <el-button @click="refreshData" :icon="Refresh">
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <div class="search-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-input
              v-model="searchForm.drugName"
              placeholder="药品名称"
              clearable
              @keyup.enter="handleSearch"
              @clear="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-select
              v-model="searchForm.drugType"
              placeholder="药品类型"
              clearable
              @change="handleSearch"
            >
              <el-option label="处方药" value="prescription" />
              <el-option label="非处方药" value="otc" />
              <el-option label="中药" value="traditional" />
              <el-option label="西药" value="western" />
              <el-option label="医疗器械" value="equipment" />
            </el-select>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-select
              v-model="searchForm.status"
              placeholder="药品状态"
              clearable
              @change="handleSearch"
            >
              <el-option label="正常" value="normal" />
              <el-option label="缺货" value="out_of_stock" />
              <el-option label="停售" value="discontinued" />
              <el-option label="近效期" value="expiring" />
            </el-select>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <div class="search-buttons">
              <el-button type="primary" @click="handleSearch" :icon="Search">
                搜索
              </el-button>
              <el-button @click="resetSearch" :icon="Refresh">
                重置
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        @sort-change="handleSortChange"
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
        
        <el-table-column prop="genericName" label="通用名" width="150" show-overflow-tooltip />
        
        <el-table-column prop="drugType" label="药品类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getDrugTypeTag(row.drugType)">
              {{ getDrugTypeText(row.drugType) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="specification" label="规格" width="120" />
        
        <el-table-column prop="unit" label="单位" width="80" />
        
        <el-table-column prop="manufacturer" label="生产厂家" width="180" show-overflow-tooltip />
        
        <el-table-column prop="stock" label="库存" width="100" sortable>
          <template #default="{ row }">
            <div :class="{
              'stock-text': true,
              'stock-warning': row.stock < row.minStock,
              'stock-danger': row.stock === 0
            }">
              {{ row.stock }}
              <el-tooltip v-if="row.stock < row.minStock" content="库存低于最低库存量">
                <el-icon class="warning-icon"><Warning /></el-icon>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="price" label="单价(元)" width="100" sortable>
          <template #default="{ row }">
            ¥{{ (row.price || 0).toFixed(2) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="primary"
                link
                @click="handleView(row)"
                :icon="View"
                size="small"
              >
                详情
              </el-button>
              <el-button
                type="success"
                link
                @click="handleEdit(row)"
                :icon="Edit"
                size="small"
              >
                编辑
              </el-button>
              <el-button
                type="danger"
                link
                @click="handleDelete(row)"
                :icon="Delete"
                size="small"
              >
                删除
              </el-button>
              <el-dropdown @command="(command) => handleMore(row, command)">
                <el-button link size="small">
                  更多<el-icon><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="stock" :icon="Box">库存调整</el-dropdown-item>
                    <el-dropdown-item command="price" :icon="PriceTag">调价</el-dropdown-item>
                    <el-dropdown-item command="disable" :icon="Close" v-if="row.status === 'normal'">
                      停售
                    </el-dropdown-item>
                    <el-dropdown-item command="enable" :icon="Check" v-else>
                      启用
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <div class="batch-actions">
          <el-button
            type="danger"
            :disabled="selection.length === 0"
            @click="batchDelete"
            :icon="Delete"
            size="small"
          >
            批量删除({{ selection.length }})
          </el-button>
          <el-button
            type="warning"
            :disabled="selection.length === 0"
            @click="batchDisable"
            :icon="Close"
            size="small"
          >
            批量停售
          </el-button>
          <el-button
            type="danger"
            @click="deleteTestData"
            :icon="Delete"
            size="small"
            style="margin-left: 8px"
          >
            删除测试数据
          </el-button>
        </div>
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

    <!-- 药品详情/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :before-close="handleDialogClose"
    >
      <drug-form
        v-if="dialogVisible"
        ref="drugFormRef"
        :drug-data="currentDrug"
        :mode="dialogMode"
        @success="handleFormSuccess"
        @cancel="handleCancel" 
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import DrugForm from './components/DrugForm.vue'
import {
  Plus, Download, Refresh, Search, Delete,
  Edit, View, Warning, Timer, Box, PriceTag,
  Close, Check, ArrowDown
} from '@element-plus/icons-vue'
import drugApi from '@/api/drug' 



// 响应式数据
const loading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('add') // 'add' | 'edit' | 'view'
const dialogTitle = computed(() => {
  const map = {
    add: '新增药品',
    edit: '编辑药品',
    view: '药品详情'
  }
  return map[dialogMode.value]
})
const currentDrug = ref(null)
const drugFormRef = ref(null)

// 表格数据
const tableData = ref([])
const selection = ref([])

// 搜索表单
const searchForm = reactive({
  drugName: '',
  drugType: '',
  status: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 排序
const sortConfig = reactive({
  prop: '',
  order: ''
})

// 初始化数据（使用后端 API）
const initData = async () => {
  loading.value = true
  try {
    const res = await drugApi.getDrugList()
    // 兼容不同后端返回格式
    let data = []
    if (Array.isArray(res)) {
      data = res
    } else if (Array.isArray(res.data)) {
      data = res.data
    } else if (Array.isArray(res.data?.data)) {
      data = res.data.data
    } else if (Array.isArray(res.data?.payload)) {
      data = res.data.payload
    } else {
      data = res.data ?? res
      if (!Array.isArray(data)) data = []
    }

    // 应用搜索条件
    if (searchForm.drugName) {
      data = data.filter(item => 
        (item.drugName || '').toLowerCase().includes(searchForm.drugName.toLowerCase())
      )
    }

    if (searchForm.drugType) {
      data = data.filter(item => item.drugType === searchForm.drugType)
    }

    if (searchForm.status) {
      data = data.filter(item => item.status === searchForm.status)
    }

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
    console.error('获取药品列表失败', err)
    ElMessage.error(err.message || '获取药品列表失败')
  } finally {
    loading.value = false
  }
} 

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  initData()
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  handleSearch()
}

// 排序
const handleSortChange = (sort) => {
  sortConfig.prop = sort.prop
  sortConfig.order = sort.order
  initData()
}

// 分页
const handleSizeChange = (size) => {
  pagination.pageSize = size
  initData()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  initData()
}

// 新增药品
const handleAdd = () => {
  dialogMode.value = 'add'
  currentDrug.value = null
  dialogVisible.value = true
}

// 查看详情
const handleView = (row) => {
  dialogMode.value = 'view'
  currentDrug.value = { ...row }
  dialogVisible.value = true
}

// 编辑药品
const handleEdit = (row) => {
  dialogMode.value = 'edit'
  currentDrug.value = { ...row }
  dialogVisible.value = true
}

// 删除药品（使用 PUT /drug/update 标记为停售）
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除药品"${row.drugName}"吗？此操作会将该药品标记为停售。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await drugApi.updateDrug({ id: row.id, status: 'discontinued' })
      ElMessage.success('删除成功')
      initData()
    } catch (err) {
      console.error('删除失败', err)
      ElMessage.error(err.message || '删除失败')
    }
  }).catch(() => {
    // 用户取消
  })
} 

// 批量删除（标记为停售）
const batchDelete = () => {
  if (selection.value.length === 0) return

  ElMessageBox.confirm(
    `确定要删除选中的${selection.value.length}条记录吗？`,
    '批量删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const ids = selection.value.map(item => item.id)
      await Promise.all(ids.map(id => drugApi.updateDrug({ id, status: 'discontinued' })))
      ElMessage.success('批量删除成功')
      selection.value = []
      initData()
    } catch (err) {
      console.error('批量删除失败', err)
      ElMessage.error(err.message || '批量删除失败')
    }
  }).catch(() => {
    // 用户取消
  })
}

// 自动查找并删除测试数据（按关键字匹配 name/code/batch）
const deleteTestData = () => {
  ElMessageBox.confirm(
    '确认要自动查找并标记测试数据（名称/编码/批次匹配 test|mock|药A|DC001|BATCH1）为停售吗？',
    '删除测试数据',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    loading.value = true
    try {
      const res = await drugApi.getDrugList()
      let data = Array.isArray(res) ? res : (res.data ?? res)
      if (!Array.isArray(data)) data = []

      const pattern = /test|mock|药A|DC001|BATCH1/i
      const candidates = data.filter(item => pattern.test(item.drugName || '') || pattern.test(item.drugCode || '') || pattern.test(item.batchNo || ''))
      if (candidates.length === 0) {
        ElMessage.info('未找到匹配的测试数据')
        return
      }

      await Promise.all(candidates.map(c => drugApi.updateDrug({ id: c.id, status: 'discontinued' })))
      ElMessage.success(`已标记 ${candidates.length} 条测试数据为停售`)
      initData()
    } catch (err) {
      console.error('删除测试数据失败', err)
      ElMessage.error(err.message || '删除测试数据失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {
    // 用户取消
  })
}

// 批量停售
const batchDisable = () => {
  if (selection.value.length === 0) return
  
  ElMessageBox.confirm(
    `确定要停售选中的${selection.value.length}种药品吗？`,
    '批量停售确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('操作成功')
    selection.value = []
    initData()
  }).catch(() => {
    // 用户取消
  })
}

// 更多操作
const handleMore = (row, command) => {
  switch (command) {
    case 'stock':
      ElMessage.info(`调整库存: ${row.drugName}`)
      break
    case 'price':
      ElMessage.info(`调整价格: ${row.drugName}`)
      break
    case 'disable':
      ElMessage.info(`停售药品: ${row.drugName}`)
      break
    case 'enable':
      ElMessage.info(`启用药品: ${row.drugName}`)
      break
  }
}

// 导出数据
const exportData = async () => {
  loading.value = true
  try {
    const res = await drugApi.exportDrugList({ /* 可选参数 */ })
    // 如果后端返回文件流，保存为文件
    const blob = new Blob([res.data], { type: res.headers['content-type'] || 'application/octet-stream' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'drug_export.xlsx'
    document.body.appendChild(a)
    a.click()
    a.remove()
    window.URL.revokeObjectURL(url)
  } catch (err) {
    console.error('导出失败', err)
    ElMessage.error(err.message || '导出失败')
  } finally {
    loading.value = false
  }
}

// 刷新数据
const refreshData = async () => {
  loading.value = true
  try {
    await initData()
    ElMessage.success('数据已刷新')
  } catch (err) {
    console.error('刷新失败', err)
    ElMessage.error(err.message || '刷新失败')
  } finally {
    loading.value = false
  }
}

// 表单提交成功
const handleFormSuccess = () => {
  dialogVisible.value = false
  initData()
}

// 表单取消
const handleCancel = () => {
  dialogVisible.value = false
}

// 对话框关闭
const handleDialogClose = () => {
  if (drugFormRef.value?.hasUnsavedChanges()) {
    ElMessageBox.confirm(
      '有未保存的更改，确定要关闭吗？',
      '确认关闭',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      dialogVisible.value = false
    }).catch(() => {
      // 用户取消
    })
  } else {
    dialogVisible.value = false
  }
}

// 工具函数
const getDrugTypeText = (type) => {
  const map = {
    prescription: '处方药',
    otc: '非处方药',
    traditional: '中药',
    western: '西药',
    equipment: '医疗器械'
  }
  return map[type] || type
}

const getDrugTypeTag = (type) => {
  const map = {
    prescription: 'danger',
    otc: 'success',
    traditional: 'warning',
    western: 'info',
    equipment: 'info'
  }
  return map[type] || 'info'
}

const getStatusText = (status) => {
  const map = {
    normal: '正常',
    out_of_stock: '缺货',
    discontinued: '停售',
    expiring: '近效期'
  }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = {
    normal: 'success',
    out_of_stock: 'warning',
    discontinued: 'danger',
    expiring: 'warning'
  }
  return map[status] || 'info'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

const isExpiringSoon = (dateString) => {
  if (!dateString) return false
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = date.getTime() - now.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays > 0 && diffDays <= 90 // 90天内过期
}

const isExpired = (dateString) => {
  if (!dateString) return false
  const date = new Date(dateString)
  const now = new Date()
  return date < now
}

// 表格行样式
const tableRowClassName = ({ row }) => {
  if (row.stock === 0) {
    return 'row-danger'
  } else if (row.stock < row.minStock) {
    return 'row-warning'
  } else if (isExpiringSoon(row.expiryDate)) {
    return 'row-expiring'
  }
  return ''
}

// 生命周期
onMounted(() => {
  initData()
})
</script>

<style scoped>
.drug-list-container {
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

.search-card {
  margin-bottom: 20px;
}

.search-form .el-col {
  margin-bottom: 10px;
}

.search-buttons {
  display: flex;
  gap: 10px;
}

.table-card {
  margin-bottom: 20px;
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
  display: flex;
  align-items: center;
  gap: 5px;
}

.stock-warning {
  color: #e6a23c;
}

.stock-danger {
  color: #f56c6c;
  font-weight: bold;
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
  font-weight: bold;
}

.warning-icon {
  font-size: 14px;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.batch-actions {
  display: flex;
  gap: 10px;
}

/* 表格行样式 */
:deep(.row-danger) {
  background-color: #fef0f0;
}

:deep(.row-warning) {
  background-color: #fdf6ec;
}

:deep(.row-expiring) {
  background-color: #fcf8e3;
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
  
  .search-buttons {
    width: 100%;
  }
  
  .search-buttons .el-button {
    flex: 1;
  }
  
  .pagination-container {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .batch-actions {
    justify-content: center;
  }
}
</style>