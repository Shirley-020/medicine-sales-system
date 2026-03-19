<!-- src/views/sale/SaleReturn.vue -->
<template>
  <div class="sale-return-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>销售退货管理</h2>
      <p class="subtitle">处理销售退货业务，系统将自动恢复库存并生成退货记录</p>
    </div>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="saveReturnOrder" :icon="Check" :loading="saving">
        保存退货单
      </el-button>
      <el-button @click="resetForm" :icon="Refresh">
        重置表单
      </el-button>
      <el-button @click="showReturnHistory" :icon="List">
        退货历史
      </el-button>
    </div>

    <!-- 主要表单区域 -->
    <el-card class="form-card">
      <!-- 退货基本信息 -->
      <div class="form-section">
        <h3 class="section-title">📋 退货单基本信息</h3>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="退货单号" required>
              <el-input
                v-model="formData.returnNo"
                placeholder="系统自动生成"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="原销售单号" required>
              <el-input
                v-model="formData.originalOrderNo"
                placeholder="请输入原销售单号"
                clearable
                @keyup.enter="searchOriginalOrder"
                style="width: 100%"
              >
                <template #append>
                  <el-button :icon="Search" @click="searchOriginalOrder" />
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="退货日期" required>
              <el-date-picker
                v-model="formData.returnDate"
                type="date"
                placeholder="选择退货日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="处理人员" required>
              <el-select
                v-model="formData.handler"
                placeholder="请选择处理人员"
                style="width: 100%"
                filterable
                clearable
              >
                <el-option
                  v-for="user in userList"
                  :key="user"
                  :label="user"
                  :value="user"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="退货类型" required>
              <el-select
                v-model="formData.returnType"
                placeholder="请选择退货类型"
                style="width: 100%"
              >
                <el-option label="质量问题退货" value="quality" />
                <el-option label="客户原因退货" value="customer" />
                <el-option label="过期药品退货" value="expired" />
                <el-option label="其他原因" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="退款方式" required>
              <el-select
                v-model="formData.refundMethod"
                placeholder="请选择退款方式"
                style="width: 100%"
              >
                <el-option label="原路退回" value="original" />
                <el-option label="现金退款" value="cash" />
                <el-option label="转账退款" value="transfer" />
                <el-option label="账户余额" value="balance" />
                <el-option label="换货处理" value="exchange" />
                <el-option label="挂账处理" value="credit" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="退货原因" required>
              <el-input
                v-model="formData.reason"
                type="textarea"
                :rows="3"
                placeholder="请输入详细的退货原因"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 原销售单信息 -->
      <div class="form-section" v-if="originalOrder">
        <h3 class="section-title">📄 原销售单信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="销售单号">{{ originalOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="销售日期">{{ formatDate(originalOrder.saleDate) }}</el-descriptions-item>
          <el-descriptions-item label="客户">{{ originalOrder.customerName }}</el-descriptions-item>
          <el-descriptions-item label="销售员">{{ originalOrder.salesman }}</el-descriptions-item>
          <el-descriptions-item label="销售类型">{{ getSaleTypeText(originalOrder.saleType) }}</el-descriptions-item>
          <el-descriptions-item label="付款方式">{{ getPaymentMethodText(originalOrder.paymentMethod) }}</el-descriptions-item>
          <el-descriptions-item label="原价总额">¥{{ originalOrder.originalAmount?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="实收金额">¥{{ originalOrder.actualAmount?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(originalOrder.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="退货状态">
            <el-tag :type="getReturnStatusTag(originalOrder.returnStatus)">
              {{ getReturnStatusText(originalOrder.returnStatus) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 退货商品明细 -->
      <div class="form-section">
        <div class="section-header">
          <h3 class="section-title">🔄 退货商品明细</h3>
          <div class="section-actions">
            <el-button type="success" @click="selectAllItems" :icon="Select">
              全选
            </el-button>
            <el-button @click="clearSelection" :icon="Close">
              清空选择
            </el-button>
            <el-button @click="showItemSelector" :icon="Plus" v-if="originalOrder">
              选择商品
            </el-button>
          </div>
        </div>

        <!-- 商品选择提示 -->
        <div v-if="!originalOrder" class="select-hint">
          <el-empty description="请先输入原销售单号并加载销售单信息" />
        </div>

        <!-- 商品明细表格 -->
        <div v-else class="return-items-container">
          <el-table
            :data="returnItems"
            style="width: 100%"
            border
            stripe
            class="return-items-table"
          >
            <el-table-column type="selection" width="55" @selection-change="handleSelectionChange" />
            
            <el-table-column label="药品名称" prop="drugName" />
            
            <el-table-column label="规格" prop="specification" width="120" />
            
            <el-table-column label="生产厂家" prop="manufacturer" width="150" />
            
            <el-table-column label="批次号" prop="batchNo" width="120" />
            
            <el-table-column label="原销售数量" width="100">
              <template #default="{ row }">
                {{ row.originalQuantity }}
              </template>
            </el-table-column>
            
            <el-table-column label="可退数量" width="100">
              <template #default="{ row }">
                {{ row.returnableQuantity }}
              </template>
            </el-table-column>
            
            <el-table-column label="退货数量" width="120">
              <template #default="{ row, $index }">
                <el-input-number
                  v-model="row.returnQuantity"
                  :min="0"
                  :max="row.returnableQuantity"
                  :step="1"
                  size="small"
                  @change="updateReturnAmount($index)"
                  :disabled="!row.selected"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="销售单价(元)" width="120">
              <template #default="{ row }">
                ¥{{ row.salePrice?.toFixed(2) }}
              </template>
            </el-table-column>
            
            <el-table-column label="折扣%" width="80">
              <template #default="{ row }">
                {{ row.discount || 0 }}%
              </template>
            </el-table-column>
            
            <el-table-column label="退货单价(元)" width="120">
              <template #default="{ row }">
                ¥{{ (row.salePrice * (1 - (row.discount || 0) / 100)).toFixed(2) }}
              </template>
            </el-table-column>
            
            <el-table-column label="退货金额(元)" width="120">
              <template #default="{ row }">
                <div class="return-amount-cell">
                  ¥{{ row.returnAmount?.toFixed(2) || '0.00' }}
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="退货原因" width="150">
              <template #default="{ row, $index }">
                <el-select
                  v-model="row.returnReason"
                  placeholder="选择原因"
                  size="small"
                  style="width: 100%"
                  :disabled="!row.selected"
                >
                  <el-option label="质量问题" value="quality" />
                  <el-option label="过期药品" value="expired" />
                  <el-option label="买错药品" value="wrong" />
                  <el-option label="客户不需要" value="not_needed" />
                  <el-option label="其他原因" value="other" />
                </el-select>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 汇总信息 -->
        <div class="summary-section" v-if="returnItems.length > 0">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="6" :lg="6">
              <div class="summary-item">
                <span class="label">退货品种：</span>
                <span class="value">{{ selectedItemCount }}</span>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" :lg="6">
              <div class="summary-item">
                <span class="label">退货数量：</span>
                <span class="value">{{ totalReturnQuantity }}</span>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" :lg="6">
              <div class="summary-item">
                <span class="label">退货金额：</span>
                <span class="value return-amount">¥{{ totalReturnAmount.toFixed(2) }}</span>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" :lg="6">
              <div class="summary-item">
                <span class="label">退货比例：</span>
                <span class="value return-ratio">{{ returnRatio }}%</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 退款信息 -->
      <div class="form-section" v-if="totalReturnAmount > 0">
        <h3 class="section-title">💰 退款信息</h3>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="应退金额">
              <div class="refund-amount">
                ¥{{ totalReturnAmount.toFixed(2) }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="实际退款金额">
              <el-input-number
                v-model="formData.actualRefundAmount"
                :min="0"
                :max="totalReturnAmount"
                :precision="2"
                style="width: 100%"
                @change="calculateRefund"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="退款手续费">
              <el-input-number
                v-model="formData.refundFee"
                :min="0"
                :max="totalReturnAmount"
                :precision="2"
                style="width: 100%"
                @change="calculateRefund"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="客户实收">
              <div class="actual-refund-amount">
                ¥{{ actualRefundAmount.toFixed(2) }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="formData.refundMethod === 'cash'">
            <el-alert
              title="现金退款提示"
              type="warning"
              description="现金退款需在退款后让客户签字确认"
              :closable="false"
            />
          </el-col>
          <el-col :span="24" v-if="formData.refundMethod === 'exchange'">
            <el-alert
              title="换货处理提示"
              type="info"
              description="换货处理需在系统中创建新的销售单"
              :closable="false"
            />
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 商品选择对话框 -->
    <el-dialog
      v-model="itemSelectorVisible"
      title="选择退货商品"
      width="80%"
      top="5vh"
    >
      <return-item-selector
        :items="originalOrderItems"
        @confirm="handleItemsConfirm"
        @cancel="itemSelectorVisible = false"
      />
    </el-dialog>

    <!-- 保存确认对话框 -->
    <el-dialog
      v-model="saveDialogVisible"
      title="保存退货单"
      width="500px"
      center
    >
      <div class="save-dialog">
        <el-icon color="#67C23A" size="48"><SuccessFilled /></el-icon>
        <h3>确认保存退货单吗？</h3>
        <div class="save-summary">
          <p>退货单号：{{ formData.returnNo }}</p>
          <p>原销售单：{{ formData.originalOrderNo }}</p>
          <p>退货品种：{{ selectedItemCount }} 种</p>
          <p>退货金额：¥{{ totalReturnAmount.toFixed(2) }}</p>
          <p>退款方式：{{ getRefundMethodText(formData.refundMethod) }}</p>
        </div>
        <div class="inventory-notice" v-if="selectedItemCount > 0">
          <el-alert
            title="库存提醒"
            type="info"
            description="保存后系统将自动恢复对应药品的库存"
            :closable="false"
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="saveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSave" :loading="saving">
            确认保存
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
  Check, Refresh, List, Search,
  Select, Close, Plus, SuccessFilled
} from '@element-plus/icons-vue'

// 导入商品选择组件
import ReturnItemSelector from './components/ReturnItemSelector.vue'
import saleApi from '@/api/sale'
import authApi from '@/api/auth'

const router = useRouter()

// 表单数据
const formData = reactive({
  returnNo: '',
  originalOrderNo: '',
  returnDate: '',
  handler: '',
  returnType: 'customer',
  refundMethod: 'original',
  reason: '',
  actualRefundAmount: 0,
  refundFee: 0
})

// 原销售单信息
const originalOrder = ref(null)

// 原销售单商品列表
const originalOrderItems = ref([])

// 退货商品列表
const returnItems = ref([])

// 用户列表（处理人员）
const userList = ref([])

// 选中的商品
const selectedItems = ref([])

// 对话框状态
const itemSelectorVisible = ref(false)
const saveDialogVisible = ref(false)

// 保存状态
const saving = ref(false)

// 加载原销售单状态
const loadingOrder = ref(false)

// 计算选中的商品数量
const selectedItemCount = computed(() => {
  return selectedItems.value.length
})

// 计算退货总数量
const totalReturnQuantity = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    return sum + (item.returnQuantity || 0)
  }, 0)
})

// 计算退货总金额
const totalReturnAmount = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    return sum + (item.returnAmount || 0)
  }, 0)
})

// 计算退货比例
const returnRatio = computed(() => {
  if (!originalOrder.value || originalOrder.value.actualAmount <= 0) return 0
  return ((totalReturnAmount.value / originalOrder.value.actualAmount) * 100).toFixed(1)
})

// 计算实际退款金额
const actualRefundAmount = computed(() => {
  return formData.actualRefundAmount - formData.refundFee
})

// 初始化数据
onMounted(() => {
  generateReturnNo()
  loadUsers()
  
  // 设置默认值
  const today = new Date()
  formData.returnDate = today.toISOString().split('T')[0]
})

// 生成退货单号
const generateReturnNo = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = String(Math.floor(Math.random() * 10000)).padStart(4, '0')
  formData.returnNo = `RT${year}${month}${day}${random}`
}

// 加载用户列表（处理人员）
const loadUsers = async () => {
  try {
    const res = await authApi.getUsers()
    const data = res.data ?? res
    userList.value = Array.isArray(data) ? data : []
    
    // 如果有用户列表，设置默认处理人员为第一个用户
    if (userList.value.length > 0 && !formData.handler) {
      formData.handler = userList.value[0]
    }
  } catch (err) {
    console.error('加载用户列表失败', err)
    ElMessage.error(err.message || '加载用户列表失败')
    userList.value = []
  }
}

// 搜索原销售单
const searchOriginalOrder = async () => {
  if (!formData.originalOrderNo.trim()) {
    ElMessage.warning('请输入原销售单号')
    return
  }

  loadingOrder.value = true
  try {
    // 先通过订单号查询销售单列表
    const res = await saleApi.getSaleReport({ orderNo: formData.originalOrderNo })
    const data = res.data ?? res
    const list = data.data ?? data
    const order = Array.isArray(list) ? list[0] : list

    if (order && order.id) {
      // 获取销售单详情（包含销售项列表）
      const detailRes = await saleApi.getSaleDetail(order.id)
      const detailData = detailRes.data ?? detailRes
      const detail = detailData.data ?? detailData
      
      if (detail) {
        originalOrder.value = detail
        await loadOriginalOrderItems(detail)
        ElMessage.success(`已加载销售单: ${detail.orderNo || formData.originalOrderNo}`)
      } else {
        originalOrder.value = null
        originalOrderItems.value = []
        returnItems.value = []
        ElMessage.error('未找到对应的销售单详情，请检查单号是否正确')
      }
    } else {
      originalOrder.value = null
      originalOrderItems.value = []
      returnItems.value = []
      ElMessage.error('未找到对应的销售单，请检查单号是否正确')
    }
  } catch (err) {
    console.error('搜索销售单失败', err)
    ElMessage.error(err.message || '搜索销售单失败')
  } finally {
    loadingOrder.value = false
  }
}

// 生成模拟销售单数据（已移除，改为调用后端接口获取）

// 加载原销售单商品
const loadOriginalOrderItems = async (order) => {
  originalOrderItems.value = []
  returnItems.value = []

  // 后端返回的详情中包含 items 字段
  const items = order.items ?? []

  if (Array.isArray(items) && items.length > 0) {
    items.forEach((it) => {
      const originalQuantity = it.quantity ?? it.qty ?? 1
      const returnedQty = it.returnedQuantity ?? it.returned ?? 0
      const returnableQuantity = Math.max(0, originalQuantity - returnedQty)

      const item = {
        id: it.id, // 使用后端返回的真实销售项ID
        drugName: it.drugName ?? it.name ?? '未知药品',
        specification: it.specification ?? it.spec ?? '',
        manufacturer: it.manufacturer ?? '',
        batchNo: it.batchNo ?? it.batch ?? '',
        originalQuantity,
        returnableQuantity,
        returnQuantity: returnableQuantity,
        salePrice: it.salePrice ?? it.price ?? 0,
        discount: it.discount ?? 0,
        returnAmount: 0,
        returnReason: 'quality',
        selected: returnableQuantity > 0
      }

      updateReturnAmountForItem(item)
      originalOrderItems.value.push({ ...item })
      if (item.returnableQuantity > 0) returnItems.value.push({ ...item })
    })
  } else {
    ElMessage.warning('该销售单没有商品明细')
  }

  // 更新选中的商品
  updateSelectedItems()
}

// 更新退货金额
const updateReturnAmount = (index) => {
  const item = returnItems.value[index]
  if (!item) return
  
  updateReturnAmountForItem(item)
}

// 更新单个商品的退货金额
const updateReturnAmountForItem = (item) => {
  const quantity = item.returnQuantity || 0
  const unitPrice = item.salePrice * (1 - (item.discount || 0) / 100)
  item.returnAmount = quantity * unitPrice
}

// 处理商品选择变化
const handleSelectionChange = (selection) => {
  selectedItems.value = selection
  
  // 更新退货商品列表中的选中状态
  returnItems.value.forEach(item => {
    item.selected = selection.some(selected => selected.id === item.id)
    
    // 如果取消选中，设置退货数量为0
    if (!item.selected && item.returnQuantity > 0) {
      item.returnQuantity = 0
      updateReturnAmountForItem(item)
    }
  })
}

// 全选商品
const selectAllItems = () => {
  // 选择所有可退货的商品
  const selectableItems = returnItems.value.filter(item => item.returnableQuantity > 0)
  selectableItems.forEach(item => {
    item.selected = true
    item.returnQuantity = item.returnableQuantity
    updateReturnAmountForItem(item)
  })
  
  // 更新选中列表
  selectedItems.value = [...selectableItems]
}

// 清空选择
const clearSelection = () => {
  returnItems.value.forEach(item => {
    item.selected = false
    item.returnQuantity = 0
    updateReturnAmountForItem(item)
  })
  
  selectedItems.value = []
}

// 显示商品选择器
const showItemSelector = () => {
  itemSelectorVisible.value = true
}

// 处理商品选择确认
const handleItemsConfirm = (selectedIds) => {
  itemSelectorVisible.value = false
  
  // 更新退货商品列表的选中状态
  returnItems.value.forEach(item => {
    item.selected = selectedIds.includes(item.id)
    if (item.selected && item.returnQuantity === 0) {
      item.returnQuantity = item.returnableQuantity
      updateReturnAmountForItem(item)
    } else if (!item.selected) {
      item.returnQuantity = 0
      updateReturnAmountForItem(item)
    }
  })
  
  // 更新选中列表
  selectedItems.value = returnItems.value.filter(item => item.selected)
  
  ElMessage.success(`已选择 ${selectedItems.value.length} 个商品`)
}

// 计算退款
const calculateRefund = () => {
  // 确保实际退款金额不超过退货金额
  if (formData.actualRefundAmount > totalReturnAmount.value) {
    formData.actualRefundAmount = totalReturnAmount.value
  }
  
  // 确保手续费不超过实际退款金额
  if (formData.refundFee > formData.actualRefundAmount) {
    formData.refundFee = formData.actualRefundAmount
  }
}

// 保存退货单
const saveReturnOrder = () => {
  // 验证表单
  if (!validateForm()) {
    return
  }
  
  saveDialogVisible.value = true
}

// 确认保存
const confirmSave = async () => {
  saving.value = true
  try {
    const payload = {
      saleId: originalOrder.value?.id,
      items: selectedItems.value.map(i => ({ saleItemId: i.id, quantity: i.returnQuantity, reason: i.returnReason })),
      refundMethod: formData.refundMethod,
      actualRefundAmount: formData.actualRefundAmount,
      refundFee: formData.refundFee,
      returnType: formData.returnType,
      reason: formData.reason
    }

    const res = await saleApi.returnSale(payload)
    const data = res.data ?? res

    ElMessage.success(data.message || '退货成功')
    saveDialogVisible.value = false

    // 重置表单数据
    resetFormData()
    
    // 询问用户是否查看销售历史
    ElMessageBox.confirm(
      '退货单已保存成功！是否查看销售历史？',
      '保存成功',
      {
        confirmButtonText: '查看历史',
        cancelButtonText: '继续退货',
        type: 'success'
      }
    ).then(() => {
      // 跳转到销售历史页面
      router.push('/sale/history')
    }).catch(() => {
      // 用户选择继续，留在当前页面
    })
  } catch (err) {
    console.error('退货失败', err)
    ElMessage.error(err.message || '退货失败')
  } finally {
    saving.value = false
  }
}

// 验证表单
const validateForm = () => {
  // 验证原销售单号
  if (!formData.originalOrderNo.trim()) {
    ElMessage.error('请输入原销售单号')
    return false
  }
  
  // 验证是否已加载销售单
  if (!originalOrder.value) {
    ElMessage.error('请先加载原销售单信息')
    return false
  }
  
  // 验证退货日期
  if (!formData.returnDate) {
    ElMessage.error('请选择退货日期')
    return false
  }
  
  // 验证处理人员
  if (!formData.handler || !formData.handler.trim()) {
    ElMessage.error('请选择处理人员')
    return false
  }
  
  // 验证退货原因
  if (!formData.reason.trim()) {
    ElMessage.error('请输入退货原因')
    return false
  }
  
  // 验证退货商品
  if (selectedItemCount.value === 0) {
    ElMessage.error('请至少选择一个退货商品')
    return false
  }
  
  // 验证退货数量
  for (const item of selectedItems.value) {
    if (!item.returnQuantity || item.returnQuantity <= 0) {
      ElMessage.error(`请为 ${item.drugName} 输入有效的退货数量`)
      return false
    }
    
    if (item.returnQuantity > item.returnableQuantity) {
      ElMessage.error(`${item.drugName} 的退货数量不能超过可退数量`)
      return false
    }
  }
  
  return true
}

// 重置表单数据（内部函数，不显示确认对话框）
const resetFormData = () => {
  // 重置表单数据
  Object.assign(formData, {
    returnNo: '',
    originalOrderNo: '',
    returnDate: new Date().toISOString().split('T')[0],
    handler: '管理员',
    returnType: 'customer',
    refundMethod: 'original',
    reason: '',
    actualRefundAmount: 0,
    refundFee: 0
  })
  
  // 重新生成单号
  generateReturnNo()
  
  // 清空相关数据
  originalOrder.value = null
  originalOrderItems.value = []
  returnItems.value = []
  selectedItems.value = []
}

// 重置表单（带确认对话框）
const resetForm = () => {
  ElMessageBox.confirm(
    '确定要重置表单吗？所有已填写的数据将丢失。',
    '确认重置',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    resetFormData()
    ElMessage.success('表单已重置')
  })
}

