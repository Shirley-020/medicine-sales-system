-- 05_stored_procedures.sql
USE medicine_sales;

-- 示例：增加库存（进货入库后调用）
DROP PROCEDURE IF EXISTS sp_increase_stock;
DELIMITER //
CREATE PROCEDURE sp_increase_stock(IN p_drug_id BIGINT, IN p_batch_id BIGINT, IN p_qty INT)
BEGIN
  INSERT INTO stock_batch(drug_id, batch_id, qty, warning_qty)
  VALUES(p_drug_id, p_batch_id, p_qty, 0)
  ON DUPLICATE KEY UPDATE qty = qty + p_qty;
END //
DELIMITER ;

-- 示例：减少库存（销售出库后调用）
DROP PROCEDURE IF EXISTS sp_decrease_stock;
DELIMITER //
CREATE PROCEDURE sp_decrease_stock(IN p_drug_id BIGINT, IN p_batch_id BIGINT, IN p_qty INT)
BEGIN
  UPDATE stock_batch
  SET qty = qty - p_qty
  WHERE drug_id = p_drug_id AND batch_id = p_batch_id AND qty >= p_qty;

  -- 你也可以加判断：ROW_COUNT()=0 时 SIGNAL 报错
END //
DELIMITER ;
