# 医药销售管理系统（medicine-sales-system）

本项目为医药销售管理系统，包含：
- **后端**：Spring Boot（JPA + MySQL），JWT 鉴权
- **前端**：Vue 3 + Vite + Element Plus

> 接口文档见根目录：[`API_OC.md`](./API_OC.md)

---

## 目录结构

```
.
├── API_OC.md              # 后端接口文档（以当前代码为准）
├── back/                  # 后端 Spring Boot 工程
└── front/                 # 前端 Vue3/Vite 工程
```

---

## 快速启动（开发环境）

### 1) 启动数据库（MySQL）

后端默认数据库连接在：`back/src/main/resources/application.properties`。

当前默认示例：
- `jdbc:mysql://localhost:13306/medicine_sales`
- 用户名：`root`
- 密码：`root123456`

如果使用 Docker 启动 MySQL，可参考：
- `back/database/README.md`
- `back/docker-compose.yml`

#### （可选）导入演示数据（整库快照）

项目提供了一个整库演示脚本：
- `back/database/demo_full.sql`

该脚本包含**建表 + 演示数据**，适合课程演示/验收。

导入前请确保目标数据库是空库（或你愿意覆盖现有表）。推荐做法：
- 如果你用 `back/docker-compose.yml` 启动 MySQL，且不介意清空旧数据：
  ```bash
  cd back
  docker compose down -v
  docker compose up -d
  ```

然后执行导入（Windows PowerShell / CMD 均可，按需调整账号端口）：

```bash
# 在项目根目录执行也可以
mysql -h 127.0.0.1 -P 13306 -u root -proot123456 medicine_sales < back/database/demo_full.sql
```

> 本项目当前**不启用 Flyway**（`spring.flyway.enabled=false`），并使用 `spring.jpa.hibernate.ddl-auto=update` 在开发环境自动建表/更新表结构。
> 如果你已经导入了 `demo_full.sql`（已包含建表），后端启动时 JPA 的 `update` 通常不会破坏现有表，但若你后续修改了实体，仍可能造成结构差异。

### 2) 启动后端（Spring Boot）

进入 `back` 目录：

```bash
cd back
mvn -DskipTests package
mvn spring-boot:run
```

后端默认端口：`http://localhost:8080`

### 3) 启动前端（Vue 3 + Vite）

进入 `front` 目录：

```bash
cd front
npm install
npm run dev
```

前端默认地址：`http://localhost:5173`

---

## 前后端联调说明

前端 Axios 基础地址配置在：`front/src/utils/request.js`

```js
baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
```

如需修改后端地址，可在前端创建环境变量文件（例如 `front/.env.development`）：

```ini
VITE_API_BASE_URL=http://localhost:8080
```

---

## 鉴权（JWT）

- 登录接口：`POST /api/auth/login`
- 登录成功后返回 `token`
- 调用受保护接口需携带请求头：

```
Authorization: Bearer <TOKEN>
```

JWT 密钥可通过环境变量设置（推荐）：
- 后端说明：`back/JWT_ENV_README.md`

---

## 常用接口（示例）

- 登录：

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

- 获取库存列表（需登录）：

```bash
curl -H "Authorization: Bearer <TOKEN>" \
  http://localhost:8080/stock/all
```

---

## 子项目说明

- 后端 README：[`back/README.md`](./back/README.md)
- 前端 README：[`front/README.md`](./front/README.md)




