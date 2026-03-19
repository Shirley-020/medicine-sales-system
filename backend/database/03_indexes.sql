-- 03_indexes.sql
USE medicine_sales;

-- 订单号通常 already unique，不需要再加
-- 临期/效期预警：expire_date + 库存
CREATE INDEX idx_batch_expire_date ON drug_batch (expire_date);
CREATE INDEX idx_stock_qty ON stock_batch (qty);

-- 销售/进货时间范围统计
CREATE INDEX idx_purchase_status_time ON purchase (status, purchased_at);
CREATE INDEX idx_sale_status_time ON sale (status, sold_at);

-- 推荐的额外索引：提高报表与常用查询性能
-- 按销售员与时间查询（按 seller + sold_at 做范围查询）
CREATE INDEX idx_sale_seller_sold_at ON sale (seller_id, sold_at);
-- 按供应商与时间查询（采购报表）
CREATE INDEX idx_purchase_supplier_purchased_at ON purchase (supplier_id, purchased_at);
-- 明细表按药品汇总查询（销售/进货明细）
CREATE INDEX idx_saleitem_drug ON sale_item (drug_id);
CREATE INDEX idx_purchaseitem_drug ON purchase_item (drug_id);
-- 按药品零售价排序/范围查询
CREATE INDEX idx_drug_retail_price ON drug (retail_price);

-- 已存在的索引说明：
-- 1) drug.code 唯一索引用于查找（uk_drug_code）
-- 2) drug.name 有索引用于模糊查询（idx_drug_name）

-- 注意：在高并发写入场景下，索引会增加写入成本，请只为常用查询添加必要的索引并定期评估。

