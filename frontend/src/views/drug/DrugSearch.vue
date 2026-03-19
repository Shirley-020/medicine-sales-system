<!-- src/views/drug/DrugSearch.vue -->
<template>
  <div class="drug-search-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>药品高级搜索</h2>
      <p class="subtitle">支持多条件组合查询，快速定位所需药品</p>
    </div>

    <!-- 高级搜索表单 -->
    <el-card class="search-form-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">搜索条件</span>
          <div class="header-actions">
            <el-button type="primary" link @click="toggleExpandAll">
              {{ isAllExpanded ? '收起全部' : '展开全部' }}
            </el-button>
            <el-button type="primary" link @click="resetForm">
              重置条件
            </el-button>
          </div>
        </div>
      </template>

      <el-form
        ref="searchFormRef"
        :model="searchForm"
        label-width="100px"
        class="advanced-search-form"
      >
        <!-- 第一行：基本条件 -->
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="药品名称">
              <el-input
                v-model="searchForm.drugName"
                placeholder="请输入药品名称"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="药品编码">
              <el-input
                v-model="searchForm.drugCode"
                placeholder="请输入药品编码"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="通用名">
              <el-input
                v-model="searchForm.genericName"
                placeholder="请输入通用名"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="生产厂家">
              <el-input
                v-model="searchForm.manufacturer"
                placeholder="请输入生产厂家"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第二行：分类条件 -->
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="药品类型">
              <el-select
                v-model="searchForm.drugType"
                placeholder="请选择药品类型"
                clearable
                multiple
                collapse-tags
                collapse-tags-tooltip
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
            <el-form-item label="药品状态">
              <el-select
                v-model="searchForm.status"
                placeholder="请选择状态"
                clearable
                multiple
                collapse-tags
                collapse-tags-tooltip
              >
                <el-option label="正常" value="normal" />
                <el-option label="缺货" value="out_of_stock" />
                <el-option label="停售" value="discontinued" />
                <el-option label="近效期" value="expiring" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="是否处方药">
              <el-select
                v-model="searchForm.isPrescription"
                placeholder="请选择"
                clearable
              >
                <el-option label="是" :value="true" />
                <el-option label="否" :value="false" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="单位">
              <el-select
                v-model="searchForm.unit"
                placeholder="请选择单位"
                clearable
                multiple
                collapse-tags
                collapse-tags-tooltip
              >
                <el-option label="盒" value="box" />
                <el-option label="瓶" value="bottle" />
                <el-option label="支" value="piece" />
                <el-option label="袋" value="bag" />
                <el-option label="粒" value="grain" />
                <el-option label="片" value="tablet" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 展开的更多条件 -->
        <el-collapse v-model="activeCollapse">
          <!-- 价格范围条件 -->
          <el-collapse-item title="价格范围" name="price">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="最低价格">
                  <el-input-number
                    v-model="searchForm.minPrice"
                    :min="0"
                    :max="searchForm.maxPrice || 99999"
                    :precision="2"
                    placeholder="最低价"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="最高价格">
                  <el-input-number
                    v-model="searchForm.maxPrice"
                    :min="searchForm.minPrice || 0"
                    :max="99999"
                    :precision="2"
                    placeholder="最高价"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>

          <!-- 库存范围条件 -->
          <el-collapse-item title="库存范围" name="stock">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="最低库存">
                  <el-input-number
                    v-model="searchForm.minStock"
                    :min="0"
                    :max="searchForm.maxStock || 9999"
                    placeholder="最低库存"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="最高库存">
                  <el-input-number
                    v-model="searchForm.maxStock"
                    :min="searchForm.minStock || 0"
                    :max="9999"
                    placeholder="最高库存"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>

          <!-- 有效期条件 -->
          <el-collapse-item title="有效期" name="expiry">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="有效期从">
                  <el-date-picker
                    v-model="searchForm.expiryDateStart"
                    type="date"
                    placeholder="开始日期"
                    style="width: 100%"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="有效期至">
                  <el-date-picker
                    v-model="searchForm.expiryDateEnd"
                    type="date"
                    placeholder="结束日期"
                    style="width: 100%"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>

          <!-- 创建时间条件 -->
          <el-collapse-item title="创建时间" name="createTime">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="创建时间从">
                  <el-date-picker
                    v-model="searchForm.createTimeStart"
                    type="date"
                    placeholder="开始日期"
                    style="width: 100%"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="创建时间至">
                  <el-date-picker
                    v-model="searchForm.createTimeEnd"
                    type="date"
                    placeholder="结束日期"
                    style="width: 100%"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
        </el-collapse>

        <!-- 搜索按钮 -->
        <div class="form-actions">
          <el-button
            type="primary"
            @click="handleSearch"
            :icon="Search"
            :loading="loading"
            class="search-button"
          >
            搜索
          </el-button>
          <el-button @click="resetForm" :icon="Refresh">
            重置
          </el-button>
          <el-button @click="exportResults" :icon="Download">
            导出结果
          </el-button>
          <el-button type="success" @click="saveSearchTemplate" :icon="Star">
            保存为模板
          </el-button>
        </div>
      </el-form>
    </el-card>

    <!-- 搜索结果 -->
    <div class="search-results-section">
      <div class="results-header">
        <div class="results-info">
          <span class="results-count">
            找到 <span class="count-number">{{ pagination.total }}</span> 条结果
          </span>
          <span class="search-time" v-if="lastSearchTime">
            搜索用时 {{ lastSearchTime }}ms
          </span>
        </div>
        <div class="results-actions">
          <el-button-group>
            <el-button @click="changeViewMode('table')" :type="viewMode === 'table' ? 'primary' : ''">
              <el-icon><Grid /></el-icon> 表格视图
            </el-button>
            <el-button @click="changeViewMode('card')" :type="viewMode === 'card' ? 'primary' : ''">
              <el-icon><CreditCard /></el-icon> 卡片视图
            </el-button>
          </el-button-group>
        </div>
      </div>

      <!-- 表格视图 -->
      <div v-if="viewMode === 'table'" class="results-table">
        <el-table
          :data="searchResults"
          style="width: 100%"
          v-loading="loading"
          @sort-change="handleSortChange"
          :default-sort="{ prop: 'drugName', order: 'ascending' }"
        >
          <el-table-column prop="drugCode" label="药品编码" width="120" sortable />
          <el-table-column prop="drugName" label="药品名称" width="180" sortable show-overflow-tooltip />
          <el-table-column prop="genericName" label="通用名" width="150" show-overflow-tooltip />
          <el-table-column prop="drugType" label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getDrugTypeTag(row.drugType)" size="small">
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
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价(元)" width="100" sortable>
            <template #default="{ row }">
              ¥{{ row.price.toFixed(2) }}
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
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="viewDetail(row)">
                详情
              </el-button>
              <el-button type="success" link @click="editDrug(row)">
                编辑
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
      </div>

      <!-- 卡片视图 -->
      <div v-else class="results-cards">
        <el-row :gutter="20">
          <el-col
            v-for="drug in paginatedResults"
            :key="drug.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
            class="card-col"
          >
            <el-card class="drug-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <h4 class="drug-name">{{ drug.drugName }}</h4>
                  <el-tag
                    :type="getDrugTypeTag(drug.drugType)"
                    size="small"
                    effect="plain"
                  >
                    {{ getDrugTypeText(drug.drugType) }}
                  </el-tag>
                </div>
              </template>
              
              <div class="card-content">
                <div class="drug-info-item">
                  <span class="label">药品编码：</span>
                  <span class="value">{{ drug.drugCode }}</span>
                </div>
                <div class="drug-info-item">
                  <span class="label">通用名：</span>
                  <span class="value">{{ drug.genericName }}</span>
                </div>
                <div class="drug-info-item">
                  <span class="label">规格：</span>
                  <span class="value">{{ drug.specification }}</span>
                </div>
                <div class="drug-info-item">
                  <span class="label">生产厂家：</span>
                  <span class="value">{{ drug.manufacturer }}</span>
                </div>
                <div class="drug-info-item">
                  <span class="label">库存：</span>
                  <span :class="{
                    'value': true,
                    'stock-warning': drug.stock < drug.minStock,
                    'stock-danger': drug.stock === 0
                  }">
                    {{ drug.stock }} {{ drug.unit }}
                  </span>
                </div>
                <div class="drug-info-item">
                  <span class="label">单价：</span>
                  <span class="value price">¥{{ drug.price.toFixed(2) }}</span>
                </div>
                <div class="drug-info-item">
                  <span class="label">有效期：</span>
                  <span :class="{
                    'value': true,
                    'expiry-warning': isExpiringSoon(drug.expiryDate),
                    'expiry-danger': isExpired(drug.expiryDate)
                  }">
                    {{ formatDate(drug.expiryDate) }}
                  </span>
                </div>
                <div class="drug-info-item">
                  <span class="label">状态：</span>
                  <el-tag :type="getStatusTag(drug.status)" size="small">
                    {{ getStatusText(drug.status) }}
                  </el-tag>
                </div>
              </div>
              
              <template #footer>
                <div class="card-footer">
                  <el-button type="primary" link @click="viewDetail(drug)">
                    详情
                  </el-button>
                  <el-button type="success" link @click="editDrug(drug)">
                    编辑
                  </el-button>
                  <el-button type="warning" link @click="adjustStock(drug)">
                    库存
                  </el-button>
                </div>
              </template>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[8, 16, 24, 32]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 搜索历史 -->
    <el-card class="search-history-card" v-if="searchHistory.length > 0">
      <template #header>
        <span class="history-title">搜索历史</span>
      </template>
      <div class="history-list">
        <el-tag
          v-for="(history, index) in searchHistory"
          :key="index"
          class="history-tag"
          closable
          @close="removeHistory(index)"
          @click="loadHistory(history)"
        >
          {{ history.name || '未命名搜索' }}
          <span class="history-time">{{ formatTime(history.time) }}</span>
        </el-tag>
      </div>
      <div class="history-actions">
        <el-button link @click="clearAllHistory">清空历史</el-button>
      </div>
    </el-card>

    <!-- 搜索模板对话框 -->
    <el-dialog
      v-model="templateDialogVisible"
      title="保存搜索模板"
      width="500px"
    >
      <el-form :model="templateForm" label-width="80px">
        <el-form-item label="模板名称" prop="name">
          <el-input
            v-model="templateForm.name"
            placeholder="请输入模板名称"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="templateForm.description"
            type="textarea"
            placeholder="请输入模板描述"
            :rows="3"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="templateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveTemplate">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Refresh, Download, Star, Grid, CreditCard
} from '@element-plus/icons-vue'
import drugApi from '@/api/drug'

