-- V2__add_indexes.sql - add recommended indexes for reporting and common queries
USE medicine_sales;

-- Sales and purchase time-based composite indexes
-- Create index only if missing using INFORMATION_SCHEMA check
SET @exists = (SELECT COUNT(1) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='medicine_sales' AND TABLE_NAME='sale' AND INDEX_NAME='idx_sale_seller_sold_at');
SET @sql = IF(@exists=0, 'CREATE INDEX idx_sale_seller_sold_at ON sale (seller_id, sold_at)', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @exists = (SELECT COUNT(1) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='medicine_sales' AND TABLE_NAME='purchase' AND INDEX_NAME='idx_purchase_supplier_purchased_at');
SET @sql = IF(@exists=0, 'CREATE INDEX idx_purchase_supplier_purchased_at ON purchase (supplier_id, purchased_at)', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Detail table indexes for faster aggregation by drug
SET @exists = (SELECT COUNT(1) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='medicine_sales' AND TABLE_NAME='sale_item' AND INDEX_NAME='idx_saleitem_drug');
SET @sql = IF(@exists=0, 'CREATE INDEX idx_saleitem_drug ON sale_item (drug_id)', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @exists = (SELECT COUNT(1) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='medicine_sales' AND TABLE_NAME='purchase_item' AND INDEX_NAME='idx_purchaseitem_drug');
SET @sql = IF(@exists=0, 'CREATE INDEX idx_purchaseitem_drug ON purchase_item (drug_id)', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Pricing index for range queries and order by
SET @exists = (SELECT COUNT(1) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='medicine_sales' AND TABLE_NAME='drug' AND INDEX_NAME='idx_drug_retail_price');
SET @sql = IF(@exists=0, 'CREATE INDEX idx_drug_retail_price ON drug (retail_price)', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Batch expiry and stock indexes (reinforced)
SET @exists = (SELECT COUNT(1) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='medicine_sales' AND TABLE_NAME='drug_batch' AND INDEX_NAME='idx_batch_expire_date');
SET @sql = IF(@exists=0, 'CREATE INDEX idx_batch_expire_date ON drug_batch (expire_date)', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @exists = (SELECT COUNT(1) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='medicine_sales' AND TABLE_NAME='stock_batch' AND INDEX_NAME='idx_stock_qty');
SET @sql = IF(@exists=0, 'CREATE INDEX idx_stock_qty ON stock_batch (qty)', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
