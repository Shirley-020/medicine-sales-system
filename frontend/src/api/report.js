// src/api/report.js
import request from '@/utils/request'

export default {
  // 低库存报表
  getLowStockReport() {
    return request.get('/api/reports/low-stock')
  },
  
  // 销售报表（按时间）
  getSalesReport(params) {
    return request.get('/api/reports/sales', { 
      params,
      // 禁用缓存，确保每次获取最新数据
      headers: {
        'Cache-Control': 'no-cache'
      }
    })
  },

  // 导出销售报表（返回文件流）
  exportSalesReport(params) {
    return request.get('/api/reports/sales/export', { params, responseType: 'arraybuffer' })
  },

  // 导出财务报表（返回文件流）
  exportFinanceReport(params) {
    return request.get('/api/reports/finance/export', { params, responseType: 'arraybuffer' })
  }
}