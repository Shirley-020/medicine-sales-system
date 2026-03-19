<!-- src/views/NotFound.vue -->
<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1 class="error-code">404</h1>
      <h2 class="error-title">页面未找到</h2>
      <p class="error-description">
        抱歉，您访问的页面不存在或已被移除
      </p>
      <div class="action-buttons">
        <el-button type="primary" @click="goHome" size="large">
          返回首页
        </el-button>
        <el-button @click="goBack" size="large">
          返回上一页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.not-found-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.not-found-content {
  text-align: center;
  padding: 60px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.error-code {
  font-size: 120px;
  color: #409EFF;
  margin: 0;
  line-height: 1;
}

.error-title {
  font-size: 32px;
  color: #333;
  margin: 20px 0 10px;
}

.error-description {
  font-size: 16px;
  color: #666;
  margin-bottom: 40px;
  max-width: 500px;
}

.action-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
}
</style>