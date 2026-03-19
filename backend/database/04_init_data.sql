-- 04_init_data.sql (fixed for 02_tables.sql)
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE medicine_sales;

-- =========================
-- 角色 role（role_name 有唯一键）
-- =========================
INSERT INTO role(role_name, description) VALUES
('ADMIN', '系统管理员'),
('SALES', '销售人员'),
('WAREHOUSE', '仓库人员')
ON DUPLICATE KEY UPDATE
  description = VALUES(description);

-- =========================
-- 用户 users（username 有唯一键）
-- password_hash 请替换成 BCrypt 等真实 hash
-- =========================
INSERT INTO users(username, password_hash, role_id, status)
SELECT 'admin', 'REPLACE_WITH_HASH', r.id, 1
FROM role r
WHERE r.role_name='ADMIN'
ON DUPLICATE KEY UPDATE
  password_hash=VALUES(password_hash),
  role_id=VALUES(role_id),
  status=VALUES(status);

INSERT INTO users(username, password_hash, role_id, status)
SELECT 'seller1', 'REPLACE_WITH_HASH_SELLER1', r.id, 1
FROM role r
WHERE r.role_name='SALES'
ON DUPLICATE KEY UPDATE
  password_hash=VALUES(password_hash),
  role_id=VALUES(role_id),
  status=VALUES(status);

-- =========================
-- 药品类别 drug_category（name 有唯一键）
-- =========================
INSERT INTO drug_category(name) VALUES
('抗生素'),
('解热镇痛'),
('心血管'),
('消化系统')
ON DUPLICATE KEY UPDATE
  name = VALUES(name);

-- =========================
-- 供应商 supplier（无唯一键：用 NOT EXISTS 防重复）
-- =========================
INSERT INTO supplier (name, phone, address, license_no)
SELECT '示例供应商A','13800000000','示例地址A', NULL
WHERE NOT EXISTS (SELECT 1 FROM supplier WHERE name='示例供应商A');

INSERT INTO supplier (name, phone, address, license_no)
SELECT '供应商B','13900001111','地址B', NULL
WHERE NOT EXISTS (SELECT 1 FROM supplier WHERE name='供应商B');

-- =========================
-- 客户 customer（无唯一键：用 NOT EXISTS 防重复）
-- =========================
INSERT INTO customer (name, phone, address, credit_code)
SELECT '示例客户A','13900000000','示例地址C', NULL
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE name='示例客户A');

INSERT INTO customer (name, phone, address, credit_code)
SELECT '客户B','13800002222','地址C', NULL
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE name='客户B');

-- =========================
-- 药品 drug（code 有唯一键）
-- =========================
INSERT INTO drug(code, name, category_id, spec, unit, manufacturer, approval_no, retail_price, status)
SELECT 'D001', '阿莫西林胶囊', c.id, '0.25g*24粒', '盒', '示例厂家', '国药准字XXXX', 18.50, 1
FROM drug_category c WHERE c.name='抗生素'
ON DUPLICATE KEY UPDATE
  name=VALUES(name),
  retail_price=VALUES(retail_price),
  status=VALUES(status);

INSERT INTO drug(code, name, category_id, spec, unit, manufacturer, approval_no, retail_price, status)
SELECT 'D002', '对乙酰氨基酚', c.id, '500mg', '片', '示例厂家', 'APP002', 5.50, 1
FROM drug_category c WHERE c.name='解热镇痛'
ON DUPLICATE KEY UPDATE
  name=VALUES(name),
  retail_price=VALUES(retail_price),
  status=VALUES(status);

-- =========================
-- 批次 drug_batch（(drug_id,batch_no) 唯一）
-- =========================
INSERT INTO drug_batch (drug_id, batch_no, production_date, expire_date, purchase_price)
VALUES (
  (SELECT id FROM drug WHERE code='D002' ORDER BY id DESC LIMIT 1),
  'B-DA-001',
  '2023-01-01',
  '2024-01-01',
  2.00
)
ON DUPLICATE KEY UPDATE
  production_date=VALUES(production_date),
  expire_date=VALUES(expire_date),
  purchase_price=VALUES(purchase_price);

-- =========================
-- 库存 stock_batch（(drug_id,batch_id) 唯一）
-- =========================
INSERT INTO stock_batch (drug_id, batch_id, qty, warning_qty, warehouse)
VALUES (
  (SELECT id FROM drug WHERE code='D002' ORDER BY id DESC LIMIT 1),
  (SELECT id FROM drug_batch WHERE batch_no='B-DA-001' ORDER BY id DESC LIMIT 1),
  5, 10, 'WH-TEST'
)
ON DUPLICATE KEY UPDATE
  qty=VALUES(qty),
  warning_qty=VALUES(warning_qty),
  warehouse=VALUES(warehouse);

-- =========================
-- 进货 purchase（purchase_no 唯一）
-- =========================
INSERT INTO purchase (purchase_no, supplier_id, purchaser_id, total_amount, status, purchased_at)
VALUES (
  'P-TEST-001',
  (SELECT id FROM supplier WHERE name='供应商B' ORDER BY id DESC LIMIT 1),
  (SELECT id FROM users WHERE username='admin' ORDER BY id DESC LIMIT 1),
  200.00, 1, NOW()
)
ON DUPLICATE KEY UPDATE
  supplier_id=VALUES(supplier_id),
  purchaser_id=VALUES(purchaser_id),
  total_amount=VALUES(total_amount),
  status=VALUES(status),
  purchased_at=VALUES(purchased_at);

-- 进货明细 purchase_item（无唯一键：用 IGNORE 防止重复执行爆炸）
INSERT IGNORE INTO purchase_item (purchase_id, drug_id, batch_id, qty, unit_price, amount)
VALUES (
  (SELECT id FROM purchase WHERE purchase_no='P-TEST-001' ORDER BY id DESC LIMIT 1),
  (SELECT id FROM drug WHERE code='D002' ORDER BY id DESC LIMIT 1),
  (SELECT id FROM drug_batch WHERE batch_no='B-DA-001' ORDER BY id DESC LIMIT 1),
  100, 2.00, 200.00
);

-- =========================
-- 销售 sale（sale_no 唯一）
-- =========================
INSERT INTO sale (sale_no, customer_id, seller_id, total_amount, status, sold_at)
VALUES (
  'S-TEST-001',
  (SELECT id FROM customer WHERE name='客户B' ORDER BY id DESC LIMIT 1),
  (SELECT id FROM users WHERE username='seller1' ORDER BY id DESC LIMIT 1),
  30.00, 1, NOW()
)
ON DUPLICATE KEY UPDATE
  customer_id=VALUES(customer_id),
  seller_id=VALUES(seller_id),
  total_amount=VALUES(total_amount),
  status=VALUES(status),
  sold_at=VALUES(sold_at);

-- 销售明细 sale_item（无唯一键：用 IGNORE 防止重复执行爆炸）
INSERT IGNORE INTO sale_item (sale_id, drug_id, batch_id, qty, unit_price, amount)
VALUES (
  (SELECT id FROM sale WHERE sale_no='S-TEST-001' ORDER BY id DESC LIMIT 1),
  (SELECT id FROM drug WHERE code='D002' ORDER BY id DESC LIMIT 1),
  (SELECT id FROM drug_batch WHERE batch_no='B-DA-001' ORDER BY id DESC LIMIT 1),
  3, 10.00, 30.00
);
