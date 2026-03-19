// src/api/drug.js
import request from '@/utils/request'

export default {
  // 获取药品列表
  getDrugList() {
    return request.get('/drug/list')
  },
  
  // 获取药品详情
  getDrugDetail(id) {
    return request.get(`/drug/detail/${id}`)
  },
  
  // 添加药品
  addDrug(data) {
    return request.post('/drug/add', data)
  },
  
  // 更新药品
  updateDrug(data) {
    return request.put('/drug/update', data)
  },

  // 导出药品列表（返回文件流）
  exportDrugList(params) {
    return request.get('/drug/export', { params, responseType: 'arraybuffer' })
  }
}