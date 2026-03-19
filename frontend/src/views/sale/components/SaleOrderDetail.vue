<!-- src/views/sale/components/SaleOrderDetail.vue -->
<template>
  <div class="sale-order-detail-container" v-if="order">
    <!-- 基本信息 -->
    <el-card class="detail-section">
      <template #header>
        <span class="section-title">📋 销售单基本信息</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="销售单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ order.customerName }}</el-descriptions-item>
        <el-descriptions-item label="会员号">{{ order.memberId || '无' }}</el-descriptions-item>
        <el-descriptions-item label="销售日期">{{ formatDate(order.saleDate) }}</el-descriptions-item>
        <el-descriptions-item label="销售员">{{ order.salesman }}</el-descriptions-item>
        <el-descriptions-item label="销售类型">
          <el-tag :type="getSaleTypeTag(order.saleType)">
            {{ getSaleTypeText(order.saleType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="付款方式">{{ getPaymentMethodText(order.paymentMethod) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(order.status)">
            {{ getStatusText(order.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处方编号" v-if="order.isPrescription">{{ order.prescriptionNo || '无' }}</el-descriptions-item>
        <el-descriptions-item label="医生姓名" v-if="order.isPrescription">{{ order.doctorName || '无' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(order.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="药品品种">{{ order.drugCount }} 种</el-descriptions-item>
        <el-descriptions-item label="总数量">{{ order.totalQuantity }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="order.remark" class="remark-section">
        <h4>备注：</h4>
        <p>{{ order.remark }}</p>
      </div>
    </el-card>

    <!-- 药品明细 -->
    <el-card class="detail-section">
      <template #header>
        <span class="section-title">💊 药品销售明细</span>
      </template>
      <div class="no-data" v-if="!drugItems || drugItems.length === 0">
        <el-empty description="暂无药品明细数据" />
      </div>
      <el-table v-else :data="drugItems" border style="width: 100%">
        <el-table-column label="序号" width="60" type="index" align="center" />
        <el-table-column label="药品名称" prop="drugName" />
        <el-table-column label="规格" prop="specification" />
        <el-table-column label="生产厂家" prop="manufacturer" />
        <el-table-column label="批次号" prop="batchNo" />
        <el-table-column label="有效期" prop="expiryDate">
          <template #default="{ row }">
            {{ formatDate(row.expiryDate) }}
          </template>
        </el-table-column>
        <el-table-column label="数量" prop="quantity" align="right" />
        <el-table-column label="单位" prop="unit" />
        <el-table-column label="销售单价(元)" align="right">
          <template #default="{ row }">
            {{ row.salePrice?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="折扣%" align="right">
          <template #default="{ row }">
            {{ row.discount || 0 }}%
          </template>
        </el-table-column>
        <el-table-column label="折扣后单价(元)" align="right">
          <template #default="{ row }">
            {{ ((row.salePrice || 0) * (1 - (row.discount || 0) / 100)).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="金额(元)" align="right">
          <template #default="{ row }">
            {{ ((row.quantity || 0) * (row.salePrice || 0) * (1 - (row.discount || 0) / 100)).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="是否可退货" align="center" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.returnable" type="success" size="small">可退</el-tag>
            <el-tag v-else type="info" size="small">不可退</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 汇总信息 -->
      <div class="summary-section">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6" :lg="6">
            <div class="summary-item">
              <span class="label">品种数量：</span>
              <span class="value">{{ order.drugCount }}</span>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" :lg="6">
            <div class="summary-item">
              <span class="label">总数量：</span>
              <span class="value">{{ order.totalQuantity }}</span>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" :lg="6">
            <div class="summary-item">
              <span class="label">原价总额：</span>
              <span class="value original-amount">¥{{ order.originalAmount.toFixed(2) }}</span>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" :lg="6">
            <div class="summary-item">
              <span class="label">实收金额：</span>
              <span class="value actual-amount">¥{{ order.actualAmount.toFixed(2) }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20" class="summary-row">
          <el-col :span="24">
            <div class="summary-item">
              <span class="label">折扣总额：</span>
              <span class="value discount-amount">-¥{{ order.discountAmount.toFixed(2) }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 操作记录 -->
    <el-card class="detail-section" v-if="operationLogs.length > 0">
      <template #header>
        <span class="section-title">📝 操作记录</span>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="(log, index) in operationLogs"
          :key="index"
          :timestamp="formatDateTime(log.time)"
          :type="getLogType(log.type)"
          placement="top"
        >
          <el-card>
            <h4>{{ log.title }}</h4>
            <p>{{ log.content }}</p>
            <div class="log-operator">操作人：{{ log.operator }}</div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  order: {
    type: Object,
    required: true
  }
})

// 模拟药品明细数据
const drugItems = computed(() => {
  if (!props.order) return []
  
  const items = []
  const drugNames = [
    '阿莫西林胶囊', '布洛芬缓释胶囊', '头孢克肟片', '连花清瘟胶囊',
    '维生素C片', '板蓝根颗粒', '硝苯地平缓释片', '二甲双胍片'
  ]
  const manufacturers = ['白云山制药', '华润医药', '同仁堂', '修正药业', '云南白药']
  
  for (let i = 0; i < props.order.drugCount; i++) {
    const today = new Date()
    const expiryDate = new Date(today)
    expiryDate.setDate(expiryDate.getDate() + 365) // 1年后
    
    const returnable = Math.random() > 0.3 // 70%可退货
    
    items.push({
      id: i + 1,
      drugName: drugNames[i % drugNames.length],
      specification: `${['250mg', '300mg', '100mg', '0.35g'][i % 4]}*${[24, 20, 10, 100][i % 4]}${['粒', '粒', '片', '片'][i % 4]}`,
      manufacturer: manufacturers[i % manufacturers.length],
      batchNo: `BATCH${String(Math.floor(Math.random() * 10000)).padStart(4, '0')}`,
      expiryDate: expiryDate.toISOString().split('T')[0],
      quantity: Math.floor(Math.random() * 10) + 1,
      unit: i % 2 === 0 ? '盒' : '瓶',
      salePrice: parseFloat((Math.random() * 100 + 20).toFixed(2)),
      discount: Math.floor(Math.random() * 30),
      returnable
    })
  }
  return items
})

// 模拟操作记录
const operationLogs = computed(() => {
  if (!props.order) return []
  
  const logs = [
    {
      time: props.order.createdAt,
      type: 'create',
      title: '创建销售单',
      content: `销售单 ${props.order.orderNo} 创建成功`,
      operator: props.order.salesman
    }
  ]
  
  // 根据状态添加记录
  if (props.order.status === 'completed') {
    logs.push({
      time: new Date(new Date(props.order.createdAt).getTime() + 5 * 60 * 1000).toISOString(),
      type: 'success',
      title: '完成销售',
      content: '销售已完成，库存已扣除',
      operator: props.order.salesman
    })
  }
  
  if (props.order.status === 'returned') {
    logs.push({
      time: new Date(new Date(props.order.createdAt).getTime() + 24 * 60 * 60 * 1000).toISOString(),
      type: 'danger',
      title: '退货处理',
      content: '客户申请退货，已全额退款',
      operator: '管理员'
    })
  }
  
  if (props.order.status === 'partial_return') {
    logs.push({
      time: new Date(new Date(props.order.createdAt).getTime() + 12 * 60 * 60 * 1000).toISOString(),
      type: 'warning',
      title: '部分退货',
      content: '部分药品退货处理完成',
      operator: '管理员'
    })
  }
  
  if (props.order.status === 'cancelled') {
    logs.push({
      time: new Date(new Date(props.order.createdAt).getTime() + 30 * 60 * 1000).toISOString(),
      type: 'info',
      title: '取消订单',
      content: '销售单已取消，库存已恢复',
      operator: '管理员'
    })
  }
  
  return logs
})

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

// 获取状态标签
const getStatusTag = (status) => {
  if (!status) return 'info'
  const tagMap = {
    completed: 'success',
    returned: 'danger',
    partial_return: 'warning',
    cancelled: 'info'
  }
  return tagMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    completed: '已完成',
    returned: '已退货',
    partial_return: '部分退货',
    cancelled: '已取消'
  }
  return textMap[status] || status
}

// 获取销售类型标签
const getSaleTypeTag = (type) => {
  if (!type) return 'info'
  const tagMap = {
    retail: 'info',
    wholesale: 'success',
    prescription: 'danger',
    urgent: 'warning',
    promotion: 'info'
  }
  return tagMap[type] || 'info'
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

// 获取日志类型
const getLogType = (type) => {
  const typeMap = {
    create: 'primary',
    success: 'success',
    danger: 'danger',
    warning: 'warning',
    info: 'info'
  }
  return typeMap[type] || 'primary'
}
</script>

<style scoped>
.sale-order-detail-container {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 10px;
}

.detail-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
}

.remark-section {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.remark-section h4 {
  margin: 0 0 10px;
  color: #333;
}

.remark-section p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.summary-section {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.summary-row {
  margin-top: 10px;
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

.original-amount {
  color: #e6a23c;
}

.actual-amount {
  color: #67C23A;
}

.discount-amount {
  color: #f56c6c;
}

.no-data {
  padding: 20px;
  text-align: center;
}

.log-operator {
  margin-top: 10px;
  font-size: 12px;
  color: #666;
  text-align: right;
}

/* 滚动条样式 */
.sale-order-detail-container::-webkit-scrollbar {
  width: 6px;
}

.sale-order-detail-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.sale-order-detail-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.sale-order-detail-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>