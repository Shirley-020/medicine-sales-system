<!-- src/views/sale/SaleOrder.vue -->
<template>
  <div class="sale-order-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>药品销售订单</h2>
      <p class="subtitle">处理药品销售业务，系统将自动扣除库存并生成销售记录</p>
    </div>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="saveOrder" :icon="Check" :loading="saving">
        保存销售单
      </el-button>
      <el-button @click="resetForm" :icon="Refresh">
        重置表单
      </el-button>
      <el-button @click="printOrder" :icon="Printer">
        打印小票
      </el-button>
      <el-button @click="showOrderHistory" :icon="List">
        销售历史
      </el-button>
    </div>

    <!-- 主要表单区域 -->
    <el-card class="form-card">
      <!-- 基本信息 -->
      <div class="form-section">
        <h3 class="section-title">📋 销售单基本信息</h3>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="销售单号" required>
              <el-input
                v-model="formData.orderNo"
                placeholder="系统自动生成"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="客户信息">
              <el-select
                v-model="formData.customerId"
                placeholder="选择客户"
                style="width: 100%"
                @change="handleCustomerChange"
                filterable
                clearable
              >
                <el-option label="散客（非会员）" value="guest" />
                <el-option
                  v-for="customer in customerList"
                  :key="customer.id"
                  :label="customer.name + ' (' + customer.memberId + ')'"
                  :value="customer.id"
                >
                  <div class="customer-option">
                    <div class="customer-name">{{ customer.name }}</div>
                    <div class="customer-details">
                      <span>会员号：{{ customer.memberId }}</span>
                      <span v-if="customer.discount">折扣：{{ customer.discount }}折</span>
                    </div>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="销售日期" required>
              <el-date-picker
                v-model="formData.saleDate"
                type="date"
                placeholder="选择销售日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="销售员" required>
              <el-select
                v-model="formData.salesman"
                placeholder="请选择销售员"
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
            <el-form-item label="付款方式" required>
              <el-select
                v-model="formData.paymentMethod"
                placeholder="请选择付款方式"
                style="width: 100%"
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
            <el-form-item label="销售类型">
              <el-select
                v-model="formData.saleType"
                placeholder="请选择销售类型"
                style="width: 100%"
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
            <el-form-item label="处方编号" v-if="formData.saleType === 'prescription'">
              <el-input
                v-model="formData.prescriptionNo"
                placeholder="请输入处方编号"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="医生姓名" v-if="formData.saleType === 'prescription'">
              <el-input
                v-model="formData.doctorName"
                placeholder="请输入医生姓名"
              />
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

      <!-- 药品销售明细 -->
      <div class="form-section">
        <div class="section-header">
          <h3 class="section-title">💊 药品销售明细</h3>
          <div class="section-actions">
            <el-button type="success" @click="addDrugItem" :icon="Plus">
              添加药品
            </el-button>
            <el-button @click="batchAddFromPrescription" :icon="Document" v-if="formData.saleType === 'prescription'">
              导入处方
            </el-button>
            <el-button @click="clearAllItems" :icon="Delete">
              清空列表
            </el-button>
          </div>
        </div>

        <!-- 搜索药品 -->
        <div class="drug-search">
          <el-input
            v-model="searchKeyword"
            placeholder="输入药品名称、拼音码或条形码搜索药品"
            clearable
            @keyup.enter="searchDrugs"
            style="max-width: 400px; margin-bottom: 15px;"
          >
            <template #append>
              <el-button :icon="Search" @click="searchDrugs" />
            </template>
          </el-input>
          
          <!-- 快速添加药品 -->
          <div class="quick-drugs" v-if="quickDrugList.length > 0 && searchKeyword">
            <el-alert
              title="搜索结果"
              type="info"
              :closable="false"
              style="margin-bottom: 10px;"
            />
            <div class="quick-drug-grid">
              <div
                v-for="drug in quickDrugList"
                :key="drug.id"
                class="quick-drug-item"
                @click="quickAddDrug(drug)"
              >
                <div class="drug-name">{{ drug.name }}</div>
                <div class="drug-spec">{{ drug.specification }}</div>
                <div class="drug-price">¥{{ drug.salePrice?.toFixed(2) }}</div>
                <div class="drug-stock">库存: {{ drug.stock }}</div>
              </div>
            </div>
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
            
            <el-table-column label="药品信息" width="280">
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
                      :label="drug.name + ' (' + drug.specification + ')'"
                      :value="drug.id"
                    >
                      <div class="drug-option">
                        <div class="drug-name">{{ drug.name }}</div>
                        <div class="drug-details">
                          <span>规格：{{ drug.specification }}</span>
                          <span>厂家：{{ drug.manufacturer }}</span>
                          <span>库存：{{ drug.stock }}</span>
                          <span>售价：¥{{ drug.salePrice?.toFixed(2) }}</span>
                        </div>
                      </div>
                    </el-option>
                  </el-select>
                  <div v-if="row.drugName" class="drug-selected-info">
                    <el-tag size="small" :type="row.isPrescription ? 'danger' : 'success'">
                      {{ row.isPrescription ? '处方药' : '非处方药' }}
                    </el-tag>
                    <span class="stock-warning" v-if="row.stockWarning">
                      <el-icon><Warning /></el-icon>
                      库存不足
                    </span>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="批次号" width="140">
              <template #default="{ row }">
                <span>{{ row.batchNo || '系统自动分配' }}</span>
              </template>
            </el-table-column>
            
            <el-table-column label="有效期" width="120">
              <template #default="{ row }">
                <span v-if="row.expiryDate">{{ formatDate(row.expiryDate) }}</span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            
            <el-table-column label="销售数量" width="120">
              <template #default="{ row, $index }">
                <el-input-number
                  v-model="row.quantity"
                  :min="1"
                  :max="row.maxQuantity || 999"
                  :step="1"
                  style="width: 100%"
                  @change="updateItem($index)"
                />
                <div class="stock-info" v-if="row.stock">
                  库存: {{ row.stock }}
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="单位" width="80">
              <template #default="{ row }">
                <span>{{ row.unit || '盒' }}</span>
              </template>
            </el-table-column>
            
            <el-table-column label="销售单价(元)" width="130">
              <template #default="{ row, $index }">
                <el-input-number
                  v-model="row.salePrice"
                  :min="0"
                  :max="99999"
                  :precision="2"
                  :step="0.01"
                  style="width: 100%"
                  @change="updateItem($index)"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="折扣%" width="100">
              <template #default="{ row, $index }">
                <el-input-number
                  v-model="row.discount"
                  :min="0"
                  :max="100"
                  :precision="1"
                  :step="1"
                  style="width: 100%"
                  @change="updateItem($index)"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="折扣后单价(元)" width="140">
              <template #default="{ row }">
                <div class="amount-cell">
                  {{ calculateDiscountedPrice(row).toFixed(2) }}
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="金额(元)" width="120">
              <template #default="{ row }">
                <div class="amount-cell">
                  {{ calculateItemAmount(row).toFixed(2) }}
                </div>
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
                <span class="label">原价总额：</span>
                <span class="value">¥{{ originalTotalAmount.toFixed(2) }}</span>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" :lg="6">
              <div class="summary-item">
                <span class="label">折扣总额：</span>
                <span class="value discount-amount">-¥{{ totalDiscountAmount.toFixed(2) }}</span>
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
            <el-form-item label="会员折扣">
              <el-input-number
                v-model="formData.memberDiscount"
                :min="0"
                :max="100"
                :precision="1"
                :step="5"
                style="width: 100%"
                @change="calculateTotal"
              />
              <span class="unit-suffix">%</span>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="整单折扣">
              <el-input-number
                v-model="formData.orderDiscount"
                :min="0"
                :max="100"
                :precision="1"
                :step="5"
                style="width: 100%"
                @change="calculateTotal"
              />
              <span class="unit-suffix">%</span>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="优惠金额(元)">
              <el-input-number
                v-model="formData.promotionAmount"
                :min="0"
                :max="originalTotalAmount"
                :precision="2"
                style="width: 100%"
                @change="calculateTotal"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="实际应收(元)">
              <div class="actual-amount">
                ¥{{ actualReceivable.toFixed(2) }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 付款详情 -->
        <div class="payment-details" v-if="formData.paymentMethod === 'mixed'">
          <h4>混合支付详情</h4>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="6" :lg="6">
              <el-form-item label="现金支付">
                <el-input-number
                  v-model="formData.cashAmount"
                  :min="0"
                  :max="actualReceivable"
                  :precision="2"
                  style="width: 100%"
                  @change="calculateTotal"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" :lg="6">
              <el-form-item label="微信支付">
                <el-input-number
                  v-model="formData.wechatAmount"
                  :min="0"
                  :max="actualReceivable"
                  :precision="2"
                  style="width: 100%"
                  @change="calculateTotal"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" :lg="6">
              <el-form-item label="支付宝支付">
                <el-input-number
                  v-model="formData.alipayAmount"
                  :min="0"
                  :max="actualReceivable"
                  :precision="2"
                  style="width: 100%"
                  @change="calculateTotal"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" :lg="6">
              <el-form-item label="银行卡支付">
                <el-input-number
                  v-model="formData.bankAmount"
                  :min="0"
                  :max="actualReceivable"
                  :precision="2"
                  style="width: 100%"
                  @change="calculateTotal"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 客户信息 -->
      <div class="form-section" v-if="selectedCustomer">
        <h3 class="section-title">👤 客户信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="客户姓名">{{ selectedCustomer.name }}</el-descriptions-item>
          <el-descriptions-item label="会员号">{{ selectedCustomer.memberId }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ selectedCustomer.phone }}</el-descriptions-item>
          <el-descriptions-item label="积分">{{ selectedCustomer.points }}</el-descriptions-item>
          <el-descriptions-item label="会员等级">
            <el-tag :type="getMemberLevelTag(selectedCustomer.level)">
              {{ getMemberLevelText(selectedCustomer.level) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="折扣率">{{ selectedCustomer.discount }}折</el-descriptions-item>
          <el-descriptions-item label="累计消费">¥{{ selectedCustomer.totalConsumption?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="最后消费">{{ formatDate(selectedCustomer.lastPurchase) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

        <!-- 快速收银模式 (组件不存在，暂时注释) -->
    <!--
    <el-dialog
      v-model="cashierDialogVisible"
      title="快速收银"
      width="800px"
      top="5vh"
      :close-on-click-modal="false"
    >
      <quick-cashier 
        @confirm="handleQuickCashierConfirm"
        @cancel="cashierDialogVisible = false"
      />
    </el-dialog>
    -->

    <!-- 保存确认对话框 -->
    <el-dialog
      v-model="saveDialogVisible"
      title="保存销售单"
      width="500px"
      center
    >
      <div class="save-dialog">
        <el-icon color="#67C23A" size="48"><SuccessFilled /></el-icon>
        <h3>确认保存销售单吗？</h3>
        <div class="save-summary">
          <p>销售单号：{{ formData.orderNo }}</p>
          <p>客户：{{ (selectedCustomer && selectedCustomer.name) || '散客' }}</p>
          <p>药品品种：{{ drugItems.length }} 种</p>
          <p>应收金额：¥{{ actualReceivable.toFixed(2) }}</p>
          <p>付款方式：{{ getPaymentMethodText(formData.paymentMethod) }}</p>
        </div>
        <div class="inventory-check" v-if="hasStockWarning">
          <el-alert
            title="库存提醒"
            type="warning"
            description="部分药品库存不足，保存后库存将变为负数"
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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Check, Refresh, Printer, List,
  Plus, Delete, SuccessFilled, Search,
  Document, Warning
} from '@element-plus/icons-vue'
import saleApi from '@/api/sale'
import customerApi from '@/api/customer'
import drugApi from '@/api/drug'
import authApi from '@/api/auth'

const router = useRouter()

// 表单数据
const formData = reactive({
  orderNo: '',
  customerId: 'guest',
  saleDate: '',
  salesman: '',
  paymentMethod: 'cash',
  saleType: 'retail',
  prescriptionNo: '',
  doctorName: '',
  remark: '',
  memberDiscount: 0,
  orderDiscount: 0,
  promotionAmount: 0,
  cashAmount: 0,
  wechatAmount: 0,
  alipayAmount: 0,
  bankAmount: 0,
  medicalAmount: 0
})

// 药品明细
const drugItems = ref([])

// 客户列表
const customerList = ref([])

// 药品列表（库存药品）
const drugList = ref([])

// 用户列表（销售员）
const userList = ref([])

// 搜索关键词
const searchKeyword = ref('')

// 快速药品列表（搜索结果）
const quickDrugList = ref([])

// 选中的客户
const selectedCustomer = ref(null)

// 对话框状态
const saveDialogVisible = ref(false)

// 保存状态
const saving = ref(false)

// 计算总数量
const totalQuantity = computed(() => {
  return drugItems.value.reduce((sum, item) => sum + (item.quantity || 0), 0)
})

// 计算原价总额
const originalTotalAmount = computed(() => {
  return drugItems.value.reduce((sum, item) => {
    const quantity = item.quantity || 0
    const salePrice = item.salePrice || 0
    return sum + quantity * salePrice
  }, 0)
})

// 计算折扣总额
const totalDiscountAmount = computed(() => {
  return drugItems.value.reduce((sum, item) => {
    const quantity = item.quantity || 0
    const salePrice = item.salePrice || 0
    const discount = item.discount || 0
    return sum + quantity * salePrice * discount / 100
  }, 0)
})

// 计算折扣后总额
const discountedTotalAmount = computed(() => {
  return originalTotalAmount.value - totalDiscountAmount.value
})

// 计算会员折扣金额
const memberDiscountAmount = computed(() => {
  if (!selectedCustomer.value || !selectedCustomer.value.discount) {
    return 0
  }
  const customerDiscount = (100 - selectedCustomer.value.discount * 10) / 100
  return discountedTotalAmount.value * customerDiscount
})

// 计算整单折扣金额
const orderDiscountAmount = computed(() => {
  return discountedTotalAmount.value * (formData.orderDiscount / 100)
})

// 计算实际应收
const actualReceivable = computed(() => {
  let amount = discountedTotalAmount.value
  
  // 应用会员折扣
  if (selectedCustomer.value?.discount) {
    const customerDiscount = selectedCustomer.value.discount / 10 // 8折转为0.8
    amount = amount * customerDiscount
  }
  
  // 应用整单折扣
  amount = amount * (1 - formData.orderDiscount / 100)
  
  // 减去优惠金额
  amount = amount - formData.promotionAmount
  
  // 确保不小于0
  return Math.max(0, amount)
})

// 检查是否有库存警告
const hasStockWarning = computed(() => {
  return drugItems.value.some(item => item.stockWarning)
})

// 初始化数据
onMounted(() => {
  generateOrderNo()
  loadCustomers()
  loadDrugs()
  loadUsers()
  
  // 设置默认值
  const today = new Date()
  formData.saleDate = today.toISOString().split('T')[0]
  
  // 添加一个空的药品行
  addDrugItem()
})

// 生成销售单号
const generateOrderNo = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = String(Math.floor(Math.random() * 10000)).padStart(4, '0')
  formData.orderNo = `SO${year}${month}${day}${random}`
}

// 加载客户数据
const loadCustomers = async () => {
  try {
    const res = await customerApi.getCustomerList()
    const data = res.data ?? res
    const list = Array.isArray(data) ? data : (data.items ?? data.list ?? [])
    customerList.value = list
  } catch (err) {
    console.error('加载客户列表失败', err)
    customerList.value = []
  }
}

// 加载药品数据（库存药品）
const loadDrugs = async () => {
  try {
    const res = await drugApi.getDrugList()
    const data = res.data ?? res
    const list = Array.isArray(data) ? data : (data.items ?? data.list ?? data.data ?? [])
    // 映射为本页面需要的字段
    drugList.value = list.map(d => ({
      id: d.id,
      name: d.name || d.drugName || d.title,
      specification: d.specification || d.spec || '',
      manufacturer: d.manufacturer || d.mfr || '',
      unit: d.unit || '盒',
      stock: d.stock ?? d.quantity ?? 0,
      isPrescription: d.isPrescription ?? d.prescription ?? false,
      purchasePrice: d.purchasePrice ?? d.cost ?? 0,
      salePrice: d.salePrice ?? d.price ?? 0,
      barcode: d.barcode ?? d.barCode ?? ''
    }))
  } catch (err) {
    console.error('加载药品列表失败', err)
    drugList.value = []
  }
}

// 加载用户列表（销售员）
const loadUsers = async () => {
  try {
    const res = await authApi.getUsers()
    const data = res.data ?? res
    userList.value = Array.isArray(data) ? data : []
    
    // 如果有用户列表，设置默认销售员为第一个用户
    if (userList.value.length > 0 && !formData.salesman) {
      formData.salesman = userList.value[0]
    }
  } catch (err) {
    console.error('加载用户列表失败', err)
    ElMessage.error(err.message || '加载用户列表失败')
    userList.value = []
  }
}

// 搜索药品
const searchDrugs = () => {
  if (!searchKeyword.value.trim()) {
    quickDrugList.value = []
    return
  }
  
  const keyword = searchKeyword.value.toLowerCase()
  quickDrugList.value = drugList.value.filter(drug => {
    return drug.name.toLowerCase().includes(keyword) ||
           drug.specification.toLowerCase().includes(keyword) ||
           (drug.barcode && drug.barcode.includes(keyword))
  }).slice(0, 6) // 最多显示6个结果
}

// 快速添加药品
const quickAddDrug = (drug) => {
  // 检查是否已经添加过该药品
  const existingIndex = drugItems.value.findIndex(item => item.drugId === drug.id)
  
  if (existingIndex >= 0) {
    // 如果已存在，增加数量
    const item = drugItems.value[existingIndex]
    item.quantity = (item.quantity || 1) + 1
    updateItem(existingIndex)
    ElMessage.success(`已增加 ${drug.name} 数量`)
  } else {
    // 添加新药品项
    addDrugItem()
    const lastIndex = drugItems.value.length - 1
    handleDrugChange(drug.id, lastIndex)
  }
  
  // 清空搜索
  searchKeyword.value = ''
  quickDrugList.value = []
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
    expiryDate: '',
    stock: 0,
    maxQuantity: 0,
    stockWarning: false,
    quantity: 1,
    salePrice: 0,
    discount: 0,
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
    item.drugName = drug.name
    item.specification = drug.specification
    item.manufacturer = drug.manufacturer
    item.unit = drug.unit
    item.isPrescription = drug.isPrescription
    item.salePrice = drug.salePrice
    item.stock = drug.stock
    item.maxQuantity = drug.stock
    
    // 设置批次号（模拟，实际应从库存批次中选择）
    item.batchNo = `BATCH${String(Math.floor(Math.random() * 10000)).padStart(4, '0')}`
    
    // 设置有效期（模拟）
    const today = new Date()
    const expiryDate = new Date(today.getTime() + 365 * 24 * 60 * 60 * 1000) // 1年后
    item.expiryDate = expiryDate.toISOString().split('T')[0]
    
    // 检查库存警告
    item.stockWarning = item.quantity > item.stock
    
    updateItem(index)
  }
}

// 客户选择变化
const handleCustomerChange = (customerId) => {
  if (customerId === 'guest') {
    selectedCustomer.value = null
    formData.memberDiscount = 0
    return
  }
  
  const customer = customerList.value.find(c => c.id === customerId)
  selectedCustomer.value = customer
  
  if (customer) {
    // 应用会员折扣
    formData.memberDiscount = (10 - customer.discount) * 10 // 8.5折转为15%折扣
    ElMessage.info(`已选择客户：${customer.name}，享受${customer.discount}折优惠`)
  }
}

// 计算折扣后单价
const calculateDiscountedPrice = (item) => {
  const salePrice = item.salePrice || 0
  const discount = item.discount || 0
  return salePrice * (1 - discount / 100)
}

// 计算单项金额
const calculateItemAmount = (item) => {
  const quantity = item.quantity || 0
  const discountedPrice = calculateDiscountedPrice(item)
  return quantity * discountedPrice
}

// 更新项目
const updateItem = (index) => {
  const item = drugItems.value[index]
  
  // 检查库存警告
  if (item.stock && item.quantity) {
    item.stockWarning = item.quantity > item.stock
    if (item.stockWarning) {
      item.maxQuantity = item.stock
    }
  }
  
  // 触发计算更新
  calculateTotal()
}

// 计算总计
const calculateTotal = () => {
  // 计算属性会自动更新，这里只是触发计算
  return actualReceivable.value
}

// 保存销售单
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
    // 构建请求体（后端只接受 customerId 和 items）
    // 处理 customerId：如果是 "guest" 则设为 null，否则转换为数字
    let customerId = null
    if (formData.customerId && formData.customerId !== 'guest') {
      customerId = typeof formData.customerId === 'number' ? formData.customerId : parseInt(formData.customerId)
      if (isNaN(customerId)) {
        customerId = null
      }
    }
    
    const payload = {
      customerId: customerId,
      salesman: formData.salesman ? formData.salesman.trim() : null,
      items: drugItems.value.map(it => ({
        drugId: typeof it.drugId === 'number' ? it.drugId : parseInt(it.drugId),
        quantity: typeof it.quantity === 'number' ? it.quantity : parseInt(it.quantity),
        salePrice: typeof it.salePrice === 'number' ? it.salePrice : parseFloat(it.salePrice)
      }))
    }

    // 验证数据
    if (!payload.items || payload.items.length === 0) {
      ElMessage.error('请至少添加一个药品')
      return
    }

    // 验证每个商品的数据
    for (const item of payload.items) {
      if (!item.drugId || item.drugId <= 0) {
        ElMessage.error('药品ID无效')
        return
      }
      if (!item.quantity || item.quantity <= 0) {
        ElMessage.error('销售数量必须大于0')
        return
      }
      if (!item.salePrice || item.salePrice <= 0) {
        ElMessage.error('销售单价必须大于0')
        return
      }
    }

    // 尝试调用后端
    const res = await saleApi.addSale(payload)

    ElMessage.success('销售单保存成功！')
    saveDialogVisible.value = false
    // 跳转到销售历史页面
    router.push('/sale/history')
  } catch (err) {
    console.error('保存销售单失败', err)
    // 提取更详细的错误信息
    const errorMessage = err.response?.data?.message || err.message || '保存销售单失败'
    ElMessage.error(errorMessage)
  } finally {
    saving.value = false
  }
}

// 验证表单
const validateForm = () => {
  // 验证销售日期
  if (!formData.saleDate) {
    ElMessage.error('请选择销售日期')
    return false
  }
  
  // 验证销售员
  if (!formData.salesman || !formData.salesman.trim()) {
    ElMessage.error('请选择销售员')
    return false
  }
  
  // 验证药品明细
  if (drugItems.value.length === 0) {
    ElMessage.error('请至少添加一个药品项')
    return false
  }
  
  // 验证每个药品项
  for (const item of drugItems.value) {
    if (!item.drugId) {
      ElMessage.error('请为所有药品项选择药品')
      return false
    }
    
    if (!item.quantity || item.quantity <= 0) {
      ElMessage.error('请输入有效的销售数量')
      return false
    }
    
    if (!item.salePrice || item.salePrice <= 0) {
      ElMessage.error('请输入有效的销售单价')
      return false
    }
    
    // 检查处方药是否填写了处方信息
    if (item.isPrescription && formData.saleType === 'prescription') {
      if (!formData.prescriptionNo.trim()) {
        ElMessage.error('处方药销售必须填写处方编号')
        return false
      }
      if (!formData.doctorName.trim()) {
        ElMessage.error('处方药销售必须填写医生姓名')
        return false
      }
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
      customerId: 'guest',
      saleDate: new Date().toISOString().split('T')[0],
      salesman: userList.value.length > 0 ? userList.value[0] : '',
      paymentMethod: 'cash',
      saleType: 'retail',
      prescriptionNo: '',
      doctorName: '',
      remark: '',
      memberDiscount: 0,
      orderDiscount: 0,
      promotionAmount: 0,
      cashAmount: 0,
      wechatAmount: 0,
      alipayAmount: 0,
      bankAmount: 0,
      medicalAmount: 0
    })
    
    // 重新生成单号
    generateOrderNo()
    
    // 清空药品明细
    drugItems.value = []
    addDrugItem()
    
    // 清空客户选择
    selectedCustomer.value = null
    
    ElMessage.success('表单已重置')
  })
}

