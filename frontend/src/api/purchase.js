// src/api/purchase.js
import request from '@/utils/request'

export default {
  // 添加进货（旧接口，单个药品）
  addPurchase(data) {
    return request.post('/purchase/add', data)
  },
  
  // 添加进货单（新接口，完整进货单）
  addPurchaseOrder(data) {
    return request.post('/purchase/order', data)
  },
  
  // 进货报表
  getPurchaseReport(params) {
    return request.get('/purchase/report', { params })
  },

  // 获取进货单列表
  getPurchaseList(params) {
    return request.get('/purchase/list', { params })
  },

  // 供应商列表
  getSuppliers(params) {
    return request.get('/supplier/list', { params })
  },

  // 删除进货单
  deletePurchase(id) {
    return request.delete(`/purchase/${id}`)
  },

  // 确认入库
  confirmReceipt(id) {
    return request.post(`/purchase/${id}/confirm`)
  }
}