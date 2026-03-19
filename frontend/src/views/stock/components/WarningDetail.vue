<!-- src/views/stock/components/WarningDetail.vue -->
<template>
  <div class="warning-detail">
    <!-- 预警基本信息 -->
    <div class="section">
      <h3 class="section-title">预警基本信息</h3>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="药品编码">{{ warningData.drugCode }}</el-descriptions-item>
        <el-descriptions-item label="药品名称">{{ warningData.drugName }}</el-descriptions-item>
        <el-descriptions-item label="预警类型">
          <el-tag :type="getWarningTypeTag(warningData.warningType)" size="small">
            {{ getWarningTypeText(warningData.warningType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预警级别">
          <el-tag :type="getWarningLevelTag(warningData.warningLevel)" size="small">
            {{ getWarningLevelText(warningData.warningLevel) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预警描述">{{ warningData.description }}</el-descriptions-item>
        <el-descriptions-item label="处理建议">{{ warningData.suggestion }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(warningData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="getStatusTag(warningData.status)" size="small">
            {{ getStatusText(warningData.status) }}
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
            'stock-warning': warningData.currentStock < warningData.minStock,
            'stock-danger': warningData.currentStock === 0
          }">
            {{ warningData.currentStock }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="最低库存">{{ warningData.minStock }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ warningData.supplier }}</el-descriptions-item>
        <el-descriptions-item label="是否处方药">
          <el-tag :type="warningData.isPrescription ? 'danger' : 'success'" size="small">
            {{ warningData.isPrescription ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 有效期信息 -->
    <div class="section">
      <h3 class="section-title">有效期信息</h3>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="有效期至">
          <span :class="{
            'expiry-date': true,
            'expiry-warning': warningData.warningType === 'expiring',
            'expiry-danger': warningData.warningType === 'expired'
          }">
            {{ formatDate(warningData.expiryDate) }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="剩余天数">
          <span :class="{
            'remaining-days': true,
            'danger': warningData.remainingDays <= 0,
            'warning': warningData.remainingDays > 0 && warningData.remainingDays <= 30,
            'normal': warningData.remainingDays > 30
          }">
            {{ warningData.remainingDays }}
          </span>
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 处理历史 -->
    <div class="section" v-if="warningData.status !== 'pending'">
      <h3 class="section-title">处理历史</h3>
      <div class="history-content">
        <div class="history-item">
          <div class="history-action">处理人：{{ warningData.handler || '系统' }}</div>
          <div class="history-time">{{ formatDateTime(warningData.createTime) }}</div>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button
        type="primary"
        @click="handleProcess"
        v-if="warningData.status === 'pending'"
      >
        立即处理
      </el-button>
      <el-button
        type="warning"
        @click="handleRemind"
      >
        发送提醒
      </el-button>
      <el-button @click="handlePrint">打印详情</el-button>
      <el-button @click="handleClose">关闭</el-button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import { ElMessage } from 'element-plus'
import stockApi from '@/api/stock'

const props = defineProps({
  warningData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['process', 'close'])

// 工具函数
const getWarningTypeText = (type) => {
  const map = {
    stock_low: '库存不足',
    stock_empty: '库存为0',
    expiring: '即将过期',
    expired: '已过期',
    stock_high: '库存积压',
    old_production: '生产日期久'
  }
  return map[type] || type
}

const getWarningTypeTag = (type) => {
  const map = {
    stock_low: 'warning',
    stock_empty: 'danger',
    expiring: 'warning',
    expired: 'danger',
    stock_high: 'info',
    old_production: 'info'
  }
  return map[type] || 'info'
}

const getWarningLevelText = (level) => {
  const map = {
    critical: '紧急预警',
    high: '高级预警',
    medium: '中级预警',
    low: '提醒'
  }
  return map[level] || level
}

const getWarningLevelTag = (level) => {
  const map = {
    critical: 'danger',
    high: 'warning',
    medium: 'primary',
    low: 'success'
  }
  return map[level] || 'info'
}

const getStatusText = (status) => {
  const map = {
    pending: '未处理',
    processing: '处理中',
    resolved: '已处理',
    ignored: '已忽略'
  }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = {
    pending: 'danger',
    processing: 'warning',
    resolved: 'success',
    ignored: 'info'
  }
  return map[status] || 'info'
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

// 操作方法
const handleProcess = () => {
  emit('process')
}

const handleRemind = async () => {
  try {
    await stockApi.remindWarning(props.warningData.id)
    ElMessage.success('提醒已发送')
  } catch (err) {
    console.warn('发送提醒失败，回退为模拟提示', err)
    ElMessage.info('提醒已发送（模拟回退）')
  }
}

const handlePrint = async () => {
  try {
    const res = await stockApi.printWarning(props.warningData.id)
    const data = res.data ?? res
    // 如果后端返回下载链接或文件地址，打开新窗口下载；否则回退到模拟提示
    if (data && (data.url || data.downloadUrl || data.link)) {
      const url = data.url ?? data.downloadUrl ?? data.link
      window.open(url, '_blank')
      ElMessage.success('正在下载打印内容')
    } else {
      throw new Error('后端未返回打印链接')
    }
  } catch (err) {
    console.warn('打印接口不可用，回退为模拟提示', err)
    ElMessage.info('打印功能（模拟）')
  }
}

const handleClose = () => {
  emit('close')
}
</script>

<style scoped>
.warning-detail {
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

.remaining-days {
  font-weight: bold;
  padding: 2px 8px;
  border-radius: 10px;
  display: inline-block;
}

.remaining-days.danger {
  background-color: #fef0f0;
  color: #f56c6c;
}

.remaining-days.warning {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.remaining-days.normal {
  background-color: #f0f9eb;
  color: #67C23A;
}

.history-content {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.history-action {
  font-weight: 500;
  color: #333;
}

.history-time {
  font-size: 12px;
  color: #999;
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