// 从处方导入
const batchAddFromPrescription = () => {
  ElMessage.info('处方导入功能开发中...')
}

// 打印订单
const printOrder = async () => {
  if (drugItems.value.length === 0) {
    ElMessage.warning('没有药品可以打印')
    return
  }

  try {
    const res = await saleApi.printOrder(formData.orderNo || formData.orderId || formData.id)
    const data = res.data ?? res
    if (data && (data.url || data.downloadUrl || data.link)) {
      window.open(data.url ?? data.downloadUrl ?? data.link, '_blank')
      ElMessage.success('正在下载打印内容')
    } else {
      throw new Error('后端未返回打印链接')
    }
  } catch (err) {
    console.warn('打印接口不可用，回退至本地打印或预览', err)
    const printContent = `销售单 ${formData.orderNo} - 打印预览（后端不可用）`
    console.log(printContent)
    ElMessage.info('打印功能暂不可用，已提供本地预览')
  }
}

// 查看历史
const showOrderHistory = () => {
  router.push('/sale/history')
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toISOString().split('T')[0]
}

// 获取会员等级标签
const getMemberLevelTag = (level) => {
  if (!level) return 'info'
  const tagMap = {
    bronze: 'info',
    silver: 'info',
    gold: 'warning',
    platinum: 'success',
    diamond: 'danger'
  }
  return tagMap[level] || 'info'
}

