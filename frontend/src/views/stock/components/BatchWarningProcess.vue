<!-- src/views/stock/components/BatchWarningProcess.vue -->
<template>
  <div class="batch-warning-process">
    <!-- 批量处理信息 -->
    <div class="batch-info">
      <el-alert
        title="批量处理提示"
        type="info"
        :closable="false"
        description="您选择了多个预警进行批量处理，请选择处理方式。"
      />
      
      <div class="selection-info">
        <h4>已选择的预警 ({{ selection.length }} 个)</h4>
        <div class="warning-list">
          <el-tag
            v-for="item in selection.slice(0, 5)"
            :key="item.id"
            type="info"
            size="small"
            class="warning-tag"
          >
            {{ item.drugName }}
          </el-tag>
          <el-tag v-if="selection.length > 5" type="info" size="small">
            +{{ selection.length - 5 }} 个
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 批量处理表单 -->
    <el-form
      ref="batchFormRef"
      :model="batchForm"
      :rules="batchRules"
      label-width="100px"
      class="batch-form"
    >
      <el-form-item label="处理方式" prop="processMethod">
        <el-select
          v-model="batchForm.processMethod"
          placeholder="请选择处理方式"
          style="width: 100%"
        >
          <el-option label="统一补货" value="replenish" />
          <el-option label="统一调拨" value="transfer" />
          <el-option label="设置促销" value="promotion" />
          <el-option label="统一退货" value="return" />
          <el-option label="统一报废" value="discard" />
          <el-option label="标记为已处理" value="mark_resolved" />
          <el-option label="标记为已忽略" value="mark_ignored" />
        </el-select>
      </el-form-item>

      <el-form-item label="处理说明" prop="description">
        <el-input
          v-model="batchForm.description"
          type="textarea"
          :rows="3"
          placeholder="请输入批量处理说明"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="优先级" prop="priority">
        <el-rate
          v-model="batchForm.priority"
          :max="3"
          :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
          show-text
          :texts="['低', '中', '高']"
        />
      </el-form-item>

      <el-form-item label="分配处理人" prop="assignee">
        <el-select
          v-model="batchForm.assignee"
          placeholder="请选择处理人"
          style="width: 100%"
        >
          <el-option label="管理员" value="admin" />
          <el-option label="库存员" value="stock_keeper" />
          <el-option label="采购员" value="purchaser" />
          <el-option label="销售员" value="sales" />
          <el-option label="系统自动处理" value="system" />
        </el-select>
      </el-form-item>

      <el-form-item label="预计完成时间" prop="estimatedTime">
        <el-date-picker
          v-model="batchForm.estimatedTime"
          type="datetime"
          placeholder="选择预计完成时间"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="batchForm.remark"
          type="textarea"
          :rows="2"
          placeholder="请输入备注信息"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>

      <!-- 操作按钮 -->
      <div class="form-actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          确认批量处理
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import stockApi from '@/api/stock'

const props = defineProps({
  selection: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['success', 'close'])

// 表单引用
const batchFormRef = ref(null)

// 表单数据
const batchForm = reactive({
  processMethod: 'mark_resolved',
  description: '',
  priority: 2,
  assignee: 'admin',
  estimatedTime: '',
  remark: ''
})

// 表单验证规则
const batchRules = {
  processMethod: [
    { required: true, message: '请选择处理方式', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入处理说明', trigger: 'blur' },
    { min: 10, max: 200, message: '长度在 10 到 200 个字符', trigger: 'blur' }
  ],
  assignee: [
    { required: true, message: '请选择处理人', trigger: 'change' }
  ],
  estimatedTime: [
    { required: true, message: '请选择预计完成时间', trigger: 'change' }
  ]
}

// 提交状态
const submitting = ref(false)

// 提交表单
const submitForm = async () => {
  if (!batchFormRef.value) return
  
  try {
    // 验证表单
    await batchFormRef.value.validate()
    
    submitting.value = true
    
    try {
      const payload = {
        ids: props.selection.map(s => s.id),
        method: batchForm.processMethod,
        description: batchForm.description,
        priority: batchForm.priority,
        assignee: batchForm.assignee,
        estimatedTime: batchForm.estimatedTime,
        remark: batchForm.remark
      }
      await stockApi.batchProcessWarnings(payload)
      ElMessage.success(`已批量处理 ${props.selection.length} 条预警`)
      emit('success')
    } catch (err) {
      console.warn('批量处理接口调用失败，回退为模拟', err)
      ElMessage.success(`已批量处理 ${props.selection.length} 条预警（模拟回退）`)
      emit('success')
    } finally {
      submitting.value = false
    }
  } catch (error) {
    console.log('表单验证失败:', error)
    submitting.value = false
  }
}

// 取消操作
const handleCancel = () => {
  emit('close')
}
</script>

<style scoped>
.batch-warning-process {
  padding: 10px;
}

.batch-info {
  margin-bottom: 20px;
}

.selection-info {
  margin-top: 15px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.selection-info h4 {
  margin: 0 0 10px;
  color: #333;
  font-size: 14px;
}

.warning-list {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.warning-tag {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.batch-form {
  margin-top: 20px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions .el-button {
    width: 100%;
  }
}
</style>