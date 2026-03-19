<!-- src/views/purchase/components/OrderDetail.vue -->
<template>
  <div class="order-detail-container" v-if="order">
    <!-- 基本信息 -->
    <el-card class="detail-section">
      <template #header>
        <span class="section-title">📋 进货单基本信息</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="进货单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ order.supplierName }}</el-descriptions-item>
        <el-descriptions-item label="进货日期">{{ formatDate(order.purchaseDate) }}</el-descriptions-item>
        <el-descriptions-item label="预计到货日期">{{ formatDate(order.expectedDate) }}</el-descriptions-item>
        <el-descriptions-item label="采购员">{{ order.handler }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ getWarehouseText(order.warehouse) }}</el-descriptions-item>
        <el-descriptions-item label="进货类型">{{ getPurchaseTypeText(order.purchaseType) }}</el-descriptions-item>
        <el-descriptions-item label="结算方式">{{ getPaymentMethodText(order.paymentMethod) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(order.status)">
            {{ getStatusText(order.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(order.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="运费">¥{{ order.freight?.toFixed(2) || '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="其他费用">¥{{ order.otherCharges?.toFixed(2) || '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="折扣金额">¥{{ order.discount?.toFixed(2) || '0.00' }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="order.remark" class="remark-section">
        <h4>备注：</h4>
        <p>{{ order.remark }}</p>
      </div>
    </el-card>

    <!-- 药品明细 -->
    <el-card class="detail-section">
      <template #header>
        <span class="section-title">📦 药品进货明细</span>
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
        <el-table-column label="生产日期" prop="productionDate">
          <template #default="{ row }">
            {{ formatDate(row.productionDate) }}
          </template>
        </el-table-column>
        <el-table-column label="有效期至" prop="expiryDate">
          <template #default="{ row }">
            {{ formatDate(row.expiryDate) }}
          </template>
        </el-table-column>
        <el-table-column label="数量" prop="quantity" align="right" />
        <el-table-column label="单位" prop="unit" />
        <el-table-column label="单价(元)" prop="unitPrice" align="right">
          <template #default="{ row }">
            {{ row.unitPrice?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="金额(元)" align="right">
          <template #default="{ row }">
            {{ ((row.quantity || 0) * (row.unitPrice || 0)).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="税率%" prop="taxRate" align="right">
          <template #default="{ row }">
            {{ row.taxRate }}%
          </template>
        </el-table-column>
        <el-table-column label="含税金额(元)" align="right">
          <template #default="{ row }">
            {{ (((row.quantity || 0) * (row.unitPrice || 0)) * (row.taxRate || 0) / 100).toFixed(2) }}
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
              <span class="label">总金额：</span>
              <span class="value total-amount">¥{{ order.totalAmount.toFixed(2) }}</span>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" :lg="6">
            <div class="summary-item">
              <span class="label">实际应付：</span>
              <span class="value actual-amount">
                ¥{{ (order.totalAmount + (order.freight || 0) + (order.otherCharges || 0) - (order.discount || 0)).toFixed(2) }}
              </span>
            </div>
          </el-col>
        </el-row>
      </div>
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

// 药品明细（优先使用后端返回的明细）
const drugItems = computed(() => {
  if (!props.order) return []
  const items = props.order.items ?? props.order.orderItems ?? props.order.itemsList ?? []
  if (Array.isArray(items) && items.length > 0) {
    return items.map(it => ({
      id: it.id,
      drugName: it.drugName ?? it.name,
      specification: it.specification ?? it.spec ?? '',
      manufacturer: it.manufacturer ?? '',
      batchNo: it.batchNo ?? it.batch ?? '',
      productionDate: it.productionDate ?? it.productionDate ?? '',
      expiryDate: it.expiryDate ?? it.expiryDate ?? '',
      quantity: it.quantity ?? it.qty ?? 0,
      unit: it.unit ?? '盒',
      unitPrice: it.unitPrice ?? it.price ?? 0,
      taxRate: it.taxRate ?? 0
    }))
  }
  // 如果后端未返回明细，回退为最小示例
  const fallback = []
  for (let i = 0; i < (props.order.drugCount || 0); i++) {
    fallback.push({
      id: i + 1,
      drugName: `药品 ${i + 1}`,
      specification: '',
      manufacturer: '',
      batchNo: '',
      productionDate: '',
      expiryDate: '',
      quantity: 0,
      unit: '盒',
      unitPrice: 0,
      taxRate: 0
    })
  }
  return fallback
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
    pending: 'warning',
    completed: 'success',
    cancelled: 'danger',
    partial: 'info'
  }
  return tagMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    pending: '待入库',
    completed: '已入库',
    cancelled: '已取消',
    partial: '部分入库'
  }
  return textMap[status] || status
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
.order-detail-container {
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

.total-amount {
  color: #f56c6c;
}

.actual-amount {
  color: #67C23A;
}

.no-data {
  padding: 20px;
  text-align: center;
}

/* 滚动条样式 */
.order-detail-container::-webkit-scrollbar {
  width: 6px;
}

.order-detail-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.order-detail-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.order-detail-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>