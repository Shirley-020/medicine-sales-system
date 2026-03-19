<!-- src/views/layout/index.vue -->
<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside 
      :class="['sidebar', { 'sidebar-collapsed': isCollapsed }]"
      :width="isCollapsed ? '64px' : '240px'"
    >
      <!-- Logo区域 -->
      <div class="logo">
        <div class="logo-content">
          <el-icon size="28" color="#409EFF" v-if="isCollapsed">
            <FirstAidKit />
          </el-icon>
          <template v-else>
            <el-icon size="28" color="#409EFF">
              <FirstAidKit />
            </el-icon>
            <h2>医药销售管理系统</h2>
          </template>
        </div>
      </div>
      
      <!-- 菜单 -->
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        @select="handleMenuSelect"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        class="sidebar-menu"
      >
        <!-- 药品管理 -->
        <el-sub-menu index="drug">
          <template #title>
            <el-icon><FirstAidKit /></el-icon>
            <span>药品管理</span>
          </template>
          <el-menu-item index="/drug/list">
            <el-icon><List /></el-icon>
            <span>药品列表</span>
          </el-menu-item>
          <el-menu-item index="/drug/search">
            <el-icon><Search /></el-icon>
            <span>药品搜索</span>
          </el-menu-item>
        </el-sub-menu>
        
        <!-- 进货管理 -->
        <el-sub-menu index="purchase">
          <template #title>
            <el-icon><ShoppingCart /></el-icon>
            <span>进货管理</span>
          </template>
          <el-menu-item index="/purchase/order">
            <el-icon><DocumentAdd /></el-icon>
            <span>进货单</span>
          </el-menu-item>
          <el-menu-item index="/purchase/history">
            <el-icon><Document /></el-icon>
            <span>进货历史</span>
          </el-menu-item>
        </el-sub-menu>
        
        <!-- 销售管理 -->
        <el-sub-menu index="sale">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>销售管理</span>
          </template>
          <el-menu-item index="/sale/order">
            <el-icon><Document /></el-icon>
            <span>销售单</span>
          </el-menu-item>
          <el-menu-item index="/sale/return">
            <el-icon><Refresh /></el-icon>
            <span>销售退货</span>
          </el-menu-item>
          <el-menu-item index="/sale/daily">
            <el-icon><TrendCharts /></el-icon>
            <span>当日销售</span>
          </el-menu-item>
        </el-sub-menu>
        
        <!-- 库存管理 -->
        <el-sub-menu index="stock">
          <template #title>
            <el-icon><Box /></el-icon>
            <span>库存管理</span>
          </template>
          <el-menu-item index="/stock/query">
            <el-icon><Search /></el-icon>
            <span>库存查询</span>
          </el-menu-item>
          <el-menu-item index="/stock/warning">
            <el-icon><Warning /></el-icon>
            <span>库存预警</span>
          </el-menu-item>
        </el-sub-menu>
        
        <!-- 报表统计 -->
        <el-sub-menu index="report">
          <template #title>
            <el-icon><PieChart /></el-icon>
            <span>报表统计</span>
          </template>
          <el-menu-item index="/report/sale">
            <el-icon><DataAnalysis /></el-icon>
            <span>销售报表</span>
          </el-menu-item>
          <el-menu-item index="/report/finance">
            <el-icon><Money /></el-icon>
            <span>财务统计</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon 
            @click="toggleSidebar"
            class="collapse-icon"
            :size="20"
          >
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
          
          <!-- 面包屑导航 -->
          <el-breadcrumb separator="/">
            <el-breadcrumb-item 
              v-for="(item, index) in breadcrumb" 
              :key="index"
            >
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <!-- 消息通知 -->
          <el-dropdown trigger="click">
            <span class="header-action">
              <el-badge :value="3" class="badge">
                <el-icon :size="20">
                  <Bell />
                </el-icon>
              </el-badge>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  <el-icon><Warning /></el-icon>
                  库存预警：5种药品库存不足
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-icon><Timer /></el-icon>
                  3种药品即将过期
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-icon><Money /></el-icon>
                  今日销售额：¥12,500
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          
          <!-- 全屏切换 -->
          <el-icon 
            @click="toggleFullScreen" 
            class="header-action fullscreen-icon"
            :size="20"
          >
            <FullScreen />
          </el-icon>
          
          <!-- 用户信息 -->
          <el-dropdown>
            <span class="header-action user-info">
              <el-avatar :size="32" :src="userInfo.avatar" />
              <span class="user-name">{{ userInfo.name }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-icon><Setting /></el-icon>
                  系统设置
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 标签页导航 -->
      <div class="tags-container">
        <el-tabs 
          v-model="activeTab" 
          type="card" 
          closable 
          @tab-remove="handleTabRemove"
          @tab-click="handleTabClick"
        >
          <el-tab-pane
            v-for="tab in tabList"
            :key="tab.name"
            :label="tab.title"
            :name="tab.name"
          >
          </el-tab-pane>
        </el-tabs>
      </div>
      
      <!-- 页面内容 -->
      <div class="content-container">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
      
      <!-- 页脚 -->
      <el-footer class="footer">
        <div class="footer-content">
          <span>© 2024 医药销售管理系统</span>
          <span>技术支持：计算机系</span>
          <span>当前版本：v1.0.0</span>
        </div>
      </el-footer>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useLayoutStore } from '@/stores/layout'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  FirstAidKit, List, Search, ShoppingCart,
  DocumentAdd, Document, Goods, Refresh,
  TrendCharts, Box, Warning, PieChart, DataAnalysis,
  Money, Bell, Timer, FullScreen, User, Setting,
  SwitchButton, ArrowDown, Fold, Expand
} from '@element-plus/icons-vue'

