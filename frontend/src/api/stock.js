// src/api/stock.js
import request from '@/utils/request'

export default {
  // 获取所有库存
  getAllStock() {
    return request.get('/stock/all')
  },

  // 获取库存列表（支持查询参数）
  getStockList(params) {
    return request.get('/stock', { params })
  },

  // 获取指定药品库存（按 drugId 获取批次列表）
  getStockByDrugId(drugId) {
    return request.get(`/stock/${drugId}`)
  },

  // 获取某药品的批次列表（后端可能提供此接口）
  getBatchesByDrugId(drugId) {
    return request.get(`/stock/${drugId}/batches`)
  },

  // 获取库存预警
  getStockWarning(drugId) {
    return request.get(`/stock/warning/${drugId}`)
  },

  // 获取补货建议
  getStockSuggest(drugId) {
    return request.get(`/stock/suggest/${drugId}`)
  },

  // 处理预警（由后端执行相应的业务，如生成订单、调拨、报废等）
  processWarning(warningId, data) {
    return request.post(`/stock/warning/${warningId}/process`, data)
  },

  // 保存处理模板
  saveWarningTemplate(data) {
    return request.post('/stock/warning/template', data)
  },

  // 发送预警提醒（如短信/通知）
  remindWarning(warningId) {
    return request.post(`/stock/warning/${warningId}/remind`)
  },

  // 打印预警详情或生成导出链接
  printWarning(warningId) {
    return request.get(`/stock/warning/${warningId}/print`)
  },

  // 库存调拨（需要后端实现具体逻辑）
  transferStock(stockId, data) {
    return request.post(`/stock/${stockId}/transfer`, data)
  },

  // 库存调整：使用库存批次ID作为路径参数
  // 调整库存（需要 quantity 和 type，path 使用 stockId）
  adjustStock(stockId, data) {
    const payload = { 
      quantity: data?.quantity,
      type: data?.type,
      reason: data?.reason,
      remark: data?.remark
    }
    return request.post(`/stock/${stockId}/adjust`, payload)
  },

  // 批量处理预警
  batchProcessWarnings(data) {
    return request.post('/stock/warning/batch/process', data)
  },

  // 导出库存（根据查询参数导出）
  exportInventory(params) {
    return request.get('/stock/export', { params })
  },

  // 批量导出（按 ids）
  exportInventoryBatch(data) {
    return request.post('/stock/export/batch', data)
  },

  // 删除库存批次
  deleteStock(stockId) {
    return request.delete(`/stock/${stockId}`)
  }
}
