# 医药销售管理系统 - 后端API文档 

> **版本**: 1.0.0
> **生成日期**: 2025-12-31

---

## 1. 概述

本文档详细描述了医药销售管理系统后端提供的RESTful API。所有接口均基于`HTTP/HTTPS`协议，并推荐使用`JSON`格式进行数据交换。

### 1.1 统一响应格式

大多数接口返回一个标准的`ApiResponse`对象，结构如下：

```json
{
  "code": 0, // 状态码，0表示成功，非0表示失败
  "message": "success", // 响应消息
  "data": { ... } // 实际数据负载
}
```

- **报表接口** (`/api/reports/*`) 是个例外，它们会直接返回数据数组，例如 `List<SalesSummaryDto>`。

### 1.2 认证

除登录接口外，所有API都需要在HTTP请求头中附加`Authorization`字段，值为`Bearer <JWT_TOKEN>`。

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 2. 认证接口 (Auth)

基路径: `/api/auth`

### 2.1 用户登录

- **Endpoint**: `POST /api/auth/login`
- **描述**: 用户通过用户名和密码进行认证，成功后获取JWT。
- **请求体** (`AuthRequest`):
  ```json
  {
    "username": "admin",
    "password": "123456"
  }
  ```
- **成功响应** (`AuthResponse`):
  ```json
  {
    "success": true,
    "message": "Login successful",
    "username": "admin",
    "role": "ADMIN",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```
- **失败响应**: `401 Unauthorized`

### 2.2 获取用户列表

- **Endpoint**: `GET /api/auth/users`
- **描述**: 获取所有状态为“启用”的用户名列表。
- **响应** (`ApiResponse<List<String>>`):
  ```json
  {
    "code": 0,
    "message": "success",
    "data": ["admin", "user1", "user2"]
  }
  ```

---

## 3. 药品管理 (Drug)

基路径: `/drug`

### 3.1 获取药品列表

- **Endpoint**: `GET /drug/list`
- **响应**: `ApiResponse<List<Map<String, Object>>>`

### 3.2 获取药品详情

- **Endpoint**: `GET /drug/detail/{id}`
- **路径参数**: `id` (Integer) - 药品ID。
- **响应**: `ApiResponse<Map<String, Object>>`

### 3.3 新增药品

- **Endpoint**: `POST /drug/add`
- **请求体** (`DrugAddRequest`):
  ```json
  { "name": "阿莫西林", "price": 12.5 }
  ```
- **响应**: `ApiResponse<String>` ("添加成功")

### 3.4 更新药品

- **Endpoint**: `PUT /drug/update`
- **请求体** (`DrugUpdateRequest`):
  ```json
  {
    "id": 1,
    "name": "阿莫西林胶囊",
    "price": 15.0,
    "specification": "250mg*24粒",
    "unit": "盒",
    "manufacturer": "某制药厂",
    "status": "在售"
  }
  ```
- **响应**: `ApiResponse<String>` ("修改成功")

---

## 4. 采购管理 (Purchase)

基路径: `/purchase`

### 4.1 创建采购订单

- **Endpoint**: `POST /purchase/order`
- **描述**: 创建一个新的采购订单，包含多个药品条目。
- **请求体** (`PurchaseOrderRequest`):
  ```json
  {
    "supplierId": 1,
    "items": [
      { "drugId": 101, "quantity": 50, "costPrice": 10.0 },
      { "drugId": 102, "quantity": 30, "costPrice": 25.5 }
    ]
  }
  ```
- **响应**: `ApiResponse<String>` ("进货单保存成功")

### 4.2 获取采购单列表

- **Endpoint**: `GET /purchase/list`
- **描述**: 分页和筛选查询采购单。
- **查询参数**:
  - `orderNo`, `supplierId`, `start`, `end`, `purchaseType`, `paymentMethod`, `warehouse`, `status`
  - `page` (默认1), `limit` (默认10), `sortField`, `sortOrder`
- **响应**: `ApiResponse<Map<String, Object>>` (包含分页信息和列表)

### 4.3 确认采购入库

- **Endpoint**: `POST /purchase/{id}/confirm`
- **描述**: 确认采购订单并更新库存。
- **路径参数**: `id` (Long) - 采购单ID。
- **请求体** (`PurchaseConfirmRequest`):
  ```json
  { "confirmDate": "2025-12-31T10:00:00" }
  ```
- **响应**: `ApiResponse<Map<String, Object>>`

### 4.4 删除采购单

- **Endpoint**: `DELETE /purchase/{id}`
- **路径参数**: `id` (Long) - 采购单ID。
- **响应**: `ApiResponse<String>` ("删除成功")

---

## 5. 销售管理 (Sale)

基路径: `/sale`

### 5.1 新增销售单

