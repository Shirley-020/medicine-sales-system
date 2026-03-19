# 医药销售管理系统 — 前端（Vue 3 + Vite）

基于 Vue 3 + Vite + Element Plus 构建的医药销售管理系统前端。

## 功能模块

- **登录/登出**：JWT 认证
- **药品管理**：药品列表、搜索、新增/编辑
- **采购管理**：采购单创建、查询、确认
- **销售管理**：销售开单、退货、历史查询
- **库存管理**：库存查询、调拨、预警处理
- **报表统计**：销售报表、财务报表
- **系统设置**：用户管理、权限控制

## 开发环境

### 推荐开发工具

- [VS Code](https://code.visualstudio.com/) + 插件：
  - [Volar (Vue 3)](https://marketplace.visualstudio.com/items?itemName=Vue.volar)
  - [ESLint](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint)
  - [Prettier](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode)

### 浏览器扩展

- **Chrome/Edge**：
  - [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd)
  - [Redux DevTools](https://chrome.google.com/webstore/detail/redux-devtools/lmhkpmbekcpmknklioeibfkpmmfibljd)

## 项目结构

```
src/
├── api/               # 接口请求
├── assets/            # 静态资源
├── components/        # 公共组件
├── router/            # 路由配置
├── stores/            # 状态管理
├── utils/             # 工具函数
└── views/             # 页面组件
    ├── drug/          # 药品管理
    ├── purchase/      # 采购管理
    ├── sale/          # 销售管理
    ├── stock/         # 库存管理
    ├── report/        # 报表统计
    └── layout/        # 布局组件
```

## 快速开始

### 环境要求

- Node.js 16+
- npm 8+

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
# 启动开发服务器（默认 http://localhost:5173）
npm run dev
```

### 构建生产版本

```bash
# 构建生产版本到 dist 目录
npm run build

# 预览生产版本
npm run preview
```

## 环境变量

项目使用 `.env` 文件管理环境变量，开发时可创建 `.env.development`：

```ini
# 后端 API 基础 URL（默认 http://localhost:8080）
VITE_API_BASE_URL=http://localhost:8080

# 其他 Vite 环境变量...
```

## 代码规范

- 使用 ESLint + Prettier 统一代码风格
- 提交前请运行 `npm run lint` 检查代码
- 组件命名采用 PascalCase，如 `MyComponent.vue`
- 变量/方法使用 camelCase

## 部署

构建后的文件位于 `dist` 目录，可部署到任何静态文件服务器（如 Nginx）。

### Nginx 配置示例

```nginx
server {
    listen       80;
    server_name  your-domain.com;
    root         /path/to/dist;
    index        index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # 代理 API 请求到后端
    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 浏览器兼容性

- Chrome 88+
- Edge 88+
- Firefox 78+
- Safari 14+

> 如需支持 IE 11，需要额外配置 Babel 和 polyfills。

## 相关文档

- [Vue 3 文档](https://v3.vuejs.org/)
- [Vite 文档](https://vitejs.dev/)
- [Element Plus 文档](https://element-plus.org/)
- [Vue Router 文档](https://router.vuejs.org/)
- [Pinia 文档](https://pinia.vuejs.org/)

## 许可证

MIT