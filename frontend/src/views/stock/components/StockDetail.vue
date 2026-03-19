<!-- src/views/stock/components/StockDetail.vue -->
<template>
  <div class="stock-detail">
    <!-- 药品基本信息 -->
    <div class="section">
      <h3 class="section-title">药品基本信息</h3>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="药品编码">{{ stockData.drugCode }}</el-descriptions-item>
        <el-descriptions-item label="药品名称">{{ stockData.drugName }}</el-descriptions-item>
        <el-descriptions-item label="规格">{{ stockData.specification }}</el-descriptions-item>
        <el-descriptions-item label="单位">{{ stockData.unit }}</el-descriptions-item>
        <el-descriptions-item label="批次号">{{ stockData.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="是否处方药">
          <el-tag :type="stockData.isPrescription ? 'danger' : 'success'" size="small">
            {{ stockData.isPrescription ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 库存信息 -->
    <div class="section">
      <h3 class="section-title">库存信息</h3>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="当前库存">
          <span :class="{
            'stock-value': true,
            'stock-warning': stockData.currentStock < stockData.minStock,
            'stock-danger': stockData.currentStock === 0
          }">
            {{ stockData.currentStock }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="库存状态">
          <el-tag :type="getStockStatusTag(stockData)" size="small">
            {{ getStockStatusText(stockData) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="最低库存">{{ stockData.minStock }}</el-descriptions-item>
        <el-descriptions-item label="最高库存">{{ stockData.maxStock }}</el-descriptions-item>
        <el-descriptions-item label="存放位置">
          <el-tag :type="getLocationTag(stockData.location)" size="small">
            {{ getLocationText(stockData.location) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="供应商">{{ stockData.supplier }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 生产有效期信息 -->
    <div class="section">
      <h3 class="section-title">生产与有效期</h3>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="生产日期">{{ formatDate(stockData.productionDate) }}</el-descriptions-item>
        <el-descriptions-item label="有效期至">
          <span :class="{
            'expiry-date': true,
            'expiry-warning': isExpiringSoon(stockData.expiryDate),
            'expiry-danger': isExpired(stockData.expiryDate)
          }">
            {{ formatDate(stockData.expiryDate) }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="剩余有效期">
          {{ getRemainingDays(stockData.expiryDate) }}
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 价格信息 -->
    <div class="section">
      <h3 class="section-title">价格信息</h3>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="单价(元)">
          ¥{{ stockData.unitPrice.toFixed(2) }}
        </el-descriptions-item>
        <el-descriptions-item label="库存总价值">
          <span class="total-value">
            ¥{{ (stockData.currentStock * stockData.unitPrice).toFixed(2) }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="最后更新时间">
          {{ formatDateTime(stockData.lastUpdated) }}
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="handleAdjust">库存调整</el-button>
      <el-button type="warning" @click="handleTransfer">库存调拨</el-button>
      <el-button @click="handlePrint">打印详情</el-button>
      <el-button @click="handleClose">关闭</el-button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  stockData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['close', 'adjust'])

// 工具函数
const getStockStatusText = (item) => {
  if (item.currentStock === 0) return '无库存'
  if (item.currentStock < item.minStock) return '库存不足'
  if (item.currentStock > item.maxStock * 0.8) return '库存充足'
  return '库存正常'
}

const getStockStatusTag = (item) => {
  if (item.currentStock === 0) return 'danger'
  if (item.currentStock < item.minStock) return 'warning'
  if (item.currentStock > item.maxStock * 0.8) return 'info'
  return 'success'
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

const getRemainingDays = (dateString) => {
  if (!dateString) return '未设置'
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = date.getTime() - now.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays < 0) return '已过期'
  if (diffDays === 0) return '今天到期'
  return `${diffDays} 天`
}

// 操作方法
const handleAdjust = () => {
  emit('adjust')
}

const handleTransfer = async () => {
  try {
    await stockApi.transferStock(props.stockData.id, { toLocation: 'B' })
    ElMessage.success('库存调拨已提交')
  } catch (err) {
    console.warn('库存调拨接口不可用，回退模拟提示', err)
    ElMessage.info('库存调拨（模拟）')
  }
}

const handlePrint = async () => {
  try {
    const res = await stockApi.printWarning(props.stockData.id)
    const data = res.data ?? res
    if (data && (data.url || data.downloadUrl || data.link)) {
      window.open(data.url ?? data.downloadUrl ?? data.link, '_blank')
      ElMessage.success('正在下载打印内容')
    } else {
      throw new Error('后端未返回打印链接')
    }
  } catch (err) {
    console.warn('打印接口不可用，回退模拟提示', err)
    ElMessage.info('打印功能（模拟）')
  }
}

const handleClose = () => {
  emit('close')
}
</script>

<style scoped>
.stock-detail {
  padding: 10px;
}

.section {
  margin-bottom: 20px;
}

.section-title {
  margin: 0 0 10px;
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.stock-value {
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

.total-value {
  color: #e6a23c;
  font-weight: bold;
  font-size: 18px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>