<!-- src/views/sale/components/ReturnItemSelector.vue -->
<template>
  <div class="return-item-selector-container">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-input
        v-model="searchKeyword"
        placeholder="输入药品名称搜索"
        clearable
        @keyup.enter="handleSearch"
        style="width: 300px; margin-bottom: 15px;"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch" />
        </template>
      </el-input>
    </div>

    <!-- 商品列表 -->
    <div class="items-list">
      <el-table
        ref="itemsTableRef"
        :data="filteredItems"
        style="width: 100%"
        height="400"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
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
        <el-table-column label="退货状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.returnableQuantity > 0" type="success" size="small">可退</el-tag>
            <el-tag v-else type="info" size="small">不可退</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 操作按钮 -->
    <div class="selector-actions">
      <div class="selected-info">
        已选择 {{ selectedItems.length }} 个商品
      </div>
      <div class="action-buttons">
        <el-button @click="handleCancel">
          取消
        </el-button>
        <el-button type="primary" @click="handleConfirm" :disabled="selectedItems.length === 0">
          确认选择
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const props = defineProps({
  items: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['confirm', 'cancel'])

// 搜索关键词
const searchKeyword = ref('')

// 表格引用
const itemsTableRef = ref(null)

// 选中的商品
const selectedItems = ref([])

// 过滤后的商品列表
const filteredItems = computed(() => {
  if (!searchKeyword.value.trim()) {
    return props.items
  }
  
  const keyword = searchKeyword.value.toLowerCase()
  return props.items.filter(item => {
    return item.drugName.toLowerCase().includes(keyword) ||
           item.specification.toLowerCase().includes(keyword) ||
           item.manufacturer.toLowerCase().includes(keyword) ||
           item.batchNo.includes(keyword)
  })
})

// 处理搜索
const handleSearch = () => {
  // 搜索逻辑已在 computed 中实现
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedItems.value = selection
}

// 确认选择
const handleConfirm = () => {
  const selectedIds = selectedItems.value.map(item => item.id)
  emit('confirm', selectedIds)
  ElMessage.success(`已选择 ${selectedItems.value.length} 个商品`)
}

// 取消
const handleCancel = () => {
  emit('cancel')
}

// 初始化：默认选中所有可退货的商品
import { nextTick } from 'vue'

onMounted(async () => {
  // 使用 nextTick 确保表格渲染完成再操作
  await nextTick()
  if (itemsTableRef.value) {
    const selectableItems = props.items.filter(item => item.returnableQuantity > 0)
    selectableItems.forEach(item => {
      itemsTableRef.value.toggleRowSelection(item, true)
    })
    selectedItems.value = selectableItems
  }
})
</script>

<style scoped>
.return-item-selector-container {
  padding: 10px 0;
}

.search-section {
  margin-bottom: 15px;
}

.items-list {
  margin-bottom: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.selector-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.selected-info {
  font-size: 14px;
  color: #666;
}

.action-buttons {
  display: flex;
  gap: 10px;
}
</style>