<!-- src/views/purchase/PurchaseOrder.vue -->
<template>
  <div class="purchase-order-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>进货单管理</h2>
      <p class="subtitle">录入新的药品进货信息，系统将自动更新库存</p>
    </div>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="saveOrder" :icon="Check" :loading="saving">
        保存进货单
      </el-button>
      <el-button @click="resetForm" :icon="Refresh">
        重置表单
      </el-button>
      <el-button @click="printOrder" :icon="Printer">
        打印预览
      </el-button>
      <el-button @click="showOrderHistory" :icon="List">
        查看历史
      </el-button>
    </div>

    <!-- 主要表单区域 -->
    <el-card class="form-card">
      <!-- 基本信息 -->
      <div class="form-section">
        <h3 class="section-title">📋 进货单基本信息</h3>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="进货单号" required>
              <el-input
                v-model="formData.orderNo"
                placeholder="系统自动生成"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="供应商" required>
              <el-select
                v-model="formData.supplierId"
                placeholder="请选择供应商"
                style="width: 100%"
                @change="handleSupplierChange"
                filterable
                clearable
              >
                <el-option
                  v-for="supplier in supplierList"
                  :key="supplier.id"
                  :label="supplier.name"
                  :value="supplier.id"
                >
                  <div class="supplier-option">
                    <span>{{ supplier.name }}</span>
                    <span class="supplier-phone">{{ supplier.phone }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="进货日期" required>
              <el-date-picker
                v-model="formData.purchaseDate"
                type="date"
                placeholder="选择进货日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="预计到货日期">
              <el-date-picker
                v-model="formData.expectedDate"
                type="date"
                placeholder="选择预计到货日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="采购员" required>
              <el-select
                v-model="formData.handler"
                placeholder="请选择采购员"
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
            <el-form-item label="仓库">
              <el-select
                v-model="formData.warehouse"
                placeholder="请选择仓库"
                style="width: 100%"
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
            <el-form-item label="进货类型">
              <el-select
                v-model="formData.purchaseType"
                placeholder="请选择进货类型"
                style="width: 100%"
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
                v-model="formData.paymentMethod"
                placeholder="请选择结算方式"
                style="width: 100%"
              >
                <el-option label="现金" value="cash" />
                <el-option label="银行转账" value="bank" />
                <el-option label="支付宝" value="alipay" />
                <el-option label="微信支付" value="wechat" />
                <el-option label="赊账" value="credit" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input
                v-model="formData.remark"
                type="textarea"
                :rows="2"
                placeholder="请输入备注信息"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 药品明细 -->
      <div class="form-section">
        <div class="section-header">
          <h3 class="section-title">📦 药品进货明细</h3>
          <div class="section-actions">
            <el-button type="success" @click="addDrugItem" :icon="Plus">
              添加药品
            </el-button>
            <el-button @click="batchImport" :icon="Upload">
              批量导入
            </el-button>
            <el-button @click="clearAllItems" :icon="Delete">
              清空列表
            </el-button>
          </div>
        </div>

        <!-- 药品明细表格 -->
        <div class="drug-table-container">
          <el-table
            :data="drugItems"
            style="width: 100%"
            border
            :row-class-name="tableRowClassName"
            class="drug-table"
          >
            <el-table-column label="序号" width="60" type="index" align="center" />
            
            <el-table-column label="药品信息" width="300">
              <template #default="{ row, $index }">
                <div class="drug-info-cell">
                  <el-select
                    v-model="row.drugId"
                    placeholder="选择药品"
                    style="width: 100%"
                    filterable
                    clearable
                    @change="(value) => handleDrugChange(value, $index)"
                  >
                    <el-option
                      v-for="drug in drugList"
                      :key="drug.id"
                      :label="`${drug.drugName || drug.name || '未知药品'} (${drug.specification || ''})`"
                      :value="drug.id"
                    >
                      <div class="drug-option">
                        <div class="drug-name">{{ drug.drugName || drug.name || '未知药品' }}</div>
                        <div class="drug-details">
                          <span>规格：{{ drug.specification || '' }}</span>
                          <span>厂家：{{ drug.manufacturer || '' }}</span>
                          <span>库存：{{ drug.stock || 0 }}</span>
                        </div>
                      </div>
                    </el-option>
                  </el-select>
                  <div v-if="row.drugName" class="drug-selected-info">
                    <el-tag size="small" :type="row.isPrescription ? 'danger' : 'success'">
                      {{ row.isPrescription ? '处方药' : '非处方药' }}
                    </el-tag>
                    <span class="manufacturer">{{ row.manufacturer }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="批次号" width="150">
              <template #default="{ row, $index }">
                <el-input
                  v-model="row.batchNo"
                  placeholder="输入批次号"
                  @change="updateItem($index)"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="生产日期" width="120">
              <template #default="{ row, $index }">
                <el-date-picker
                  v-model="row.productionDate"
                  type="date"
                  placeholder="生产日期"
                  style="width: 100%"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  @change="updateItem($index)"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="有效期至" width="120">
              <template #default="{ row, $index }">
                <el-date-picker
                  v-model="row.expiryDate"
                  type="date"
                  placeholder="有效期"
                  style="width: 100%"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  @change="updateItem($index)"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="进货数量" width="120">
              <template #default="{ row, $index }">
                <el-input-number
                  v-model="row.quantity"
                  :min="1"
                  :max="9999"
                  :step="1"
                  style="width: 100%"
                  @change="updateItem($index)"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="单位" width="80">
              <template #default="{ row }">
                <span>{{ row.unit || '件' }}</span>
              </template>
            </el-table-column>
            
            <el-table-column label="进货单价(元)" width="130">
              <template #default="{ row, $index }">
                <el-input-number
                  v-model="row.unitPrice"
                  :min="0"
                  :max="99999"
                  :precision="2"
                  :step="0.01"
                  style="width: 100%"
                  @change="updateItem($index)"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="金额(元)" width="120">
              <template #default="{ row }">
                <div class="amount-cell">
                  {{ calculateItemAmount(row).toFixed(2) }}
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="税率%" width="100">
              <template #default="{ row, $index }">
                <el-input-number
                  v-model="row.taxRate"
                  :min="0"
                  :max="100"
                  :precision="2"
                  :step="0.1"
                  style="width: 100%"
                  @change="updateItem($index)"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="含税金额(元)" width="130">
              <template #default="{ row }">
                <div class="tax-amount-cell">
                  {{ calculateTaxAmount(row).toFixed(2) }}
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="备注" width="150">
              <template #default="{ row, $index }">
                <el-input
                  v-model="row.remark"
                  placeholder="备注"
                  size="small"
                  @change="updateItem($index)"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="80" fixed="right">
              <template #default="{ $index }">
                <el-button
                  type="danger"
                  link
                  @click="removeDrugItem($index)"
                  :icon="Delete"
                  size="small"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 汇总信息 -->
        <div class="summary-section">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="6" :lg="6">
              <div class="summary-item">
                <span class="label">品种数量：</span>
                <span class="value">{{ drugItems.length }}</span>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" :lg="6">
              <div class="summary-item">
                <span class="label">总数量：</span>
                <span class="value">{{ totalQuantity }}</span>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" :lg="6">
              <div class="summary-item">
                <span class="label">总金额：</span>
                <span class="value total-amount">¥{{ totalAmount.toFixed(2) }}</span>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" :lg="6">
              <div class="summary-item">
                <span class="label">总税额：</span>
                <span class="value tax-amount">¥{{ totalTaxAmount.toFixed(2) }}</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 费用信息 -->
      <div class="form-section">
        <h3 class="section-title">💰 费用信息</h3>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="运费(元)">
              <el-input-number
                v-model="formData.freight"
                :min="0"
                :max="99999"
                :precision="2"
                :step="10"
                style="width: 100%"
                @change="calculateTotal"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="其他费用(元)">
              <el-input-number
                v-model="formData.otherCharges"
                :min="0"
                :max="99999"
                :precision="2"
                style="width: 100%"
                @change="calculateTotal"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="折扣金额(元)">
              <el-input-number
                v-model="formData.discount"
                :min="0"
                :max="totalAmount"
                :precision="2"
                style="width: 100%"
                @change="calculateTotal"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="实际应付(元)">
              <div class="actual-amount">
                ¥{{ actualPayable.toFixed(2) }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 供应商信息 -->
      <div class="form-section" v-if="selectedSupplier">
        <h3 class="section-title">🏢 供应商信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="供应商名称">{{ selectedSupplier.name }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ selectedSupplier.contactPerson }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ selectedSupplier.phone }}</el-descriptions-item>
          <el-descriptions-item label="地址">{{ selectedSupplier.address }}</el-descriptions-item>
          <el-descriptions-item label="开户银行">{{ selectedSupplier.bankName }}</el-descriptions-item>
          <el-descriptions-item label="银行账号">{{ selectedSupplier.bankAccount }}</el-descriptions-item>
          <el-descriptions-item label="信用等级">
            <el-rate
              v-model="selectedSupplier.creditLevel"
              disabled
              show-score
              text-color="#ff9900"
              :max="5"
            />
          </el-descriptions-item>
          <el-descriptions-item label="结账周期">{{ selectedSupplier.paymentPeriod }}天</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <!-- 操作指南 -->
    <el-card class="guide-card">
      <template #header>
        <span class="guide-title">📝 操作指南</span>
      </template>
      <div class="guide-content">
        <el-alert
          title="温馨提示"
          type="info"
          :closable="false"
          description="请仔细核对进货信息，保存后系统将自动更新库存，并生成应付账款记录。"
        />
        
        <div class="guide-steps">
          <div class="step">
            <div class="step-number">1</div>
            <div class="step-content">
              <h4>填写基本信息</h4>
              <p>选择供应商、填写进货日期、采购员等基本信息。</p>
            </div>
          </div>
          <div class="step">
            <div class="step-number">2</div>
            <div class="step-content">
              <h4>添加药品明细</h4>
              <p>点击"添加药品"按钮，选择药品并填写数量、单价等信息。</p>
            </div>
          </div>
          <div class="step">
            <div class="step-number">3</div>
            <div class="step-content">
              <h4>核对费用信息</h4>
              <p>确认运费、其他费用和折扣，系统会自动计算应付金额。</p>
            </div>
          </div>
          <div class="step">
            <div class="step-number">4</div>
            <div class="step-content">
              <h4>保存进货单</h4>
              <p>点击保存按钮，系统将生成进货单并更新库存。</p>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 保存确认对话框 -->
    <el-dialog
      v-model="saveDialogVisible"
      title="保存确认"
      width="500px"
      center
    >
      <div class="save-dialog">
        <el-icon color="#67C23A" size="48"><SuccessFilled /></el-icon>
        <h3>确认保存进货单吗？</h3>
        <div class="save-summary">
          <p>进货单号：{{ formData.orderNo }}</p>
          <p>供应商：{{ selectedSupplier?.name || '未选择' }}</p>
          <p>药品品种：{{ drugItems.length }} 种</p>
          <p>总金额：¥{{ actualPayable.toFixed(2) }}</p>
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
  Check, Refresh, Printer, List,
  Plus, Upload, Delete, SuccessFilled
} from '@element-plus/icons-vue'
import purchaseApi from '@/api/purchase'
import drugApi from '@/api/drug'
import authApi from '@/api/auth'