const router = useRouter()
const layoutStore = useLayoutStore()

// 计算属性
const isCollapsed = computed(() => layoutStore.sidebarCollapsed)
const activeMenu = computed(() => layoutStore.activeMenu)
const tabList = computed(() => layoutStore.tabList)
const activeTab = computed({
  get: () => layoutStore.activeTab,
  set: (value) => layoutStore.setActiveTab(value)
})
const breadcrumb = computed(() => layoutStore.breadcrumb)
const userInfo = computed(() => layoutStore.userInfo)

// 方法
const toggleSidebar = () => {
  layoutStore.toggleSidebar()
}

const handleMenuSelect = (index) => {
  layoutStore.setActiveMenu(index)
  
  // 添加标签页
  const title = layoutStore.getTitleByPath(index)
  layoutStore.addTab({ name: index, title })
  
  // 跳转路由
  router.push(index)
}

const handleTabRemove = (targetName) => {
  const activeName = layoutStore.removeTab(targetName)
  router.push(activeName)
}

const handleTabClick = (tab) => {
  router.push(tab.props.name)
}

const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    }
  }
}

// 修改 handleLogout 方法
const handleLogout = async () => {
  try {
    // 使用 ElMessageBox 弹出确认框
    await ElMessageBox.confirm('确定要退出登录吗？', '退出确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 清除本地存储的登录信息
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    
    // 显示成功消息
    ElMessage.success('退出登录成功')
    
    // 跳转到登录页面
    router.push('/login')
  } catch (error) {
    // 用户点击了取消，不执行任何操作
    console.log('用户取消退出')
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.sidebar-collapsed {
  width: 64px !important;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #263445;
}

.logo-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 0 16px;
  color: #fff;
}

.logo-content h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-icon {
  cursor: pointer;
  color: #666;
  transition: color 0.3s;
}

.collapse-icon:hover {
  color: #409EFF;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-action {
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.header-action:hover {
  background-color: #f5f7fa;
}

.badge {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
}

.user-name {
  font-size: 14px;
  color: #333;
}

.tags-container {
  background: #fff;
  padding: 8px 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.content-container {
  flex: 1;
  padding: 20px;
  overflow: auto;
  background-color: #f5f7fa;
}

.footer {
  height: 40px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer-content {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #999;
}

/* 过渡动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

/* 滚动条样式 */
.content-container::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.content-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.content-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.content-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .sidebar {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
  }
  
  .sidebar-collapsed {
    transform: translateX(-100%);
  }
  
  .header-left .el-breadcrumb {
    display: none;
  }
  
  .footer-content {
    flex-direction: column;
    gap: 4px;
    text-align: center;
  }
}

</style>