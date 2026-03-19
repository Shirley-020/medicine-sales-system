-- V3__init_data.sql - initial seed data (idempotent)
USE medicine_sales;

-- NOTE: Replace 'REPLACE_WITH_BCRYPT_HASH' with a real bcrypt password hash for the admin user before deploying to production.

-- Roles
INSERT INTO role (role_name, description, created_at, updated_at)
SELECT 'ADMIN', '系统管理员', NOW(), NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM role WHERE role_name='ADMIN');
INSERT INTO role (role_name, description, created_at, updated_at)
SELECT 'SALES', '销售人员', NOW(), NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM role WHERE role_name='SALES');
INSERT INTO role (role_name, description, created_at, updated_at)
SELECT 'WAREHOUSE', '仓库人员', NOW(), NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM role WHERE role_name='WAREHOUSE');

-- Admin user (placeholder password hash)
INSERT INTO `user` (username, password_hash, role_id, status, created_at, updated_at)
SELECT 'admin', 'REPLACE_WITH_BCRYPT_HASH', r.id, 1, NOW(), NOW()
FROM role r
WHERE r.role_name='ADMIN' AND NOT EXISTS (SELECT 1 FROM `user` u WHERE u.username='admin');

-- Example seller user
INSERT INTO `user` (username, password_hash, role_id, status, created_at, updated_at)
SELECT 'seller1', 'REPLACE_WITH_BCRYPT_HASH', r.id, 1, NOW(), NOW()
FROM role r
WHERE r.role_name='SALES' AND NOT EXISTS (SELECT 1 FROM `user` u WHERE u.username='seller1');

-- Drug categories
INSERT INTO drug_category (name, created_at, updated_at)
SELECT '抗生素', NOW(), NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drug_category WHERE name='抗生素');
INSERT INTO drug_category (name, created_at, updated_at)
SELECT '解热镇痛', NOW(), NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drug_category WHERE name='解热镇痛');
INSERT INTO drug_category (name, created_at, updated_at)
SELECT '心血管', NOW(), NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drug_category WHERE name='心血管');
INSERT INTO drug_category (name, created_at, updated_at)
SELECT '消化系统', NOW(), NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drug_category WHERE name='消化系统');

-- Suppliers and Customers
INSERT INTO supplier (name, phone, address, created_at, updated_at)
SELECT '示例供应商A','13800000000','示例地址A', NOW(), NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM supplier WHERE name='示例供应商A');

INSERT INTO customer (name, phone, address, created_at, updated_at)
SELECT '示例客户A','13900000000','示例地址C', NOW(), NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM customer WHERE name='示例客户A');

-- Example drugs
INSERT INTO drug (code, name, category_id, spec, unit, manufacturer, approval_no, retail_price, status, created_at, updated_at)
SELECT 'D001', '阿莫西林胶囊', c.id, '0.25g*24粒', '盒', '示例厂家', '国药准字XXXX', 18.50, 1, NOW(), NOW()
FROM drug_category c
WHERE c.name='抗生素' AND NOT EXISTS (SELECT 1 FROM drug WHERE code='D001');

INSERT INTO drug (code, name, category_id, spec, unit, manufacturer, approval_no, retail_price, status, created_at, updated_at)
SELECT 'D002', '对乙酰氨基酚', c.id, '500mg', '片', '示例厂家', 'APP002', 5.50, 1, NOW(), NOW()
FROM drug_category c
WHERE c.name='解热镇痛' AND NOT EXISTS (SELECT 1 FROM drug WHERE code='D002');

-- Drug batch (if missing)
INSERT INTO drug_batch (drug_id, batch_no, production_date, expire_date, purchase_price, created_at, updated_at)
SELECT d.id, 'B-DA-001', '2023-01-01', '2024-01-01', 2.00, NOW(), NOW()
FROM drug d
WHERE d.code='D002' AND NOT EXISTS (SELECT 1 FROM drug_batch WHERE batch_no='B-DA-001');

-- Stock batch (low stock example)
INSERT INTO stock_batch (drug_id, batch_id, qty, warning_qty, warehouse, updated_at)
SELECT d.id, b.id, 5, 10, 'WH-TEST', NOW()
FROM drug d JOIN drug_batch b ON b.batch_no='B-DA-001' AND b.drug_id=d.id
WHERE NOT EXISTS (SELECT 1 FROM stock_batch sb WHERE sb.batch_id=b.id AND sb.warehouse='WH-TEST');

-- Supplier and purchase sample
INSERT INTO supplier (name, phone, address, created_at, updated_at)
SELECT '供应商B','13900001111','地址B', NOW(), NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM supplier WHERE name='供应商B');

INSERT INTO purchase (purchase_no, supplier_id, purchaser_id, total_amount, status, purchased_at, created_at, updated_at)
SELECT 'P-TEST-001', s.id, u.id, 200.00, 1, NOW(), NOW(), NOW()
FROM supplier s, `user` u
WHERE s.name='供应商B' AND u.username='admin' AND NOT EXISTS (SELECT 1 FROM purchase WHERE purchase_no='P-TEST-001');

INSERT INTO purchase_item (purchase_id, drug_id, batch_id, qty, unit_price, amount, created_at)
SELECT p.id, d.id, b.id, 100, 2.00, 200.00, NOW()
FROM purchase p JOIN drug d ON d.code='D002' JOIN drug_batch b ON b.batch_no='B-DA-001'
WHERE p.purchase_no='P-TEST-001' AND NOT EXISTS (SELECT 1 FROM purchase_item WHERE purchase_id=p.id AND drug_id=d.id);

-- Sale sample (requires seller user)
INSERT INTO customer (name, phone, address, created_at, updated_at)
SELECT '客户B','13800002222','地址C', NOW(), NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM customer WHERE name='客户B');

INSERT INTO sale (sale_no, customer_id, seller_id, total_amount, status, sold_at, created_at, updated_at)
SELECT 'S-TEST-001', c.id, u.id, 30.00, 1, NOW(), NOW(), NOW()
FROM customer c JOIN `user` u ON u.username='seller1'
WHERE c.name='客户B' AND NOT EXISTS (SELECT 1 FROM sale WHERE sale_no='S-TEST-001');

INSERT INTO sale_item (sale_id, drug_id, batch_id, qty, unit_price, amount, created_at)
SELECT s.id, d.id, b.id, 3, 10.00, 30.00, NOW()
FROM sale s JOIN drug d ON d.code='D002' JOIN drug_batch b ON b.batch_no='B-DA-001'
WHERE s.sale_no='S-TEST-001' AND NOT EXISTS (SELECT 1 FROM sale_item WHERE sale_id=s.id AND drug_id=d.id);