const router = useRouter()

// 表单数据
const formData = reactive({
  orderNo: '',
  supplierId: null,
  purchaseDate: '',
  expectedDate: '',
  handler: '',
  warehouse: 'main',
  purchaseType: 'normal',
  paymentMethod: 'cash',
  remark: '',
  freight: 0,
  otherCharges: 0,
  discount: 0
})

// 药品明细
const drugItems = ref([])

// 供应商列表
const supplierList = ref([])

// 药品列表
const drugList = ref([])

// 用户列表（采购员）
const userList = ref([])

// 选中的供应商
const selectedSupplier = ref(null)

// 对话框状态
const saveDialogVisible = ref(false)

// 保存状态
const saving = ref(false)

// 计算总数量
const totalQuantity = computed(() => {
  return drugItems.value.reduce((sum, item) => sum + (item.quantity || 0), 0)
})

// 计算总金额
const totalAmount = computed(() => {
  return drugItems.value.reduce((sum, item) => {
    return sum + calculateItemAmount(item)
  }, 0)
})

// 计算总税额
const totalTaxAmount = computed(() => {
  return drugItems.value.reduce((sum, item) => {
    return sum + calculateTaxAmount(item)
  }, 0)
})

// 计算实际应付
const actualPayable = computed(() => {
  return totalAmount.value + totalTaxAmount.value + formData.freight + formData.otherCharges - formData.discount
})