- **Endpoint**: `POST /sale/add`
- **请求体** (`SaleAddRequest`):
  ```json
  {
    "customerId": 1,
    "items": [
      { "drugId": 101, "quantity": 2, "salePrice": 15.0 },
      { "drugId": 102, "quantity": 1, "salePrice": 30.0 }
    ]
  }
  ```
- **响应**: `ApiResponse<Sale>` (返回创建的销售单实体)

### 5.2 获取销售单列表

- **Endpoint**: `GET /sale/report`
- **查询参数**:
  - `orderNo`, `customerId`, `start` (datetime), `end` (datetime), `status` (`completed`, `cancelled`)
- **响应**: `ApiResponse<List<SaleListDto>>`

### 5.3 获取销售单详情

- **Endpoint**: `GET /sale/{id}/detail`
- **路径参数**: `id` (Long) - 销售单ID。
- **响应**: `ApiResponse<SaleDetailDto>`

### 5.4 销售退货

- **Endpoint**: `POST /sale/return`
- **请求体** (`SaleReturnRequest`):
  ```json
  {
    "saleId": 123,
    "items": [
      { "saleItemId": 45, "returnQuantity": 1 }
    ]
  }
  ```
- **响应**: `ApiResponse<String>` ("退货成功")

### 5.5 取消销售单

- **Endpoint**: `POST /sale/cancel`
- **请求体** (`CancelSaleRequest`):
  ```json
  { "saleId": 123 }
  ```
- **响应**: `ApiResponse<Object>`

---

## 6. 库存管理 (Stock)

基路径: `/stock`

### 6.1 获取所有库存详情

- **Endpoint**: `GET /stock/all`
- **响应**: `ApiResponse<List<StockDetailDto>>`

### 6.2 获取单个药品总库存

- **Endpoint**: `GET /stock/{drugId}`
- **路径参数**: `drugId` (Integer)
- **响应**: `ApiResponse<Integer>` (总库存量)

### 6.3 库存调整

- **Endpoint**: `POST /stock/{stockId}/adjust`
- **描述**: 对特定库存批次进行数量调整（盘盈、盘亏）。
- **路径参数**: `stockId` (Integer) - 库存批次ID。
- **请求体** (`AdjustRequest`):
  ```json
  { "quantity": 5, "type": "in" } // type: 'in' (入库), 'out' (出库)
  ```
- **响应**: `ApiResponse<Object>` (包含 `newQty`)

### 6.4 库存调拨

- **Endpoint**: `POST /stock/{stockId}/transfer`
- **路径参数**: `stockId` (Integer) - 源库存批次ID。
- **请求体** (`TransferRequest`):
  ```json
  { "quantity": 10, "toWarehouseId": 2 }
  ```
- **响应**: `ApiResponse<Object>` (包含 `transferred` 数量)

### 6.5 库存预警与建议

- **Endpoint**: `GET /stock/warning/{drugId}`
- **描述**: 检查单个药品的库存是否低于预警值。
- **响应**: `ApiResponse<String>`

- **Endpoint**: `GET /stock/suggest/{drugId}`
- **描述**: 为低于预警的药品提供建议补货量。
- **响应**: `ApiResponse<Integer>`

### 6.6 库存预警处理

- **Endpoint**: `POST /stock/warning/{warningId}/process`
- **描述**: 处理单个库存预警。
- **路径参数**: `warningId` (Long)
- **请求体**: `{ "action": "handle" }`
- **响应**: `ApiResponse<Object>`

---

## 7. 报表接口 (Reports)

基路径: `/api/reports`

### 7.1 低库存报表

- **Endpoint**: `GET /api/reports/low-stock`
- **描述**: 获取所有低于预警库存的药品列表。
- **响应**: `List<LowStockDto>` (直接返回数组)

### 7.2 销售报表

- **Endpoint**: `GET /api/reports/sales`
- **描述**: 获取销售统计数据。支持按天或按时间段查询。
- **查询参数**:
  - `date` (YYYY-MM-DD): 获取指定日期的详细报表 (`DailySalesReportDto`)。
  - `start` & `end` (datetime): 获取时间段内的销售汇总 (`List<SalesSummaryDto>`)。
- **响应**: `DailySalesReportDto` 或 `List<SalesSummaryDto>` (直接返回对象或数组)

---

## 8. 财务接口 (Finance)

基路径: `/finance`

### 8.1 今日财务报告

- **Endpoint**: `GET /finance/today`
- **响应**: `ApiResponse<FinanceReportResponse>`
  ```json
  {
    "code": 0,
    "message": "success",
    "data": { "salesAmount": 5000.0, "salesCount": 50 }
  }
  ```

### 8.2 本月财务报告

- **Endpoint**: `GET /finance/month`
- **描述**: (当前实现与`/finance/today`相同，为占位接口)
- **响应**: `ApiResponse<FinanceReportResponse>`

