// src/stores/layout.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useLayoutStore = defineStore('layout', () => {
  // 侧边栏状态
  const sidebarCollapsed = ref(false)
  
  // 当前激活菜单
  const activeMenu = ref('/drug/list')
  
  // 标签页列表
  const tabList = ref([
    { name: '/drug/list', title: '药品列表' }
  ])
  
  // 当前激活标签页
  const activeTab = ref('/drug/list')
  
  // 用户信息
  const userInfo = ref({
    name: '管理员',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    role: 'admin'
  })
  
  // 计算属性：面包屑导航
  const breadcrumb = computed(() => [
    { path: '/', title: '首页' },
    { path: activeMenu.value, title: getTitleByPath(activeMenu.value) }
  ])
  
  function getTitleByPath(path) {
    const map = {
      '/drug/list': '药品列表',
      '/drug/search': '药品搜索',
      '/purchase/order': '进货单',
      '/purchase/history': '进货历史',
      '/sale/order': '销售单',
      '/sale/return': '销售退货',
      '/sale/daily': '当日销售',
      '/stock/query': '库存查询',
      '/stock/warning': '库存预警',
      '/report/sale': '销售报表',
      '/report/finance': '财务统计'
    }
    return map[path] || '未知页面'
  }
  
  // 切换侧边栏状态
  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }
  
  // 设置激活菜单
  function setActiveMenu(path) {
    activeMenu.value = path
  }
  
  // 添加标签页
  function addTab(tab) {
    if (!tabList.value.some(t => t.name === tab.name)) {
      tabList.value.push(tab)
    }
    activeTab.value = tab.name
  }
  
  // 移除标签页
  function removeTab(targetName) {
    const tabs = tabList.value
    let activeName = activeTab.value
    
    if (activeName === targetName) {
      tabs.forEach((tab, index) => {
        if (tab.name === targetName) {
          const nextTab = tabs[index + 1] || tabs[index - 1]
          if (nextTab) {
            activeName = nextTab.name
          }
        }
      })
    }
    
    activeTab.value = activeName
    tabList.value = tabs.filter(tab => tab.name !== targetName)
    return activeName
  }
  
  // 设置激活标签页
  function setActiveTab(name) {
    activeTab.value = name
  }
  
  return {
    sidebarCollapsed,
    activeMenu,
    tabList,
    activeTab,
    userInfo,
    breadcrumb,
    toggleSidebar,
    setActiveMenu,
    addTab,
    removeTab,
    setActiveTab,
    getTitleByPath
  }
})