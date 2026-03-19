// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求配置错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 后端 ApiResponse 格式：{code: 0/-1, message: "...", data: ...}
    // code === 0 表示成功，code !== 0 表示失败
    if (res.code !== undefined && res.code !== 0) {
      const errorMsg = res.message || '请求失败'
      ElMessage.error(errorMsg)
      return Promise.reject(new Error(errorMsg))
    }
    
    return res
  },
  error => {
    console.error('响应错误:', error)
    
    if (error.response) {
      const res = error.response.data
      let errorMessage = '网络错误'
      
      // 优先使用后端返回的错误消息
      if (res && res.message) {
        errorMessage = res.message
      } else if (res && res.code !== undefined && res.code !== 0) {
        errorMessage = res.message || '请求失败'
      }
      
      switch (error.response.status) {
        case 401:
          errorMessage = '登录已过期，请重新登录'
          localStorage.removeItem('token')
          localStorage.removeItem('username')
          localStorage.removeItem('role')
          router.push('/login')
          break
        case 403:
          errorMessage = errorMessage || '没有权限访问'
          break
        case 404:
          errorMessage = errorMessage || '请求的资源不存在'
          break
        case 500:
          errorMessage = errorMessage || '服务器内部错误'
          break
        default:
          errorMessage = errorMessage || '网络错误'
      }
      
      ElMessage.error(errorMessage)
    } else {
      ElMessage.error('网络连接失败，请检查网络设置')
    }
    
    return Promise.reject(error)
  }
)

export default service