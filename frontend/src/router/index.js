import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../views/layout/index.vue'
import { ElMessage } from 'element-plus'

const routes = [
  // 1. 根路径根据登录状态动态重定向
  {
    path: '/',
    redirect: (to) => {
      // 根据是否有token来决定重定向到哪里
      const token = localStorage.getItem('token') || sessionStorage.getItem('token')
      return token ? '/drug/list' : '/login'
    }
  },
  // 2. 登录页
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { 
      title: '登录',
      requiresAuth: false
    }
  },
  // 3. 主布局（需要登录后才能访问的页面）- 改回原来的路径结构
  {
    path: '/',
    component: Layout,
    redirect: '/drug/list',
    children: [
      // 药品管理
      {
        path: 'drug/list',
        name: 'DrugList',
        component: () => import('../views/drug/DrugList.vue'),
        meta: { 
          title: '药品列表',
          requiresAuth: true
        },
        props: {
          title: '药品列表管理',
          description: '管理所有药品信息，包括添加、编辑、删除、查询等功能',
          features: ['药品信息录入', '药品信息编辑', '药品信息查询', '药品分类管理']
        }
      },
      {
        path: 'drug/search',
        name: 'DrugSearch',
        component: () => import('../views/drug/DrugSearch.vue'),
        meta: { 
          title: '药品搜索',
          requiresAuth: true
        }
      },
      
      // 进货管理
      {
        path: 'purchase/order',
        name: 'PurchaseOrder',
        component: () => import('../views/purchase/PurchaseOrder.vue'),
        meta: { 
          title: '进货单',
          requiresAuth: true
        }
      },
      {
        path: 'purchase/history',
        name: 'PurchaseHistory',
        component: () => import('../views/purchase/PurchaseHistory.vue'),
        meta: { 
          title: '进货历史',
          requiresAuth: true
        }
      },
      
      // 销售管理
      {
        path: 'sale/order',
        name: 'SaleOrder',
        component: () => import('../views/sale/SaleOrder.vue'),
        meta: { 
          title: '销售单',
          requiresAuth: true
        }
      },
      {
        path: 'sale/history',
        name: 'SaleHistory',
        component: () => import('../views/sale/SaleHistory.vue'),
        meta: { 
          title: '销售历史',
          requiresAuth: true
        }
      },
      {
        path: 'sale/return',
        name: 'SaleReturn',
        component: () => import('../views/sale/SaleReturn.vue'),
        meta: { 
          title: '销售退货',
          requiresAuth: true
        }
      },
      {
        path: 'sale/daily',
        name: 'SaleDaily',
        component: () => import('../views/sale/SaleDaily.vue'),
        meta: { 
          title: '当日销售',
          requiresAuth: true
        }
      },
      
      // 库存管理
      {
        path: 'stock/query',
        name: 'StockQuery',
        component: () => import('../views/stock/StockQuery.vue'),
        meta: { 
          title: '库存查询',
          requiresAuth: true
        }
      },
      {
        path: 'stock/warning',
        name: 'StockWarning',
        component: () => import('../views/stock/StockWarning.vue'),
        meta: { 
          title: '库存预警',
          requiresAuth: true
        }
      },
      
      // 报表统计
      {
        path: 'report/sale',
        name: 'SaleReport',
        component: () => import('../views/report/SaleReport.vue'),
        meta: { 
          title: '销售报表',
          requiresAuth: true
        }
      },
      {
        path: 'report/finance',
        name: 'FinanceReport',
        component: () => import('../views/report/FinanceReport.vue'),
        meta: { 
          title: '财务统计',
          requiresAuth: true
        }
      }
    ]
  },
  // 404页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: { 
      title: '页面未找到',
      requiresAuth: false
    }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 医药销售管理系统`
  }
  
  // 获取token
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  
  // 检查是否需要登录验证
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  
  // 处理登录页的特殊情况
  if (to.path === '/login') {
    if (token) {
      // 已登录用户访问登录页，跳转到首页
      ElMessage.info('您已登录')
      next('/drug/list')
      return
    }
    next()
    return
  }
  
  // 检查权限
  if (requiresAuth && !token) {
    // 需要登录但未登录，重定向到登录页
    ElMessage.warning('请先登录')
    next('/login')
    return
  }
  
  // 其他情况正常访问
  next()
})

export default router