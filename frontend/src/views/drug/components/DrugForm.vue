<!-- src/views/drug/components/DrugForm.vue -->
<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="formRules"
    label-width="120px"
    :disabled="mode === 'view'"
  >
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="药品编码" prop="drugCode">
          <el-input
            v-model="formData.drugCode"
            placeholder="系统自动生成"
            disabled
          />
        </el-form-item>
      </el-col>
      
      <el-col :span="12">
        <el-form-item label="药品名称" prop="drugName">
          <el-input
            v-model="formData.drugName"
            placeholder="请输入药品名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
      </el-col>
      
      <el-col :span="12">
        <el-form-item label="通用名" prop="genericName">
          <el-input
            v-model="formData.genericName"
            placeholder="请输入药品通用名"
          />
        </el-form-item>
      </el-col>
      
      <el-col :span="12">
        <el-form-item label="药品类型" prop="drugType">
          <el-select
            v-model="formData.drugType"
            placeholder="请选择药品类型"
            style="width: 100%"
          >
            <el-option label="处方药" value="prescription" />
            <el-option label="非处方药" value="otc" />
            <el-option label="中药" value="traditional" />
            <el-option label="西药" value="western" />
            <el-option label="医疗器械" value="equipment" />
          </el-select>
        </el-form-item>
      </el-col>
      
      <el-col :span="12">
        <el-form-item label="规格" prop="specification">
          <el-input
            v-model="formData.specification"
            placeholder="如：10mg*20片"
          />
        </el-form-item>
      </el-col>
      
      <el-col :span="12">
        <el-form-item label="单位" prop="unit">
          <el-select
            v-model="formData.unit"
            placeholder="请选择单位"
            style="width: 100%"
          >
            <el-option label="盒" value="box" />
            <el-option label="瓶" value="bottle" />
            <el-option label="支" value="piece" />
            <el-option label="袋" value="bag" />
            <el-option label="粒" value="grain" />
            <el-option label="片" value="tablet" />
          </el-select>
        </el-form-item>
      </el-col>
      
      <el-col :span="12">
        <el-form-item label="生产厂家" prop="manufacturer">
          <el-input
            v-model="formData.manufacturer"
            placeholder="请输入生产厂家"
          />
        </el-form-item>
      </el-col>
      
      <el-col :span="12">
        <el-form-item label="库存数量" prop="stock">
          <el-input-number
            v-model="formData.stock"
            :min="0"
            :max="99999"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
      
      <el-col :span="12">
        <el-form-item label="最低库存" prop="minStock">
          <el-input-number
            v-model="formData.minStock"
            :min="0"
            :max="9999"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
      
      <el-col :span="12">
        <el-form-item label="单价(元)" prop="price">
          <el-input-number
            v-model="formData.price"
            :min="0"
            :max="99999"
            :precision="2"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
      
      <el-col :span="12">
        <el-form-item label="有效期至" prop="expiryDate">
          <el-date-picker
            v-model="formData.expiryDate"
            type="date"
            placeholder="请选择有效期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-col>
      
      <el-col :span="12">
        <el-form-item label="是否处方药" prop="isPrescription">
          <el-switch
            v-model="formData.isPrescription"
            active-text="是"
            inactive-text="否"
          />
        </el-form-item>
      </el-col>
      
      <el-col :span="12">
        <el-form-item label="药品状态" prop="status">
          <el-select
            v-model="formData.status"
            placeholder="请选择状态"
            style="width: 100%"
          >
            <el-option label="正常" value="normal" />
            <el-option label="缺货" value="out_of_stock" />
            <el-option label="停售" value="discontinued" />
          </el-select>
        </el-form-item>
      </el-col>
      
      <el-col :span="24">
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-col>
    </el-row>
    
    <div class="form-footer" v-if="mode !== 'view'">
      <el-button type="primary" @click="submitForm" :loading="submitting">
        {{ mode === 'add' ? '创建' : '保存' }}
      </el-button>
      <el-button @click="resetForm">重置</el-button>
      <el-button @click="emit('cancel')">取消</el-button>
    </div>
  </el-form>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import drugApi from '@/api/drug'

