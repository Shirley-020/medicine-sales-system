// src/api/sale.js
import request from '@/utils/request'

export default {
  // 销售登记
  addSale(data) {
    return request.post('/sale/add', data)
  },
  
  // 销售退货
  returnSale(data) {
    return request.post('/sale/return', data)
  },
  
  // 销售报表
  getSaleReport(params) {
    return request.get('/sale/report', { params })
  },
  
  // 获取销售单详情（包含销售项列表）
  getSaleDetail(id) {
    return request.get(`/sale/${id}/detail`)
  },
  
  // 退货报表
  getReturnReport(params) {
    return request.get('/sale/return/report', { params })
  },

  // 取消销售单
  cancelSale(id) {
    return request.post('/sale/cancel', { id })
  },

  // 打印销售单（返回下载链接或文件）
  printOrder(id) {
    return request.get(`/sale/${id}/print`)
  }
}