// 获取会员等级文本
const getMemberLevelText = (level) => {
  const textMap = {
    bronze: '青铜会员',
    silver: '白银会员',
    gold: '黄金会员',
    platinum: '白金会员',
    diamond: '钻石会员'
  }
  return textMap[level] || level
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

// 表格行样式
const tableRowClassName = ({ rowIndex }) => {
  if (rowIndex % 2 === 0) {
    return 'row-even'
  }
  return 'row-odd'
}
</script>

<style scoped>
.sale-order-container {
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

.drug-search {
  margin-bottom: 20px;
}

.quick-drugs {
  margin-bottom: 20px;
}

.quick-drug-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 10px;
}

.quick-drug-item {
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.quick-drug-item:hover {
  border-color: #409EFF;
  background-color: #f5f7fa;
}

.quick-drug-item .drug-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.quick-drug-item .drug-spec {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.quick-drug-item .drug-price {
  color: #f56c6c;
  font-weight: bold;
}

.quick-drug-item .drug-stock {
  font-size: 12px;
  color: #67C23A;
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

.drug-option .drug-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.drug-option .drug-details {
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

.stock-info {
  font-size: 12px;
  color: #409EFF;
  margin-top: 4px;
}

.stock-warning {
  font-size: 12px;
  color: #f56c6c;
  display: flex;
  align-items: center;
  gap: 4px;
}

.amount-cell {
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

.discount-amount {
  color: #67C23A;
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

.unit-suffix {
  margin-left: 8px;
  color: #666;
}

.payment-details {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.payment-details h4 {
  margin: 0 0 15px;
  color: #333;
  font-size: 16px;
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

.inventory-check {
  margin-top: 15px;
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
  
  .quick-drug-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .summary-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}
</style>