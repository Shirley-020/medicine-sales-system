<!-- src/views/Login.vue -->
<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="login-background">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
      <div class="decoration-circle circle-4"></div>
    </div>
    
    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- 左侧品牌信息 -->
      <div class="brand-section">
        <div class="brand-logo">
          <el-icon size="64" color="#409EFF">
            <FirstAidKit />
          </el-icon>
        </div>
        <h1 class="brand-title">医药销售管理系统</h1>
        <p class="brand-subtitle">Medicine Sales Management System</p>
        
        <div class="features">
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>药品信息管理</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>进货销售统计</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>库存智能预警</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>财务数据分析</span>
          </div>
        </div>
      </div>
      
      <!-- 右侧登录表单 -->
      <div class="form-section">
        <div class="form-header">
          <h2>用户登录</h2>
          <p>请输入您的账号和密码</p>
        </div>
        
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              :type="passwordVisible ? 'text' : 'password'"
              clearable
            >
              <template #suffix>
                <el-icon
                  class="password-eye"
                  @click="passwordVisible = !passwordVisible"
                >
                  <View v-if="passwordVisible" />
                  <Hide v-else />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="remember" class="remember-item">
            <el-checkbox v-model="loginForm.remember">
              记住密码
            </el-checkbox>
            <el-link type="primary" :underline="false" class="forgot-password">
              忘记密码?
            </el-link>
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleLogin"
              class="login-button"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="form-footer">
          <el-divider>其他登录方式</el-divider>
          <div class="social-login">
            <el-tooltip content="微信登录" placement="top">
              <div class="social-icon wechat">
                <el-icon><ChatDotRound /></el-icon>
              </div>
            </el-tooltip>
            <el-tooltip content="手机验证码登录" placement="top">
              <div class="social-icon phone">
                <el-icon><Iphone /></el-icon>
              </div>
            </el-tooltip>
            <el-tooltip content="指纹登录" placement="top">
              <div class="social-icon fingerprint">
                <el-icon><Pointer /></el-icon>
              </div>
            </el-tooltip>
          </div>
          
          <div class="register-tip">
            还没有账号? <el-link type="primary" :underline="false">立即注册</el-link>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 页脚 -->
    <div class="login-footer">
      <p>© 2024 医药销售管理系统 | 版本 v1.0.0</p>
      <p>仅供课程设计使用，非商业用途</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  FirstAidKit, User, Lock, View, Hide,
  Check, ChatDotRound, Iphone, Pointer
} from '@element-plus/icons-vue'
import auth from '@/api/auth'

const router = useRouter()

// 表单数据
const loginForm = reactive({
  username: 'admin',
  password: '123456',
  remember: false
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 响应式数据
const loading = ref(false)
const passwordVisible = ref(false)
const loginFormRef = ref()

// 登录方法
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    // 验证表单
    await loginFormRef.value.validate()
    
    loading.value = true
    
    // 使用真实登录接口
    try {
      const resp = await auth.login({
        username: loginForm.username,
        password: loginForm.password
      })

      // 兼容返回结构（token 可能在顶层或 data 中）
      const token = resp.token ?? resp.data?.token
      if (!token) throw new Error(resp.message || '未获取到 token')

      localStorage.setItem('token', token)
      localStorage.setItem('userInfo', JSON.stringify({
        username: resp.username ?? loginForm.username,
        role: resp.role ?? 'admin'
      }))

      if (loginForm.remember) {
        localStorage.setItem('rememberedUser', loginForm.username)
      }

      ElMessage.success(resp.message || '登录成功！')
      router.push('/')
    } catch (err) {
      console.error('登录失败', err)
      ElMessage.error(err.message || '登录失败')
    }
  } catch (error) {
    console.log('表单验证失败:', error)
  } finally {
    setTimeout(() => {
      loading.value = false
    }, 800)
  }
}

// 页面加载时检查是否有记住的用户名
onMounted(() => {
  const rememberedUser = localStorage.getItem('rememberedUser')
  if (rememberedUser) {
    loginForm.username = rememberedUser
    loginForm.remember = true
  }
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  right: -150px;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: 100px;
  left: -100px;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 30%;
  left: 10%;
}

.circle-4 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  right: 10%;
}

.login-card {
  width: 900px;
  height: 550px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  overflow: hidden;
  z-index: 1;
}

.brand-section {
  flex: 1;
  background: linear-gradient(135deg, #409EFF 0%, #36CBCB 100%);
  color: white;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.brand-logo {
  margin-bottom: 20px;
}

.brand-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 10px;
}

.brand-subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 40px;
}

.features {
  width: 100%;
  margin-top: 30px;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  font-size: 14px;
}

.feature-item .el-icon {
  margin-right: 10px;
}

.form-section {
  flex: 1;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  margin-bottom: 40px;
  text-align: center;
}

.form-header h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
}

.form-header p {
  color: #666;
  font-size: 14px;
}

.login-form {
  width: 100%;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
}

:deep(.el-input__inner) {
  height: 48px;
}

.password-eye {
  cursor: pointer;
  color: #c0c4cc;
}

.password-eye:hover {
  color: #409EFF;
}

.remember-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.forgot-password {
  font-size: 14px;
}

.login-button {
  width: 100%;
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  margin-top: 10px;
}

.form-footer {
  margin-top: 30px;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin: 20px 0 30px;
}

.social-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 20px;
  color: white;
}

.social-icon.wechat {
  background: #07C160;
}

.social-icon.phone {
  background: #409EFF;
}

.social-icon.fingerprint {
  background: #F56C6C;
}

.social-icon:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.register-tip {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.login-footer {
  position: absolute;
  bottom: 20px;
  width: 100%;
  text-align: center;
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
}

.login-footer p {
  margin: 5px 0;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .login-card {
    width: 90%;
    height: auto;
    flex-direction: column;
  }
  
  .brand-section {
    padding: 30px 20px;
  }
  
  .form-section {
    padding: 30px 20px;
  }
}

@media (max-width: 576px) {
  .login-card {
    width: 95%;
  }
  
  .brand-title {
    font-size: 24px;
  }
  
  .form-header h2 {
    font-size: 24px;
  }
  
  .social-login {
    gap: 20px;
  }
  
  .social-icon {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
}
</style>