// 初始化数据
onMounted(() => {
  generateOrderNo()
  loadSuppliers()
  loadDrugs()
  loadUsers()
  
  // 设置默认值
  const today = new Date()
  formData.purchaseDate = today.toISOString().split('T')[0]
  formData.expectedDate = new Date(today.getTime() + 3 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
  
  // 添加一个空的药品行
  addDrugItem()
})

// 生成进货单号
const generateOrderNo = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = String(Math.floor(Math.random() * 10000)).padStart(4, '0')
  formData.orderNo = `PO${year}${month}${day}${random}`
}

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
}

// 加载药品数据
const loadDrugs = async () => {
  try {
    const res = await drugApi.getDrugList()
    const data = res.data ?? res
    const list = data.data ?? data
    drugList.value = Array.isArray(list) ? list : (list.items ?? [])
  } catch (err) {
    console.error('加载药品列表失败', err)
    ElMessage.error(err.message || '加载药品列表失败')
    drugList.value = []
  }
}

// 加载用户列表（采购员）
const loadUsers = async () => {
  try {
    const res = await authApi.getUsers()
    const data = res.data ?? res
    userList.value = Array.isArray(data) ? data : []
    
    // 如果有用户列表，设置默认采购员为第一个用户
    if (userList.value.length > 0 && !formData.handler) {
      formData.handler = userList.value[0]
    }
  } catch (err) {
    console.error('加载用户列表失败', err)
    ElMessage.error(err.message || '加载用户列表失败')
    userList.value = []
  }
}

