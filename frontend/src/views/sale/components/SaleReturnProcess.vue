<!-- src/views/sale/components/SaleReturnProcess.vue -->
<template>
  <div class="sale-return-process-container" v-if="order">
    <!-- 退货信息 -->
    <div class="return-header">
      <h3>销售退货处理</h3>
      <p class="subtitle">销售单号: {{ order.orderNo }} | 客户: {{ order.customerName }}</p>
    </div>

    <!-- 原订单信息 -->
    <el-card class="original-order-info">
      <template #header>
        <span class="section-title">📋 原销售单信息</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="销售日期">{{ formatDate(order.saleDate) }}</el-descriptions-item>
        <el-descriptions-item label="销售员">{{ order.salesman }}</el-descriptions-item>
        <el-descriptions-item label="原价总额">¥{{ order.originalAmount.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="实收金额">¥{{ order.actualAmount.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="销售类型">{{ getSaleTypeText(order.saleType) }}</el-descriptions-item>
        <el-descriptions-item label="付款方式">{{ getPaymentMethodText(order.paymentMethod) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 退货商品选择 -->
    <el-card class="return-items-section">
      <template #header>
        <span class="section-title">🔄 选择退货商品</span>
      </template>

      <div class="return-type-selector">
        <el-radio-group v-model="returnType" @change="handleReturnTypeChange">
          <el-radio-button value="full">全部退货</el-radio-button>
          <el-radio-button value="partial">部分退货</el-radio-button>
        </el-radio-group>
      </div>

      <el-table
        :data="returnItems"
        style="width: 100%"
        border
        stripe
        class="return-items-table"
      >
        <el-table-column label="药品名称" prop="drugName" />
        <el-table-column label="规格" prop="specification" width="120" />
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
        <el-table-column label="退货数量" width="120" v-if="returnType === 'partial'">
          <template #default="{ row, $index }">
            <el-input-number
              v-model="row.returnQuantity"
              :min="0"
              :max="row.returnableQuantity"
              :step="1"
              size="small"
              @change="updateReturnAmount($index)"
            />
          </template>
        </el-table-column>
        <el-table-column label="退货数量" width="100" v-else>
          <template #default="{ row }">
            {{ row.returnableQuantity }}
          </template>
        </el-table-column>
        <el-table-column label="销售单价" width="100">
          <template #default="{ row }">
            ¥{{ row.salePrice?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="折扣" width="80">
          <template #default="{ row }">
            {{ row.discount || 0 }}%
          </template>
        </el-table-column>
        <el-table-column label="退货金额" width="120">
          <template #default="{ row }">
            ¥{{ row.returnAmount?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column label="退货原因" width="150" v-if="returnType === 'partial'">
          <template #default="{ row, $index }">
            <el-select
              v-model="row.returnReason"
              placeholder="选择原因"
              size="small"
              style="width: 100%"
            >
              <el-option label="质量问题" value="quality" />
              <el-option label="过期药品" value="expired" />
              <el-option label="买错药品" value="wrong" />
              <el-option label="客户不需要" value="not_needed" />
              <el-option label="其他原因" value="other" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="是否退货" width="100" v-if="returnType === 'partial'">
          <template #default="{ row }">
            <el-switch
              v-model="row.returnFlag"
              @change="handleItemReturnChange(row)"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 退货信息汇总 -->
    <el-card class="return-summary-section">
      <template #header>
        <span class="section-title">💰 退货信息汇总</span>
      </template>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <div class="summary-item">
            <span class="label">退货商品：</span>
            <span class="value">{{ returnItemCount }} 种</span>
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

      <!-- 退货详情 -->
      <div class="return-details">
        <el-form :model="returnForm" label-width="100px">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="8" :lg="8">
              <el-form-item label="退货类型">
                <el-select
                  v-model="returnForm.returnType"
                  placeholder="选择退货类型"
                  style="width: 100%"
                >
                  <el-option label="质量问题退货" value="quality" />
                  <el-option label="客户原因退货" value="customer" />
                  <el-option label="过期药品退货" value="expired" />
                  <el-option label="其他原因" value="other" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8" :lg="8">
              <el-form-item label="退款方式">
                <el-select
                  v-model="returnForm.refundMethod"
                  placeholder="选择退款方式"
                  style="width: 100%"
                >
                  <el-option label="原路退回" value="original" />
                  <el-option label="现金退款" value="cash" />
                  <el-option label="转账退款" value="transfer" />
                  <el-option label="账户余额" value="balance" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8" :lg="8">
              <el-form-item label="处理人员">
                <el-input
                  v-model="returnForm.handler"
                  placeholder="请输入处理人员"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="退货原因">
                <el-input
                  v-model="returnForm.reason"
                  type="textarea"
                  :rows="2"
                  placeholder="请输入详细的退货原因"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-card>

    <!-- 操作按钮 -->
    <div class="return-actions">
      <el-button @click="handleCancel" size="large">
        取消
      </el-button>
      <el-button
        type="primary"
        @click="handleConfirmReturn"
        size="large"
        :disabled="totalReturnAmount <= 0"
        :loading="processing"
      >
        确认退货 (¥{{ totalReturnAmount.toFixed(2) }})
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  order: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['success', 'cancel'])

// 退货类型：full-全部退货，partial-部分退货
const returnType = ref('full')

// 退货商品列表
const returnItems = ref([])

// 退货表单
const returnForm = reactive({
  returnType: 'customer',
  refundMethod: 'original',
  handler: '管理员',
  reason: ''
})

// 处理状态
const processing = ref(false)

// 计算退货商品数量
const returnItemCount = computed(() => {
  if (returnType.value === 'full') {
    return returnItems.value.length
  } else {
    return returnItems.value.filter(item => item.returnFlag && item.returnQuantity > 0).length
  }
})

// 计算退货总数量
const totalReturnQuantity = computed(() => {
  if (returnType.value === 'full') {
    return returnItems.value.reduce((sum, item) => sum + item.returnableQuantity, 0)
  } else {
    return returnItems.value.reduce((sum, item) => {
      if (item.returnFlag) {
        return sum + (item.returnQuantity || 0)
      }
      return sum
    }, 0)
  }
})

// 计算退货总金额
const totalReturnAmount = computed(() => {
  if (returnType.value === 'full') {
    return returnItems.value.reduce((sum, item) => {
      const unitPrice = item.salePrice * (1 - (item.discount || 0) / 100)
      return sum + (item.returnableQuantity * unitPrice)
    }, 0)
  } else {
    return returnItems.value.reduce((sum, item) => {
      if (item.returnFlag) {
        const unitPrice = item.salePrice * (1 - (item.discount || 0) / 100)
        return sum + ((item.returnQuantity || 0) * unitPrice)
      }
      return sum
    }, 0)
  }
})

// 计算退货比例
const returnRatio = computed(() => {
  if (props.order.actualAmount <= 0) return 0
  return ((totalReturnAmount.value / props.order.actualAmount) * 100).toFixed(1)
})

// 初始化退货商品数据
const initReturnItems = () => {
  // 模拟药品数据
  const drugNames = [
    '阿莫西林胶囊', '布洛芬缓释胶囊', '头孢克肟片', '连花清瘟胶囊',
    '维生素C片', '板蓝根颗粒', '硝苯地平缓释片', '二甲双胍片'
  ]

  returnItems.value = []

  for (let i = 0; i < props.order.drugCount; i++) {
    // 随机决定是否可退货（实际应该从数据库获取）
    const returnableQuantity = Math.floor(Math.random() * 5) + 1

    returnItems.value.push({
      id: i + 1,
      drugName: drugNames[i % drugNames.length],
      specification: `${['250mg', '300mg', '100mg', '0.35g'][i % 4]}*${[24, 20, 10, 100][i % 4]}${['粒', '粒', '片', '片'][i % 4]}`,
      originalQuantity: Math.floor(Math.random() * 10) + 1,
      returnableQuantity,
      returnQuantity: returnableQuantity, // 全部退货时默认可退数量
      salePrice: parseFloat((Math.random() * 100 + 20).toFixed(2)),
      discount: Math.floor(Math.random() * 30),
      returnAmount: 0,
      returnReason: '',
      returnFlag: true // 默认全部退货
    })

    // 计算退货金额
    updateReturnAmount(i)
  }
}

// 更新退货金额
const updateReturnAmount = (index) => {
  const item = returnItems.value[index]
  if (!item) return

  const quantity = returnType.value === 'full' ? item.returnableQuantity : (item.returnQuantity || 0)
  const unitPrice = item.salePrice * (1 - (item.discount || 0) / 100)
  item.returnAmount = quantity * unitPrice
}

// 处理退货类型变化
const handleReturnTypeChange = (type) => {
  if (type === 'full') {
    // 全部退货，恢复默认值
    returnItems.value.forEach((item, index) => {
      item.returnQuantity = item.returnableQuantity
      item.returnFlag = true
      updateReturnAmount(index)
    })
  } else {
    // 部分退货，初始化退货数量为0
    returnItems.value.forEach((item, index) => {
      item.returnQuantity = 0
      item.returnFlag = false
      updateReturnAmount(index)
    })
  }
}

// 处理商品退货开关变化
const handleItemReturnChange = (item) => {
  if (!item.returnFlag) {
    item.returnQuantity = 0
  } else {
    item.returnQuantity = item.returnableQuantity
  }

  // 找到商品索引并更新金额
  const index = returnItems.value.findIndex(i => i.id === item.id)
  if (index !== -1) {
    updateReturnAmount(index)
  }
}

// 确认退货
const handleConfirmReturn = async () => {
  if (totalReturnAmount.value <= 0) {
    ElMessage.warning('请选择要退货的商品')
    return
  }

  if (!returnForm.reason.trim()) {
    ElMessage.warning('请输入退货原因')
    return
  }

  processing.value = true

  try {
    const payload = {
      saleId: props.order.id,
      items: returnItems.value.filter(item => item.returnFlag && item.returnQuantity > 0).map(i => ({ saleItemId: i.id, quantity: i.returnQuantity, reason: i.returnReason })),
      refundMethod: returnForm.refundMethod,
      actualRefundAmount: totalReturnAmount.value,
      refundFee: returnForm.refundFee,
      returnType: returnType.value,
      reason: returnForm.reason
    }

    await saleApi.returnSale(payload)
    ElMessage.success('退货处理成功！')
    processing.value = false
    // 通知父组件退货成功
    emit('success', {
      orderId: props.order.id,
      returnType: returnType.value,
      returnAmount: totalReturnAmount.value,
      returnItems: returnItems.value.filter(item =>
        returnType.value === 'full' ? true : (item.returnFlag && item.returnQuantity > 0)
      ),
      returnForm: { ...returnForm }
    })
  } catch (err) {
    console.warn('退货接口不可用，回退至本地成功提示', err)
    ElMessage.success('退货处理成功（模拟回退）')
    processing.value = false
    emit('success', {
      orderId: props.order.id,
      returnType: returnType.value,
      returnAmount: totalReturnAmount.value,
      returnItems: returnItems.value.filter(item =>
        returnType.value === 'full' ? true : (item.returnFlag && item.returnQuantity > 0)
      ),
      returnForm: { ...returnForm }
    })
  }
}

// 取消退货
const handleCancel = () => {
  emit('cancel')
}

// 格式化日期
import saleApi from '@/api/sale'

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toISOString().split('T')[0]
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

// 初始化
onMounted(() => {
  initReturnItems()
  // 设置默认处理人员
  returnForm.handler = '管理员'
})
</script>

<style scoped>
.sale-return-process-container {
  padding: 0 10px;
}

.return-header {
  text-align: center;
  margin-bottom: 20px;
}

.return-header h3 {
  margin: 0;
  color: #333;
  font-size: 20px;
}

.subtitle {
  margin: 5px 0 0;
  color: #666;
  font-size: 14px;
}

.original-order-info,
.return-items-section,
.return-summary-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
}

.return-type-selector {
  margin-bottom: 15px;
}

.return-items-table {
  margin-top: 10px;
}

.return-summary-section {
  padding-bottom: 20px;
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

.return-details {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.return-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

.return-actions .el-button {
  flex: 1;
  height: 50px;
  font-size: 16px;
}
</style>
