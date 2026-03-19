// src/api/customer.js
import request from '@/utils/request'

export default {
  // 获取客户列表
  getCustomerList(params) {
    return request.get('/customer/list', { params })
  }
}
