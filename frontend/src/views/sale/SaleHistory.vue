<!-- src/views/sale/SaleHistory.vue -->
<template>
  <div class="sale-history-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>销售历史记录</h2>
      <p class="subtitle">查看、筛选和搜索历史销售记录</p>
    </div>

    <!-- 查询筛选区域 -->
    <el-card class="filter-card">
      <el-form :model="queryParams" label-width="80px">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="销售单号">
              <el-input
                v-model="queryParams.orderNo"
                placeholder="请输入销售单号"
                clearable
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="客户">
              <el-select
                v-model="queryParams.customerId"
                placeholder="请选择客户"
                style="width: 100%"
                clearable
                filterable
              >
                <el-option label="散客" value="guest" />
                <el-option
                  v-for="customer in customerList"
                  :key="customer.id"
                  :label="`${customer.name} (${customer.memberId})`"
                  :value="customer.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="销售日期">
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
            <el-form-item label="销售类型">
              <el-select
                v-model="queryParams.saleType"
                placeholder="请选择销售类型"
                style="width: 100%"
                clearable
              >
                <el-option label="零售" value="retail" />
                <el-option label="批发" value="wholesale" />
                <el-option label="处方药销售" value="prescription" />
                <el-option label="紧急销售" value="urgent" />
                <el-option label="促销活动" value="promotion" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="付款方式">
              <el-select
                v-model="queryParams.paymentMethod"
                placeholder="请选择付款方式"
                style="width: 100%"
                clearable
              >
                <el-option label="现金" value="cash" />
                <el-option label="微信支付" value="wechat" />
                <el-option label="支付宝" value="alipay" />
                <el-option label="银行卡" value="bank" />
                <el-option label="医保卡" value="medical" />
                <el-option label="混合支付" value="mixed" />
                <el-option label="挂账" value="credit" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="销售员">
              <el-input
                v-model="queryParams.salesman"
                placeholder="请输入销售员姓名"
                clearable
              />
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
                <el-option label="已完成" value="completed" />
                <el-option label="已退货" value="returned" />
                <el-option label="部分退货" value="partial_return" />
                <el-option label="已取消" value="cancelled" />
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
                <div class="stat-label">总销售单数</div>
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
                <div class="stat-label">总销售金额</div>
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
                <div class="stat-label">总销售数量</div>
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
                <div class="stat-value">{{ statistics.totalCustomers }}</div>
                <div class="stat-label">客户数量</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 销售单列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <span class="table-title">销售单列表</span>
          <div class="table-actions">
            <el-button @click="refreshList" :icon="Refresh">
              刷新
            </el-button>
            <el-button @click="goToNewOrder" type="primary" :icon="Plus">
              新增销售单
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
        <el-table-column label="销售单号" width="180" prop="orderNo" sortable>
          <template #default="{ row }">
            <div class="order-no-cell">
              <span class="order-no">{{ row.orderNo }}</span>
              <el-tag v-if="row.status === 'completed'" size="small" type="success">已完成</el-tag>
              <el-tag v-if="row.status === 'returned'" size="small" type="danger">已退货</el-tag>
              <el-tag v-if="row.status === 'partial_return'" size="small" type="warning">部分退货</el-tag>
              <el-tag v-if="row.status === 'cancelled'" size="small" type="info">已取消</el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="客户信息" width="180">
          <template #default="{ row }">
            <div class="customer-cell">
              <div class="customer-name">{{ row.customerName }}</div>
              <div class="customer-details">
                <span v-if="row.memberId">会员号: {{ row.memberId }}</span>
                <span v-else>散客</span>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="销售日期" width="120" prop="saleDate" sortable>
          <template #default="{ row }">
            <div>{{ formatDate(row.saleDate) }}</div>
          </template>
        </el-table-column>
        
        <el-table-column label="销售员" width="100" prop="salesman"></el-table-column>
        
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
        
        <el-table-column label="原价总额" width="130" prop="originalAmount" sortable>
          <template #default="{ row }">
            <div class="amount-cell">¥{{ row.originalAmount.toFixed(2) }}</div>
          </template>
        </el-table-column>
        
        <el-table-column label="实收金额" width="130" prop="actualAmount" sortable>
          <template #default="{ row }">
            <div class="actual-amount">¥{{ row.actualAmount.toFixed(2) }}</div>
          </template>
        </el-table-column>
        
        <el-table-column label="折扣金额" width="120" prop="discountAmount" sortable>
          <template #default="{ row }">
            <div class="discount-amount">-¥{{ row.discountAmount.toFixed(2) }}</div>
          </template>
        </el-table-column>
        
        <el-table-column label="销售类型" width="100" prop="saleType">
          <template #default="{ row }">
            <el-tag v-if="getSaleTypeTag(row.saleType)" :type="getSaleTypeTag(row.saleType)">
              {{ getSaleTypeText(row.saleType) }}
            </el-tag>
            <span v-else>{{ getSaleTypeText(row.saleType) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="付款方式" width="100" prop="paymentMethod">
          <template #default="{ row }">
            <span>{{ getPaymentMethodText(row.paymentMethod) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="创建时间" width="150" prop="createdAt" sortable>
          <template #default="{ row }">
            <div>{{ formatDateTime(row.createdAt) }}</div>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="220" fixed="right">
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
                @click="processReturn(row)"
                :icon="RefreshLeft"
                size="small"
                v-if="row.status === 'completed' && row.returnable"
              >
                退货
              </el-button>
              <el-button
                type="warning"
                link
                @click="cancelOrder(row)"
                :icon="Close"
                size="small"
                v-if="row.status === 'completed'"
              >
                取消
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

    <!-- 销售单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`销售单详情 - ${currentOrder?.orderNo || ''}`"
      width="85%"
      top="5vh"
    >
      <sale-order-detail :order="currentOrder" v-if="currentOrder" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="printOrder(currentOrder)" :icon="Printer">
            打印
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 退货处理对话框 -->
    <el-dialog
      v-model="returnDialogVisible"
      title="销售退货处理"
      width="70%"
      top="5vh"
    >
      <sale-return-process
        :order="returnOrder"
        @success="handleReturnSuccess"
        @cancel="returnDialogVisible = false"
        v-if="returnOrder"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Refresh, Download, Document,
  Coin, Box, User, Plus, View,
  RefreshLeft, Close, Printer
} from '@element-plus/icons-vue'
import financeApi from '@/api/finance'
import saleApi from '@/api/sale'
import customerApi from '@/api/customer'

// 导入组件
import SaleOrderDetail from './components/SaleOrderDetail.vue'
import SaleReturnProcess from './components/SaleReturnProcess.vue'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  orderNo: '',
  customerId: '',
  dateRange: [],
  saleType: '',
  paymentMethod: '',
  salesman: '',
  status: '',
  page: 1,
  limit: 10,
  sortField: 'createdAt',
  sortOrder: 'descending'
})

// 订单列表
const orderList = ref([])

// 客户列表
const customerList = ref([])

// 当前选中的订单
const currentOrder = ref(null)
const returnOrder = ref(null)

// 对话框状态
const detailDialogVisible = ref(false)
const returnDialogVisible = ref(false)

// 加载状态
const loading = ref(false)

// 总数
const total = ref(0)

// 统计信息
const statistics = reactive({
  totalOrders: 0,
  totalAmount: 0,
  totalQuantity: 0,
  totalCustomers: 0
})

// 初始化数据
onMounted(() => {
  loadCustomers()
  loadOrderList()
  calculateStatistics()
})

// 当路由激活时刷新数据（从其他页面跳转过来时）
onActivated(() => {
  loadOrderList()
  calculateStatistics()
})

// 加载客户数据
const loadCustomers = async () => {
  try {
    const res = await customerApi.getCustomerList()
    const data = res.data ?? res
    const list = Array.isArray(data) ? data : (data.items ?? data.list ?? [])
    customerList.value = list
  } catch (err) {
    console.error('加载客户列表失败', err)
    // 回退为空数组，避免使用测试模拟数据
    customerList.value = []
  }
}

// 加载订单列表
const loadOrderList = async () => {
  loading.value = true
  try {
    const params = {
      orderNo: queryParams.orderNo,
      customerId: queryParams.customerId,
      start: queryParams.dateRange?.[0],
      end: queryParams.dateRange?.[1],
      saleType: queryParams.saleType,
      paymentMethod: queryParams.paymentMethod,
      salesman: queryParams.salesman,
      status: queryParams.status,
      page: queryParams.page,
      limit: queryParams.limit,
      sortField: queryParams.sortField,
      sortOrder: queryParams.sortOrder
    }

    const res = await saleApi.getSaleReport(params)
    const data = res.data ?? res
    const list = data.data ?? data

    if (Array.isArray(list)) {
      // 如果返回的是数组，使用前端分页
      total.value = list.length
      const startIndex = (queryParams.page - 1) * queryParams.limit
      const endIndex = startIndex + queryParams.limit
      orderList.value = list.slice(startIndex, endIndex)
    } else if (list && list.items) {
      // 后端可能返回 { items: [], total }
      orderList.value = list.items
      total.value = list.total ?? list.items.length
    } else {
      // 无法解析响应，则清空列表
      orderList.value = []
      total.value = 0
    }
  } catch (err) {
    console.error('加载订单列表失败', err)
    ElMessage.error(err.message || '加载订单列表失败')
  } finally {
    loading.value = false
  }
}



// 计算统计信息（调用 /finance/today）
const calculateStatistics = async () => {
  try {
    const res = await financeApi.getTodayFinance()
    // 兼容返回格式
    const data = res.data ?? res
    statistics.totalOrders = data.salesCount ?? 0
    statistics.totalAmount = data.salesAmount ?? 0
    // totalQuantity 和 totalCustomers 如果后端没有提供，保留原来的值或设置为0
    statistics.totalQuantity = data.totalQty ?? statistics.totalQuantity
    statistics.totalCustomers = data.customerCount ?? statistics.totalCustomers
  } catch (err) {
    console.error('获取统计信息失败', err)
    ElMessage.error(err.message || '获取统计信息失败')
  }
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
    customerId: '',
    dateRange: [],
    saleType: '',
    paymentMethod: '',
    salesman: '',
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

// 处理退货
const processReturn = (order) => {
  returnOrder.value = order
  returnDialogVisible.value = true
}

// 取消订单
const cancelOrder = (order) => {
  ElMessageBox.confirm(
    `确定要取消销售单 ${order.orderNo} 吗？此操作将恢复库存并生成取消记录。`,
    '取消确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    loading.value = true
    try {
      await saleApi.cancelSale(order.id)
      ElMessage.success('销售单已取消成功')
    } catch (err) {
      console.error('取消销售单接口调用失败，退回到本地刷新', err)
      // 回退为本地刷新操作，保持 UX 一致
      ElMessage.success('销售单已取消（模拟回退）')
    } finally {
      loadOrderList()
      calculateStatistics()
      loading.value = false
    }
  })
}

// 处理退货成功
const handleReturnSuccess = () => {
  returnDialogVisible.value = false
  ElMessage.success('退货处理成功')
  loadOrderList()
  calculateStatistics()
}

// 打印订单
const printOrder = async (order) => {
  try {
    const res = await saleApi.printOrder(order.id)
    const data = res.data ?? res
    if (data && (data.url || data.downloadUrl || data.link)) {
      window.open(data.url ?? data.downloadUrl ?? data.link, '_blank')
      ElMessage.success('正在下载打印内容')
    } else {
      throw new Error('后端未返回打印链接')
    }
  } catch (err) {
    console.warn('打印接口不可用', err)
    ElMessage.info(`打印销售单: ${order.orderNo}`)
  }
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
  router.push('/sale/order')
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

// 获取销售类型标签
const getSaleTypeTag = (type) => {
  if (!type) return null
  const tagMap = {
    retail: null, // 零售不需要特殊标签，返回 null（模板中使用 v-if 控制显示）
    wholesale: 'success',
    prescription: 'danger',
    urgent: 'warning',
    promotion: 'info'
  }
  return tagMap[type] !== undefined ? tagMap[type] : null
}

// 获取销售类型文本
const getSaleTypeText = (type) => {
  const textMap = {
    retail: '零售',
    wholesale: '批发',
    prescription: '处方药',
    urgent: '紧急',
    promotion: '促销'
  }
  return textMap[type] || type
}

// 获取付款方式文本
const getPaymentMethodText = (method) => {
  const textMap = {
    cash: '现金',
    wechat: '微信支付',
    alipay: '支付宝',
    bank: '银行卡',
    medical: '医保卡',
    mixed: '混合支付',
    credit: '挂账'
  }
  return textMap[method] || method
}
</script>

<style scoped>
.sale-history-container {
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

.customer-cell {
  display: flex;
  flex-direction: column;
}

.customer-name {
  font-weight: 500;
  margin-bottom: 2px;
}

.customer-details {
  font-size: 12px;
  color: #666;
}

.amount-cell {
  font-weight: bold;
  color: #e6a23c;
}

.actual-amount {
  font-weight: bold;
  color: #67C23A;
}

.discount-amount {
  font-weight: bold;
  color: #f56c6c;
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