// 添加药品项
const addDrugItem = () => {
  drugItems.value.push({
    id: Date.now() + drugItems.value.length,
    drugId: null,
    drugName: '',
    specification: '',
    manufacturer: '',
    unit: '盒',
    isPrescription: false,
    batchNo: '',
    productionDate: '',
    expiryDate: '',
    quantity: 1,
    unitPrice: 0,
    taxRate: 13,
    remark: ''
  })
}

// 删除药品项
const removeDrugItem = (index) => {
  if (drugItems.value.length <= 1) {
    ElMessage.warning('至少需要保留一个药品项')
    return
  }
  
  drugItems.value.splice(index, 1)
  calculateTotal()
}

// 清空所有药品项
const clearAllItems = () => {
  ElMessageBox.confirm(
    '确定要清空所有药品项吗？',
    '确认清空',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    drugItems.value = []
    addDrugItem()
    ElMessage.success('已清空药品列表')
  })
}

// 药品选择变化
const handleDrugChange = (drugId, index) => {
  const drug = drugList.value.find(d => d.id === drugId)
  if (drug) {
    const item = drugItems.value[index]
    item.drugName = drug.drugName || drug.name || ''
    item.specification = drug.specification || drug.spec || ''
    item.manufacturer = drug.manufacturer || ''
    item.unit = drug.unit || '盒'
    item.isPrescription = drug.isPrescription || false
    item.unitPrice = drug.price || drug.unitPrice || drug.retailPrice || 0
    
    // 如果后端提供批次/生产/有效期信息，应在此处根据药品选择加载；当前回退为空
    item.batchNo = item.batchNo || ''
    item.productionDate = item.productionDate || ''
    item.expiryDate = item.expiryDate || ''
    
    updateItem(index)
  }
}