const router = useRouter()

// 搜索表单引用
const searchFormRef = ref(null)

// 搜索表单数据
const searchForm = reactive({
  drugName: '',
  drugCode: '',
  genericName: '',
  manufacturer: '',
  drugType: [],
  status: [],
  isPrescription: null,
  unit: [],
  minPrice: null,
  maxPrice: null,
  minStock: null,
  maxStock: null,
  expiryDateStart: '',
  expiryDateEnd: '',
  createTimeStart: '',
  createTimeEnd: ''
})

// 折叠面板活动项
const activeCollapse = ref(['price', 'stock', 'expiry', 'createTime'])

// 视图模式：table 或 card
const viewMode = ref('table')

// 加载状态
const loading = ref(false)

// 搜索结果
const searchResults = ref([])

// 搜索历史
const searchHistory = ref([])

// 最后搜索用时
const lastSearchTime = ref(0)

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

// 模板对话框
const templateDialogVisible = ref(false)
const templateForm = reactive({
  name: '',
  description: ''
})

// 从后端获取药品列表并返回数组（兼容多种返回结构）
const fetchDrugList = async () => {
  try {
    const res = await drugApi.getDrugList()
    let data = []
    if (Array.isArray(res)) {
      data = res
    } else if (Array.isArray(res.data)) {
      data = res.data
    } else if (Array.isArray(res.data?.data)) {
      data = res.data.data
    } else {
      data = res.data ?? res
      if (!Array.isArray(data)) data = []
    }
    return data
  } catch (err) {
    console.error('获取药品失败', err)
    ElMessage.error(err.message || '获取药品失败')
    return []
  }
}