// 查看退货历史（跳转到销售历史页面，可以筛选退货记录）
const showReturnHistory = () => {
  router.push('/sale/history')
}

// 更新选中的商品
const updateSelectedItems = () => {
  selectedItems.value = returnItems.value.filter(item => item.selected)
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

// 获取退款方式文本
const getRefundMethodText = (method) => {
  const textMap = {
    original: '原路退回',
    cash: '现金退款',
    transfer: '转账退款',
    balance: '账户余额',
    exchange: '换货处理',
    credit: '挂账处理'
  }
  return textMap[method] || method
}

// 获取退货状态标签
const getReturnStatusTag = (status) => {
  if (!status) return 'info'
  const tagMap = {
    none: 'success',
    partial: 'warning',
    full: 'danger'
  }
  return tagMap[status] || 'info'
}

// 获取退货状态文本
const getReturnStatusText = (status) => {
  const textMap = {
    none: '未退货',
    partial: '部分退货',
    full: '全部退货'
  }
  return textMap[status] || status
}
</script>

<style scoped>
.sale-return-container {
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

.action-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.form-card {
  margin-bottom: 20px;
}

.form-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.form-section:last-child {
  border-bottom: none;
}

.section-title {
  margin: 0 0 20px;
  font-size: 18px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-actions {
  display: flex;
  gap: 10px;
}

.select-hint {
  padding: 40px 20px;
  text-align: center;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.return-items-container {
  margin: 20px 0;
}

.return-items-table {
  min-width: 1600px;
}

.return-amount-cell {
  font-weight: bold;
  color: #f56c6c;
}

.summary-section {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.summary-item .label {
  color: #666;
  font-size: 14px;
}

.summary-item .value {
  font-weight: bold;
  font-size: 18px;
  color: #333;
}

.return-amount {
  color: #f56c6c;
  font-size: 20px;
}

.return-ratio {
  color: #409EFF;
}

.refund-amount {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  text-align: center;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.actual-refund-amount {
  font-size: 20px;
  font-weight: bold;
  color: #67C23A;
  text-align: center;
  padding: 8px;
  background-color: #f0f9eb;
  border-radius: 4px;
}

.save-dialog {
  text-align: center;
  padding: 20px 0;
}

.save-dialog h3 {
  margin: 15px 0;
  color: #333;
}

.save-summary {
  text-align: left;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  margin-top: 15px;
}

.save-summary p {
  margin: 8px 0;
  color: #666;
}

.inventory-notice {
  margin-top: 15px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .action-bar {
    flex-direction: column;
  }
  
  .action-bar .el-button {
    width: 100%;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .section-actions {
    width: 100%;
    flex-direction: column;
  }
  
  .section-actions .el-button {
    width: 100%;
  }
  
  .summary-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}
</style>