// 供应商选择变化
const handleSupplierChange = (supplierId) => {
  const supplier = supplierList.value.find(s => s.id === supplierId)
  selectedSupplier.value = supplier
  
  // 如果是老供应商，可以加载历史进货价格
  if (supplier) {
    ElMessage.info(`已选择供应商：${supplier.name}`)
  }
}

// 计算单项金额
const calculateItemAmount = (item) => {
  return (item.quantity || 0) * (item.unitPrice || 0)
}

// 计算单项含税金额
const calculateTaxAmount = (item) => {
  const amount = calculateItemAmount(item)
  const taxRate = item.taxRate || 0
  return amount * taxRate / 100
}

// 更新项目
const updateItem = (index) => {
  // 触发计算更新
  calculateTotal()
}

// 计算总计
const calculateTotal = () => {
  // 计算属性会自动更新，这里只是触发计算
  return actualPayable.value
}

// 保存进货单
const saveOrder = () => {
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
    // 仓库名称映射
    const warehouseMap = {
      'main': '主仓库',
      'backup': '备用仓库',
      'cold': '冷库',
      'western': '西药库',
      'traditional': '中药库'
    }
    
    // 构建请求数据，确保数据类型正确
    const payload = {
      supplierId: formData.supplierId,
      purchaseDate: formData.purchaseDate,
      expectedDate: formData.expectedDate || null,
      handler: formData.handler,
      warehouse: warehouseMap[formData.warehouse] || formData.warehouse || '主仓库',
      purchaseType: formData.purchaseType || null,
      paymentMethod: formData.paymentMethod || null,
      remark: formData.remark || null,
      freight: formData.freight ? Number(formData.freight) : null,
      otherCharges: formData.otherCharges ? Number(formData.otherCharges) : null,
      discount: formData.discount ? Number(formData.discount) : null,
      items: drugItems.value.map(i => {
        // 确保必填字段存在
        if (!i.batchNo || !i.batchNo.trim()) {
          throw new Error('批次号不能为空，请为所有药品项填写批次号')
        }
        if (!i.expiryDate) {
          throw new Error('有效期不能为空，请为所有药品项选择有效期')
        }
        
        return {
          drugId: i.drugId,
          quantity: Number(i.quantity) || 0,
          unitPrice: typeof i.unitPrice === 'number' ? i.unitPrice : (i.unitPrice ? parseFloat(i.unitPrice) : 0),
          batchNo: i.batchNo.trim(),
          productionDate: i.productionDate || null,
          expiryDate: i.expiryDate,
          taxRate: typeof i.taxRate === 'number' ? i.taxRate : (i.taxRate ? parseFloat(i.taxRate) : 0)
        }
      })
    }

    const res = await purchaseApi.addPurchaseOrder(payload)
    
    // 处理响应：后端返回格式为 {code, message, data}
    if (res.code === 0 || res.code === undefined) {
      // 成功
      ElMessage.success(res.message || '进货单保存成功！')
      saveDialogVisible.value = false
      router.push('/purchase/history')
    } else {
      // 失败
      ElMessage.error(res.message || '保存进货单失败')
    }
  } catch (err) {
    console.error('保存进货单失败', err)
    // 错误消息已经在 request.js 中显示，这里只记录日志
    // 如果错误消息为空，则显示默认消息
    if (!err.message || err.message === 'Error') {
      // 不重复显示错误消息，因为 request.js 已经显示了
    }
  } finally {
    saving.value = false
  }
} 