// 计算分页后的结果
const paginatedResults = computed(() => {
  const start = (pagination.currentPage - 1) * pagination.pageSize
  const end = start + pagination.pageSize
  return searchResults.value.slice(start, end)
})

// 检查是否所有折叠面板都展开
const isAllExpanded = computed(() => {
  return activeCollapse.value.length === 4
})

// 切换所有折叠面板
const toggleExpandAll = () => {
  if (isAllExpanded.value) {
    activeCollapse.value = []
  } else {
    activeCollapse.value = ['price', 'stock', 'expiry', 'createTime']
  }
}

// 搜索函数（调用后端 /drug/list，然后在前端做过滤/分页以兼容后端不支持复杂查询的情况）
const handleSearch = async () => {
  loading.value = true
  const startTime = Date.now()

  try {
    let data = await fetchDrugList()

    // 应用搜索条件（与之前逻辑一致）
    if (searchForm.drugName) {
      data = data.filter(item =>
        (item.drugName || '').toLowerCase().includes(searchForm.drugName.toLowerCase())
      )
    }

    if (searchForm.drugCode) {
      data = data.filter(item =>
        (item.drugCode || '').includes(searchForm.drugCode)
      )
    }

    if (searchForm.genericName) {
      data = data.filter(item =>
        (item.genericName || '').includes(searchForm.genericName)
      )
    }

    if (searchForm.manufacturer) {
      data = data.filter(item =>
        (item.manufacturer || '').includes(searchForm.manufacturer)
      )
    }

    if (searchForm.drugType.length > 0) {
      data = data.filter(item =>
        searchForm.drugType.includes(item.drugType)
      )
    }

    if (searchForm.status.length > 0) {
      data = data.filter(item =>
        searchForm.status.includes(item.status)
      )
    }

    if (searchForm.isPrescription !== null) {
      data = data.filter(item =>
        item.isPrescription === searchForm.isPrescription
      )
    }

    if (searchForm.unit.length > 0) {
      data = data.filter(item =>
        searchForm.unit.includes(item.unit)
      )
    }

    if (searchForm.minPrice !== null) {
      data = data.filter(item => item.price >= searchForm.minPrice)
    }
    
    if (searchForm.maxPrice !== null) {
      data = data.filter(item => item.price <= searchForm.maxPrice)
    }
    
    if (searchForm.minStock !== null) {
      data = data.filter(item => item.stock >= searchForm.minStock)
    }
    
    if (searchForm.maxStock !== null) {
      data = data.filter(item => item.stock <= searchForm.maxStock)
    }
    
    if (searchForm.expiryDateStart) {
      data = data.filter(item => item.expiryDate >= searchForm.expiryDateStart)
    }
    
    if (searchForm.expiryDateEnd) {
      data = data.filter(item => item.expiryDate <= searchForm.expiryDateEnd)
    }
    
    if (searchForm.createTimeStart) {
      data = data.filter(item => item.createTime >= searchForm.createTimeStart)
    }
    
    if (searchForm.createTimeEnd) {
      data = data.filter(item => item.createTime <= searchForm.createTimeEnd)
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
    
    // 更新结果
    searchResults.value = data
    pagination.total = data.length
    pagination.currentPage = 1
    
    // 计算搜索用时
    lastSearchTime.value = Date.now() - startTime
    
    // 保存到搜索历史
    saveToHistory()
  } catch (err) {
    console.error('搜索失败', err)
    ElMessage.error(err.message || '搜索失败')
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  if (searchFormRef.value) {
    searchFormRef.value.resetFields()
  }
  // 重置搜索条件
  Object.keys(searchForm).forEach(key => {
    if (Array.isArray(searchForm[key])) {
      searchForm[key] = []
    } else if (typeof searchForm[key] === 'number') {
      searchForm[key] = null
    } else {
      searchForm[key] = ''
    }
  })
}

// 导出结果
const exportResults = async () => {
  if (searchResults.value.length === 0) {
    ElMessage.warning('没有数据可以导出')
    return
  }

  loading.value = true
  try {
    const params = { ...searchForm }
    const res = await drugApi.exportDrugList(params)
    const blob = new Blob([res.data], { type: res.headers['content-type'] || 'application/octet-stream' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'drug_search_export.xlsx'
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

// 保存搜索模板
const saveSearchTemplate = () => {
  templateDialogVisible.value = true
  templateForm.name = ''
  templateForm.description = ''
}

// 保存模板
const saveTemplate = () => {
  if (!templateForm.name.trim()) {
    ElMessage.warning('请输入模板名称')
    return
  }

  try {
    const templates = JSON.parse(localStorage.getItem('drug_search_templates') || '[]')
    templates.unshift({
      id: Date.now(),
      name: templateForm.name,
      description: templateForm.description,
      form: JSON.parse(JSON.stringify(searchForm)),
      createdAt: new Date().toISOString()
    })
    localStorage.setItem('drug_search_templates', JSON.stringify(templates))
    ElMessage.success('模板保存成功')
    templateDialogVisible.value = false
  } catch (err) {
    console.error('保存模板失败', err)
    ElMessage.error(err.message || '保存模板失败')
  }
}

// 查看详情
const viewDetail = (drug) => {
  ElMessage.info(`查看药品详情: ${drug.drugName}`)
  // 实际开发中可以跳转到详情页或打开对话框
}

// 编辑药品
const editDrug = (drug) => {
  ElMessage.info(`编辑药品: ${drug.drugName}`)
  // 可以跳转到编辑页面或打开编辑对话框
}

// 调整库存
const adjustStock = (drug) => {
  ElMessage.info(`调整库存: ${drug.drugName}`)
}

// 更改视图模式
const changeViewMode = (mode) => {
  viewMode.value = mode
  // 根据视图模式调整分页大小
  if (mode === 'card') {
    pagination.pageSize = 8
  } else {
    pagination.pageSize = 10
  }
  handleSizeChange(pagination.pageSize)
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
}

// 排序处理
const handleSortChange = (sort) => {
  sortConfig.prop = sort.prop
  sortConfig.order = sort.order
  handleSearch() // 重新搜索以应用排序
}

// 保存到搜索历史
const saveToHistory = () => {
  const historyItem = {
    id: Date.now(),
    name: '搜索于 ' + new Date().toLocaleTimeString(),
    form: JSON.parse(JSON.stringify(searchForm)),
    resultCount: searchResults.value.length,
    time: new Date()
  }
  
  // 添加到历史记录
  searchHistory.value.unshift(historyItem)
  
  // 限制历史记录数量
  if (searchHistory.value.length > 10) {
    searchHistory.value = searchHistory.value.slice(0, 10)
  }
  
  // 保存到本地存储
  localStorage.setItem('drugSearchHistory', JSON.stringify(searchHistory.value))
}

// 加载历史记录
const loadHistory = (history) => {
  // 恢复搜索表单
  Object.keys(history.form).forEach(key => {
    if (searchForm.hasOwnProperty(key)) {
      searchForm[key] = history.form[key]
    }
  })
  
  // 执行搜索
  handleSearch()
  
  ElMessage.success('已加载历史搜索')
}

// 删除历史记录
const removeHistory = (index) => {
  searchHistory.value.splice(index, 1)
  localStorage.setItem('drugSearchHistory', JSON.stringify(searchHistory.value))
}

// 清空所有历史记录
const clearAllHistory = () => {
  ElMessageBox.confirm(
    '确定要清空所有搜索历史吗？',
    '确认清空',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    searchHistory.value = []
    localStorage.removeItem('drugSearchHistory')
    ElMessage.success('已清空搜索历史')
  })
}

// 加载搜索历史
const loadSearchHistory = () => {
  const savedHistory = localStorage.getItem('drugSearchHistory')
  if (savedHistory) {
    try {
      searchHistory.value = JSON.parse(savedHistory).map(item => ({
        ...item,
        time: new Date(item.time)
      }))
    } catch (error) {
      console.error('加载搜索历史失败:', error)
    }
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

const formatTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const isExpiringSoon = (dateString) => {
  if (!dateString) return false
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = date.getTime() - now.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays > 0 && diffDays <= 90
}

const isExpired = (dateString) => {
  if (!dateString) return false
  const date = new Date(dateString)
  const now = new Date()
  return date < now
}

// 初始化
onMounted(() => {
  // 加载搜索历史
  loadSearchHistory()
  
  // 初始搜索
  handleSearch()
})
</script>

<style scoped>
.drug-search-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.subtitle {
  margin: 5px 0 0;
  color: #666;
  font-size: 14px;
}

.search-form-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.advanced-search-form {
  margin-top: 10px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
}

.search-button {
  padding: 10px 30px;
}

.search-results-section {
  margin-top: 30px;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.results-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.results-count {
  font-size: 16px;
  color: #333;
}

.count-number {
  color: #409EFF;
  font-weight: bold;
}

.search-time {
  font-size: 14px;
  color: #666;
}

.results-table {
  margin-bottom: 20px;
}

.results-cards {
  margin-bottom: 20px;
}

.card-col {
  margin-bottom: 20px;
}

.drug-card {
  height: 100%;
}

.drug-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.drug-name {
  margin: 0;
  font-size: 16px;
  color: #333;
  flex: 1;
  margin-right: 10px;
}

.card-content {
  padding: 10px 0;
}

.drug-info-item {
  display: flex;
  margin-bottom: 8px;
  font-size: 13px;
}

.drug-info-item .label {
  color: #666;
  width: 70px;
  flex-shrink: 0;
}

.drug-info-item .value {
  color: #333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
}

.drug-info-item .price {
  color: #e6a23c;
  font-weight: bold;
}

.stock-warning {
  color: #e6a23c;
  font-weight: bold;
}

.stock-danger {
  color: #f56c6c;
  font-weight: bold;
}

.expiry-warning {
  color: #e6a23c;
  font-weight: bold;
}

.expiry-danger {
  color: #f56c6c;
  font-weight: bold;
}

.card-footer {
  display: flex;
  justify-content: space-around;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.search-history-card {
  margin-top: 30px;
}

.history-title {
  font-size: 16px;
  font-weight: 500;
}

.history-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.history-tag {
  cursor: pointer;
  padding: 5px 10px;
}

.history-time {
  margin-left: 5px;
  font-size: 12px;
  color: #999;
}

.history-actions {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-actions {
    flex-direction: column;
  }
  
  .search-button {
    width: 100%;
  }
  
  .results-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .results-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .card-content .drug-info-item {
    flex-direction: column;
  }
  
  .drug-info-item .label {
    width: auto;
    margin-bottom: 2px;
  }
}
</style>