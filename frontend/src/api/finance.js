// src/api/finance.js
import request from '@/utils/request'

export default {
  // 当日财务统计
  getTodayFinance() {
    return request.get('/finance/today')
  },
  
  // 当月财务统计
  getMonthFinance() {
    return request.get('/finance/month')
  }
}