// 验证表单
const validateForm = () => {
  // 验证供应商
  if (!formData.supplierId) {
    ElMessage.error('请选择供应商')
    return false
  }
  
  // 验证进货日期
  if (!formData.purchaseDate) {
    ElMessage.error('请选择进货日期')
    return false
  }
  
  // 验证采购员
  if (!formData.handler || !formData.handler.trim()) {
    ElMessage.error('请选择采购员')
    return false
  }
  
  // 验证药品明细
  if (drugItems.value.length === 0) {
    ElMessage.error('请至少添加一个药品项')
    return false
  }
  
  // 验证每个药品项
  for (let i = 0; i < drugItems.value.length; i++) {
    const item = drugItems.value[i]
    if (!item.drugId) {
      ElMessage.error(`第 ${i + 1} 行：请选择药品`)
      return false
    }
    
    if (!item.quantity || item.quantity <= 0) {
      ElMessage.error(`第 ${i + 1} 行：请输入有效的进货数量`)
      return false
    }
    
    if (!item.unitPrice || item.unitPrice <= 0) {
      ElMessage.error(`第 ${i + 1} 行：请输入有效的进货单价`)
      return false
    }
    
    if (!item.batchNo || !item.batchNo.trim()) {
      ElMessage.error(`第 ${i + 1} 行：请输入批次号`)
      return false
    }
    
    if (!item.expiryDate) {
      ElMessage.error(`第 ${i + 1} 行：请选择有效期`)
      return false
    }
  }
  
  return true
}

// 重置表单
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
    // 重置表单数据
    Object.assign(formData, {
      orderNo: '',
      supplierId: null,
      purchaseDate: new Date().toISOString().split('T')[0],
      expectedDate: new Date(new Date().getTime() + 3 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      handler: userList.value.length > 0 ? userList.value[0] : '',
      warehouse: 'main',
      purchaseType: 'normal',
      paymentMethod: 'cash',
      remark: '',
      freight: 0,
      otherCharges: 0,
      discount: 0
    })
    
    // 重新生成单号
    generateOrderNo()
    
    // 清空药品明细
    drugItems.value = []
    addDrugItem()
    
    // 清空供应商选择
    selectedSupplier.value = null
    
    ElMessage.success('表单已重置')
  })
}

// 打印订单
const printOrder = () => {
  ElMessage.info('打印预览功能开发中...')
}

// 查看历史
const showOrderHistory = () => {
  router.push('/purchase/history')
}

// 批量导入
const batchImport = () => {
  ElMessage.info('批量导入功能开发中...')
}

// 表格行样式
const tableRowClassName = ({ rowIndex }) => {
  if (rowIndex % 2 === 0) {
    return 'row-even'
  }
  return 'row-odd'
}
</script>

<style scoped>
.purchase-order-container {
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

.drug-table-container {
  margin: 20px 0;
  overflow-x: auto;
}

.drug-table {
  min-width: 1400px;
}

.drug-info-cell {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.drug-option {
  padding: 8px 0;
}

.drug-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.drug-details {
  font-size: 12px;
  color: #666;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.drug-selected-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 5px;
}

.manufacturer {
  font-size: 12px;
  color: #666;
}

.amount-cell,
.tax-amount-cell {
  font-weight: bold;
  color: #e6a23c;
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
  font-size: 20px;
}

.tax-amount {
  color: #409EFF;
}

.actual-amount {
  font-size: 20px;
  font-weight: bold;
  color: #67C23A;
  text-align: center;
  padding: 8px;
  background-color: #f0f9eb;
  border-radius: 4px;
}

.guide-card {
  margin-top: 20px;
}

.guide-title {
  font-size: 16px;
  font-weight: 500;
}

.guide-content {
  padding: 10px 0;
}

.guide-steps {
  margin-top: 20px;
}

.step {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
}

.step-number {
  width: 32px;
  height: 32px;
  background-color: #409EFF;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 15px;
  flex-shrink: 0;
}

.step-content h4 {
  margin: 0 0 5px;
  font-size: 16px;
  color: #333;
}

.step-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
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

/* 表格行样式 */
:deep(.row-even) {
  background-color: #fafafa;
}

:deep(.row-odd) {
  background-color: white;
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
  
  .step {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .step-number {
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style>