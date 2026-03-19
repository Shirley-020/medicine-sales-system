<!-- src/views/purchase/PurchaseHistory.vue -->
<template>
  <div class="purchase-history-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>进货单历史记录</h2>
      <p class="subtitle">查看、筛选和搜索历史进货记录</p>
    </div>

    <!-- 查询筛选区域 -->
    <el-card class="filter-card">
      <el-form :model="queryParams" label-width="80px">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="进货单号">
              <el-input
                v-model="queryParams.orderNo"
                placeholder="请输入进货单号"
                clearable
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="供应商">
              <el-select
                v-model="queryParams.supplierId"
                placeholder="请选择供应商"
                style="width: 100%"
                clearable
                filterable
              >
                <el-option
                  v-for="supplier in supplierList"
                  :key="supplier.id"
                  :label="supplier.name"
                  :value="supplier.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="进货日期">
              <el-date-picker
                v-model="queryParams.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="进货类型">
              <el-select
                v-model="queryParams.purchaseType"
                placeholder="请选择进货类型"
                style="width: 100%"
                clearable
              >
                <el-option label="常规进货" value="normal" />
                <el-option label="紧急补货" value="urgent" />
                <el-option label="计划采购" value="planned" />
                <el-option label="代销进货" value="consignment" />
                <el-option label="退货入库" value="return" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="结算方式">
              <el-select
                v-model="queryParams.paymentMethod"
                placeholder="请选择结算方式"
                style="width: 100%"
                clearable
              >
                <el-option label="现金" value="cash" />
                <el-option label="银行转账" value="bank" />
                <el-option label="支付宝" value="alipay" />
                <el-option label="微信支付" value="wechat" />
                <el-option label="赊账" value="credit" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="仓库">
              <el-select
                v-model="queryParams.warehouse"
                placeholder="请选择仓库"
                style="width: 100%"
                clearable
              >
                <el-option label="主仓库" value="main" />
                <el-option label="备用仓库" value="backup" />
                <el-option label="冷库" value="cold" />
                <el-option label="西药库" value="western" />
                <el-option label="中药库" value="traditional" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="状态">
              <el-select
                v-model="queryParams.status"
                placeholder="请选择状态"
                style="width: 100%"
                clearable
              >
                <el-option label="待入库" value="pending" />
                <el-option label="已入库" value="completed" />
                <el-option label="已取消" value="cancelled" />
                <el-option label="部分入库" value="partial" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="6">
            <div class="filter-actions">
              <el-button type="primary" @click="handleSearch" :icon="Search">
                查询
              </el-button>
              <el-button @click="resetQuery" :icon="Refresh">
                重置
              </el-button>
              <el-button @click="exportData" :icon="Download">
                导出
              </el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 统计数据 -->
    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #409EFF;">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.totalOrders }}</div>
                <div class="stat-label">总进货单数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #67C23A;">
                <el-icon><Coin /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">¥{{ statistics.totalAmount.toFixed(2) }}</div>
                <div class="stat-label">总进货金额</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #E6A23C;">
                <el-icon><Box /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.totalQuantity }}</div>
                <div class="stat-label">总进货数量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #F56C6C;">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.totalSuppliers }}</div>
                <div class="stat-label">供应商数量</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 进货单列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <span class="table-title">进货单列表</span>
          <div class="table-actions">
            <el-button @click="refreshList" :icon="Refresh">
              刷新
            </el-button>
            <el-button @click="goToNewOrder" type="primary" :icon="Plus">
              新增进货单
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="orderList"
        v-loading="loading"
        style="width: 100%"
        @sort-change="handleSortChange"
        border
        stripe
      >
        <el-table-column label="进货单号" width="180" prop="orderNo" sortable>
          <template #default="{ row }">
            <div class="order-no-cell">
              <span class="order-no">{{ row.orderNo }}</span>
              <el-tag v-if="row.status === 'pending'" size="small" type="warning">待入库</el-tag>
              <el-tag v-if="row.status === 'completed'" size="small" type="success">已入库</el-tag>
              <el-tag v-if="row.status === 'cancelled'" size="small" type="danger">已取消</el-tag>
              <el-tag v-if="row.status === 'partial'" size="small" type="info">部分入库</el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="供应商" width="180" prop="supplierName">
          <template #default="{ row }">
            <div class="supplier-cell">
              <div class="supplier-name">{{ row.supplierName }}</div>
              <div class="supplier-contact">{{ row.supplierContact }}</div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="进货日期" width="120" prop="purchaseDate" sortable>
          <template #default="{ row }">
            <div>{{ formatDate(row.purchaseDate) }}</div>
          </template>
        </el-table-column>
        
        <el-table-column label="到货日期" width="120" prop="expectedDate">
          <template #default="{ row }">
            <div>{{ formatDate(row.expectedDate) }}</div>
          </template>
        </el-table-column>
        
        <el-table-column label="采购员" width="100" prop="handler"></el-table-column>
        
        <el-table-column label="药品品种" width="100" prop="drugCount" sortable>
          <template #default="{ row }">
            <span>{{ row.drugCount }} 种</span>
          </template>
        </el-table-column>
        
        <el-table-column label="总数量" width="100" prop="totalQuantity" sortable>
          <template #default="{ row }">
            <span>{{ row.totalQuantity }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="总金额" width="130" prop="totalAmount" sortable>
          <template #default="{ row }">
            <div class="amount-cell">¥{{ row.totalAmount.toFixed(2) }}</div>
          </template>
        </el-table-column>
        
        <el-table-column label="进货类型" width="100" prop="purchaseType">
          <template #default="{ row }">
            <el-tag :type="getPurchaseTypeTag(row.purchaseType)">
              {{ getPurchaseTypeText(row.purchaseType) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="结算方式" width="100" prop="paymentMethod">
          <template #default="{ row }">
            <span>{{ getPaymentMethodText(row.paymentMethod) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="仓库" width="100" prop="warehouse">
          <template #default="{ row }">
            <span>{{ getWarehouseText(row.warehouse) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="创建时间" width="150" prop="createdAt" sortable>
          <template #default="{ row }">
            <div>{{ formatDateTime(row.createdAt) }}</div>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="primary"
                link
                @click="viewOrderDetail(row)"
                :icon="View"
                size="small"
              >
                查看
              </el-button>
              <el-button
                type="success"
                link
                @click="editOrder(row)"
                :icon="Edit"
                size="small"
                v-if="row.status === 'pending'"
              >
                编辑
              </el-button>
              <el-button
                type="danger"
                link
                @click="deleteOrder(row)"
                :icon="Delete"
                size="small"
                v-if="row.status === 'pending'"
              >
                删除
              </el-button>
              <el-button
                type="warning"
                link
                @click="confirmReceipt(row)"
                :icon="Check"
                size="small"
                v-if="row.status === 'pending'"
              >
                确认入库
              </el-button>
              <el-button
                type="info"
                link
                @click="printOrder(row)"
                :icon="Printer"
                size="small"
              >
                打印
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.limit"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 进货单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="'进货单详情 - ' + ((currentOrder && currentOrder.orderNo) || '')"
      width="80%"
      top="5vh"
    >
      <order-detail :order="currentOrder" v-if="currentOrder" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="printOrder(currentOrder)" :icon="Printer">
            打印
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Refresh, Download, Document,
  Coin, Box, User, Plus, View,
  Edit, Delete, Check, Printer
} from '@element-plus/icons-vue'
import purchaseApi from '@/api/purchase' 

// 导入订单详情组件（稍后创建）
import OrderDetail from './components/OrderDetail.vue'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  orderNo: '',
  supplierId: '',
  dateRange: [],
  purchaseType: '',
  paymentMethod: '',
  warehouse: '',
  status: '',
  page: 1,
  limit: 10,
  sortField: 'createdAt',
  sortOrder: 'descending'
})

// 订单列表
const orderList = ref([])

// 供应商列表
const supplierList = ref([])

// 当前选中的订单
const currentOrder = ref(null)

// 对话框状态
const detailDialogVisible = ref(false)

// 加载状态
const loading = ref(false)

// 总数
const total = ref(0)

// 统计信息
const statistics = reactive({
  totalOrders: 0,
  totalAmount: 0,
  totalQuantity: 0,
  totalSuppliers: 0
})

// 初始化数据
onMounted(() => {
  loadSuppliers()
  loadOrderList()
  calculateStatistics()
})

// 加载供应商数据
const loadSuppliers = async () => {
  try {
    const res = await purchaseApi.getSuppliers()
    const data = res.data ?? res
    const list = data.data ?? data
    supplierList.value = Array.isArray(list) ? list : (list.items ?? [])
  } catch (err) {
    console.error('加载供应商失败', err)
    ElMessage.error(err.message || '加载供应商失败')
    supplierList.value = []
  }
  calculateStatistics()
}

// 加载订单列表
const loadOrderList = async () => {
  loading.value = true
  try {
    const params = {
      orderNo: queryParams.orderNo,
      supplierId: queryParams.supplierId,
      start: queryParams.dateRange?.[0],
      end: queryParams.dateRange?.[1],
      purchaseType: queryParams.purchaseType,
      paymentMethod: queryParams.paymentMethod,
      warehouse: queryParams.warehouse,
      status: queryParams.status,
      page: queryParams.page,
      limit: queryParams.limit,
      sortField: queryParams.sortField,
      sortOrder: queryParams.sortOrder
    }

    const res = await purchaseApi.getPurchaseList(params)
    
    // 后端返回格式：{code, message, data: {items, total, page, limit}}
    const responseData = res.data ?? res
    
    if (responseData && responseData.items) {
      // 后端已分页
      orderList.value = responseData.items || []
      total.value = responseData.total || 0
    } else if (Array.isArray(responseData)) {
      // 如果是数组（兼容旧格式）
      orderList.value = responseData
      total.value = responseData.length
    } else {
      orderList.value = []
      total.value = 0
    }

    // 更新统计信息
    calculateStatistics()
  } catch (err) {
    console.error('加载进货历史失败', err)
    ElMessage.error(err.message || '加载进货历史失败')
  } finally {
    loading.value = false
  }
}

// 计算统计信息
const calculateStatistics = () => {
  statistics.totalOrders = total.value || 0
  statistics.totalAmount = orderList.value.reduce((sum, o) => sum + (o.totalAmount || 0), 0)
  statistics.totalQuantity = orderList.value.reduce((sum, o) => sum + (o.totalQuantity || 0), 0)
  statistics.totalSuppliers = supplierList.value.length
}

// 查询操作
const handleSearch = () => {
  queryParams.page = 1
  loadOrderList()
}

// 重置查询
const resetQuery = () => {
  Object.assign(queryParams, {
    orderNo: '',
    supplierId: '',
    dateRange: [],
    purchaseType: '',
    paymentMethod: '',
    warehouse: '',
    status: '',
    page: 1
  })
  loadOrderList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  queryParams.limit = size
  loadOrderList()
}

// 页码变化
const handleCurrentChange = (page) => {
  queryParams.page = page
  loadOrderList()
}

// 排序变化
const handleSortChange = ({ prop, order }) => {
  queryParams.sortField = prop
  queryParams.sortOrder = order
  loadOrderList()
}

// 查看订单详情
const viewOrderDetail = (order) => {
  currentOrder.value = order
  detailDialogVisible.value = true
}

// 编辑订单
const editOrder = (order) => {
  ElMessageBox.confirm(
    '确定要编辑这个进货单吗？',
    '编辑确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 跳转到编辑页面，这里可以先跳转到进货单页面并携带ID
    router.push({
      path: '/purchase/order',
      query: { id: order.id }
    })
  })
}

// 删除订单
const deleteOrder = (order) => {
  ElMessageBox.confirm(
    `确定要删除进货单 ${order.orderNo} 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    loading.value = true
    try {
      await purchaseApi.deletePurchase(order.id)
      ElMessage.success('进货单删除成功')
      await loadOrderList()
      calculateStatistics()
    } catch (err) {
      console.error('删除进货单失败', err)
      ElMessage.error(err.message || '删除进货单失败')
    } finally {
      loading.value = false
    }
  })
}

// 确认入库
const confirmReceipt = (order) => {
  ElMessageBox.confirm(
    `确定要确认进货单 ${order.orderNo} 已入库吗？`,
    '入库确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    loading.value = true
    try {
      await purchaseApi.confirmReceipt(order.id)
      ElMessage.success('进货单已确认入库')
      await loadOrderList()
    } catch (err) {
      console.error('确认入库失败', err)
      ElMessage.error(err.message || '确认入库失败')
    } finally {
      loading.value = false
    }
  })
}

// 打印订单
const printOrder = (order) => {
  ElMessage.info(`打印进货单: ${order.orderNo}`)
  // 实际开发中可以打开打印预览窗口
}

// 导出数据
const exportData = () => {
  ElMessage.info('导出功能开发中...')
}

// 刷新列表
const refreshList = () => {
  loadOrderList()
  calculateStatistics()
}

// 跳转到新建订单页面
const goToNewOrder = () => {
  router.push('/purchase/order')
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toISOString().split('T')[0]
}

// 格式化日期时间
const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return ''
  const date = new Date(dateTimeString)
  return date.toLocaleString('zh-CN')
}

// 获取进货类型标签
const getPurchaseTypeTag = (type) => {
  if (!type) return 'info'
  const tagMap = {
    normal: 'info',
    urgent: 'danger',
    planned: 'success',
    consignment: 'warning',
    return: 'info'
  }
  return tagMap[type] || 'info'
}

// 获取进货类型文本
const getPurchaseTypeText = (type) => {
  const textMap = {
    normal: '常规进货',
    urgent: '紧急补货',
    planned: '计划采购',
    consignment: '代销进货',
    return: '退货入库'
  }
  return textMap[type] || type
}

// 获取结算方式文本
const getPaymentMethodText = (method) => {
  const textMap = {
    cash: '现金',
    bank: '银行转账',
    alipay: '支付宝',
    wechat: '微信支付',
    credit: '赊账'
  }
  return textMap[method] || method
}

// 获取仓库文本
const getWarehouseText = (warehouse) => {
  const textMap = {
    main: '主仓库',
    backup: '备用仓库',
    cold: '冷库',
    western: '西药库',
    traditional: '中药库'
  }
  return textMap[warehouse] || warehouse
}
</script>

<style scoped>
.purchase-history-container {
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

.filter-card {
  margin-bottom: 20px;
}

.filter-actions {
  display: flex;
  gap: 10px;
  padding-top: 28px; /* 对齐表单项 */
}

.stats-container {
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
  margin-right: 16px;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.table-card {
  margin-bottom: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.table-actions {
  display: flex;
  gap: 10px;
}

.order-no-cell {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.order-no {
  font-weight: 500;
  color: #409EFF;
}

.supplier-cell {
  display: flex;
  flex-direction: column;
}

.supplier-name {
  font-weight: 500;
  margin-bottom: 2px;
}

.supplier-contact {
  font-size: 12px;
  color: #666;
}

.amount-cell {
  font-weight: bold;
  color: #e6a23c;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-actions {
    padding-top: 0;
    justify-content: center;
  }
  
  .table-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .table-actions {
    width: 100%;
    flex-direction: column;
  }
  
  .table-actions .el-button {
    width: 100%;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>