const props = defineProps({
  drugData: {
    type: Object,
    default: null
  },
  mode: {
    type: String,
    default: 'add' // 'add' | 'edit' | 'view'
  }
})

const emit = defineEmits(['success', 'cancel'])

// 表单引用
const formRef = ref(null)

// 表单数据
const formData = reactive({
  drugCode: '',
  drugName: '',
  genericName: '',
  drugType: 'western',
  specification: '',
  unit: 'box',
  manufacturer: '',
  stock: 0,
  minStock: 50,
  price: 0,
  expiryDate: '',
  isPrescription: false,
  status: 'normal',
  remark: ''
})

// 表单验证规则
const formRules = {
  drugName: [
    { required: true, message: '请输入药品名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  drugType: [
    { required: true, message: '请选择药品类型', trigger: 'change' }
  ],
  specification: [
    { required: true, message: '请输入药品规格', trigger: 'blur' }
  ],
  manufacturer: [
    { required: true, message: '请输入生产厂家', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存不能小于0', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入单价', trigger: 'blur' },
    { type: 'number', min: 0, message: '单价不能小于0', trigger: 'blur' }
  ],
  expiryDate: [
    { required: true, message: '请选择有效期', trigger: 'change' }
  ]
}

// 提交状态
const submitting = ref(false)

// 初始化表单数据
const initFormData = () => {
  if (props.drugData) {
    Object.keys(formData).forEach(key => {
      if (props.drugData[key] !== undefined) {
        formData[key] = props.drugData[key]
      }
    })
  } else {
    // 新增模式，生成药品编码
    const code = 'DRUG' + String(Date.now()).slice(-6)
    formData.drugCode = code
  }
}

// 检查是否有未保存的更改
const hasUnsavedChanges = () => {
  if (!props.drugData) {
    // 新增模式，检查是否有填写内容
    return Object.values(formData).some(value => {
      if (Array.isArray(value)) {
        return value.length > 0
      } else if (typeof value === 'object' && value !== null) {
        return Object.keys(value).length > 0
      }
      return !!value
    })
  }
  
  // 编辑模式，检查是否有修改
  return Object.keys(formData).some(key => {
    const originalValue = props.drugData[key]
    const currentValue = formData[key]
    return originalValue !== currentValue
  })
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    // 验证表单
    await formRef.value.validate()
    
    submitting.value = true

    try {
      if (props.mode === 'add') {
        // 适配后端可能只需要一部分字段
        const payload = {
          name: formData.drugName,
          price: formData.price,
          // 其它字段尽量一同发送以保持完整
          drugCode: formData.drugCode,
          genericName: formData.genericName,
          drugType: formData.drugType,
          specification: formData.specification,
          unit: formData.unit,
          manufacturer: formData.manufacturer,
          stock: formData.stock,
          minStock: formData.minStock,
          expiryDate: formData.expiryDate,
          isPrescription: formData.isPrescription,
          status: formData.status,
          remark: formData.remark
        }
        await drugApi.addDrug(payload)
        ElMessage.success('药品添加成功')
      } else {
        const payload = { id: props.drugData.id, ...formData }
        await drugApi.updateDrug(payload)
        ElMessage.success('药品修改成功')
      }

      emit('success')
    } catch (err) {
      console.error('保存药品失败', err)
      ElMessage.error(err.message || '保存药品失败')
    } finally {
      submitting.value = false
    }
  } catch (error) {
    console.log('表单验证失败:', error)
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  if (!formRef.value) return
  
  if (props.drugData) {
    // 重置为原始数据
    initFormData()
  } else {
    // 清空表单
    formRef.value.resetFields()
  }
}

// 取消编辑
const cancelForm = () => {
  emit('cancel')
}

// 监听药品数据变化
watch(() => props.drugData, () => {
  initFormData()
}, { immediate: true })

// 暴露方法给父组件
defineExpose({
  hasUnsavedChanges
})
</script>

<style scoped>
.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}
</style>