# 医药销售管理系统 — 后端（Spring Boot）

本目录为后端服务（Spring Boot + MySQL + Flyway），提供药品、采购、销售、库存、报表、财务等接口，并使用 JWT 做登录鉴权。

## 目录
- 项目结构
- 本地运行（开发）
- 环境变量
- 数据库迁移与初始数据
- 认证/鉴权
- 常用 API 示例

## 项目结构（重要位置）
- `src/main/java/.../controller`：REST 控制器（接口实现）
- `src/main/java/.../service`：业务逻辑
- `src/main/java/.../repository`：JPA 数据访问
- `src/main/resources/db/migration`：Flyway SQL 迁移脚本（V1/V2/V3）
- `src/main/resources/application.properties`：默认配置（开发回退）
- `JWT_ENV_README.md`：如何设置 `JWT_SECRET`
- `database/README.md`：本地数据库启动与迁移说明

> 接口文档：项目根目录的 `API_OC.md`（以当前代码为准）。

## 本地运行（开发）
1. 启动 MySQL（可用 Docker）：参见 `database/README.md`。
2. 构建并运行：

```bash
mvn -DskipTests package
mvn spring-boot:run
```

或使用 mvnw：

```bash
./mvnw -DskipTests package
./mvnw spring-boot:run
```

3. 默认端口：`8080`。

## 环境变量（重要）
- `JWT_SECRET`：JWT 签名密钥。生产/测试环境建议通过环境变量注入，优先级高于 `application.properties` 中的 `jwt.secret`。详见 `JWT_ENV_README.md`。
- `SPRING_PROFILES_ACTIVE`：可选，选择配置文件（`dev`/`prod`）。

## 数据库初始化与演示数据
本项目 **默认不开启 Flyway**，而是通过 `spring.jpa.hibernate.ddl-auto=update` 让 JPA 在开发环境自动建表。如需快速体验完整功能，可手动导入整库演示脚本：

1. 确保数据库 `medicine_sales` 是空库（或可覆盖）。
2. 执行 `back/database/demo_full.sql`，该脚本包含建表语句及演示数据（药品/用户/采购/销售等）。
   ```bash
   mysql -h 127.0.0.1 -P 13306 -u root -proot123456 medicine_sales < database/demo_full.sql
   ```
3. 默认演示账号：
   - 用户名：`admin`
   - 密码：`admin123`

若你更倾向于**纯空库**：直接启动后端即可让 JPA 自动建空表，然后通过系统后台自行录入数据。

## 认证/鉴权
- 登录：`POST /api/auth/login`，请求 JSON：`{ "username": "...", "password": "..." }`，成功返回 `token`（JWT）。
- 受保护接口请求头：`Authorization: Bearer <TOKEN>`。
- CORS：开发阶段默认较宽松；如需限制来源，可在 `src/main/resources/application.properties` 调整 CORS 配置。

## 常用 API 示例
- 登录：
```bash
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username":"admin","password":"admin123"}'
```

- 获取库存列表（需登录）：
```bash
curl -H "Authorization: Bearer <TOKEN>" http://localhost:8080/stock/all
```
