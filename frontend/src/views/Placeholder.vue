<!-- src/views/Placeholder.vue -->
<template>
  <div class="placeholder-page">
    <h1 style="color: #409EFF; font-size: 36px; margin-bottom: 20px;">
      {{ title }}
    </h1>
    <p style="font-size: 18px; color: #333; margin-bottom: 30px;">
      {{ description }}
    </p>
    <div style="background: #f5f7fa; padding: 20px; border-radius: 8px; text-align: left; display: inline-block;">
      <h3 style="color: #666; margin-bottom: 15px;">页面功能：</h3>
      <ul style="list-style-type: none; padding-left: 0;">
        <li v-for="(item, index) in features" :key="index" 
            style="margin-bottom: 8px; padding: 8px 12px; background: white; border-radius: 4px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
          📌 {{ item }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  title: {
    type: String,
    default: '🩺 医药销售管理系统'
  },
  description: {
    type: String,
    default: '这是占位页面，实际页面开发中...'
  },
  features: {
    type: Array,
    default: () => [
      '药品信息管理',
      '进货管理',
      '销售管理',
      '库存管理',
      '财务统计'
    ]
  }
})
</script>

<style scoped>
.placeholder-page {
  padding: 40px;
  text-align: center;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}
</style>