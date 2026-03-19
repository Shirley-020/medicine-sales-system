// src/api/auth.js
import request from '@/utils/request'

export default {
  // 登录接口
  login(data) {
    return request.post('/api/auth/login', data)
  },
  
  // 退出登录（前端清除token即可）
  logout() {
    return Promise.resolve()
  },
  
  // 获取用户列表
  getUsers() {
    return request.get('/api/auth/users')
  }
}