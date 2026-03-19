-- V1__init_schema.sql - initial schema
CREATE SCHEMA IF NOT EXISTS medicine_sales;
USE medicine_sales;

SET FOREIGN_KEY_CHECKS = 0;

-- role
DROP TABLE IF EXISTS role;
CREATE TABLE role (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_name    VARCHAR(50) NOT NULL,
  description  VARCHAR(255),
  created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_role_name (role_name)
) ENGINE=InnoDB;

-- user
DROP TABLE IF EXISTS user;
CREATE TABLE user (
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  username       VARCHAR(50) NOT NULL,
  password_hash  VARCHAR(255) NOT NULL,
  role_id        BIGINT NOT NULL,
  status         TINYINT NOT NULL DEFAULT 1 COMMENT '1=启用 0=禁用',
  last_login_at  DATETIME NULL,
  created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_username (username),
  KEY idx_user_role_id (role_id),
  CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(id)
) ENGINE=InnoDB;

-- drug_category
DROP TABLE IF EXISTS drug_category;
CREATE TABLE drug_category (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(100) NOT NULL,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_drug_category_name (name)
) ENGINE=InnoDB;

-- supplier
DROP TABLE IF EXISTS supplier;
CREATE TABLE supplier (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(200) NOT NULL,
  phone       VARCHAR(30),
  address     VARCHAR(255),
  license_no  VARCHAR(100),
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_supplier_name (name)
) ENGINE=InnoDB;

-- customer
DROP TABLE IF EXISTS customer;
CREATE TABLE customer (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(200) NOT NULL,
  phone       VARCHAR(30),
  address     VARCHAR(255),
  credit_code VARCHAR(100),
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_customer_name (name)
) ENGINE=InnoDB;

-- drug
DROP TABLE IF EXISTS drug;
CREATE TABLE drug (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  code          VARCHAR(50) NOT NULL COMMENT '药品编码/条码',
  name          VARCHAR(200) NOT NULL,
  category_id   BIGINT NULL,
  spec          VARCHAR(200) COMMENT '规格',
  unit          VARCHAR(50) COMMENT '单位',
  manufacturer  VARCHAR(200),
  approval_no   VARCHAR(100) COMMENT '批准文号',
  retail_price  DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '建议零售价',
  status        TINYINT NOT NULL DEFAULT 1 COMMENT '1=上架 0=下架',
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_drug_code (code),
  KEY idx_drug_name (name),
  KEY idx_drug_category_id (category_id),
  CONSTRAINT fk_drug_category FOREIGN KEY (category_id) REFERENCES drug_category(id)
) ENGINE=InnoDB;

-- drug_batch
DROP TABLE IF EXISTS drug_batch;
CREATE TABLE drug_batch (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  drug_id         BIGINT NOT NULL,
  batch_no        VARCHAR(100) NOT NULL,
  production_date DATE NULL,
  expire_date     DATE NOT NULL,
  purchase_price  DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '进价（可按批次）',
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_drug_batch (drug_id, batch_no),
  KEY idx_batch_drug_expire (drug_id, expire_date),
  CONSTRAINT fk_batch_drug FOREIGN KEY (drug_id) REFERENCES drug(id)
) ENGINE=InnoDB;

-- stock_batch
DROP TABLE IF EXISTS stock_batch;
CREATE TABLE stock_batch (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  drug_id      BIGINT NOT NULL,
  batch_id     BIGINT NOT NULL,
  qty          INT NOT NULL DEFAULT 0 COMMENT '库存数量',
  warning_qty  INT NOT NULL DEFAULT 0 COMMENT '低库存阈值',
  warehouse    VARCHAR(100) NULL COMMENT '仓库/库位(可选)',
  created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_stock_drug_batch (drug_id, batch_id),
  KEY idx_stock_drug (drug_id),
  KEY idx_stock_batch (batch_id),
  CONSTRAINT fk_stock_drug  FOREIGN KEY (drug_id) REFERENCES drug(id),
  CONSTRAINT fk_stock_batch FOREIGN KEY (batch_id) REFERENCES drug_batch(id)
) ENGINE=InnoDB;

-- purchase
DROP TABLE IF EXISTS purchase;
CREATE TABLE purchase (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  purchase_no   VARCHAR(50) NOT NULL,
  supplier_id   BIGINT NOT NULL,
  purchaser_id  BIGINT NOT NULL COMMENT '经办人(user.id)',
  total_amount  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  status        TINYINT NOT NULL DEFAULT 1 COMMENT '1=已入库 0=草稿/作废(按你们定义)',
  purchased_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_purchase_no (purchase_no),
  KEY idx_purchase_supplier (supplier_id),
  KEY idx_purchase_purchased_at (purchased_at),
  CONSTRAINT fk_purchase_supplier  FOREIGN KEY (supplier_id) REFERENCES supplier(id),
  CONSTRAINT fk_purchase_purchaser FOREIGN KEY (purchaser_id) REFERENCES user(id)
) ENGINE=InnoDB;

-- purchase_item
DROP TABLE IF EXISTS purchase_item;
CREATE TABLE purchase_item (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  purchase_id  BIGINT NOT NULL,
  drug_id      BIGINT NOT NULL,
  batch_id     BIGINT NOT NULL,
  qty          INT NOT NULL,
  unit_price   DECIMAL(10,2) NOT NULL,
  amount       DECIMAL(10,2) NOT NULL,
  created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  KEY idx_pi_purchase (purchase_id),
  KEY idx_pi_drug (drug_id),
  KEY idx_pi_batch (batch_id),
  CONSTRAINT fk_pi_purchase FOREIGN KEY (purchase_id) REFERENCES purchase(id) ON DELETE CASCADE,
  CONSTRAINT fk_pi_drug     FOREIGN KEY (drug_id) REFERENCES drug(id),
  CONSTRAINT fk_pi_batch    FOREIGN KEY (batch_id) REFERENCES drug_batch(id)
) ENGINE=InnoDB;

-- sale
DROP TABLE IF EXISTS sale;
CREATE TABLE sale (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  sale_no       VARCHAR(50) NOT NULL,
  customer_id   BIGINT NOT NULL,
  seller_id     BIGINT NOT NULL COMMENT '销售员(user.id)',
  total_amount  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  status        TINYINT NOT NULL DEFAULT 1 COMMENT '1=已出库 0=草稿/作废(按你们定义)',
  sold_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_sale_no (sale_no),
  KEY idx_sale_customer (customer_id),
  KEY idx_sale_sold_at (sold_at),
  CONSTRAINT fk_sale_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
  CONSTRAINT fk_sale_seller   FOREIGN KEY (seller_id) REFERENCES user(id)
) ENGINE=InnoDB;

-- sale_item
DROP TABLE IF EXISTS sale_item;
CREATE TABLE sale_item (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  sale_id    BIGINT NOT NULL,
  drug_id    BIGINT NOT NULL,
  batch_id   BIGINT NOT NULL,
  qty        INT NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  amount     DECIMAL(10,2) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  KEY idx_si_sale (sale_id),
  KEY idx_si_drug (drug_id),
  KEY idx_si_batch (batch_id),
  CONSTRAINT fk_si_sale  FOREIGN KEY (sale_id) REFERENCES sale(id) ON DELETE CASCADE,
  CONSTRAINT fk_si_drug  FOREIGN KEY (drug_id) REFERENCES drug(id),
  CONSTRAINT fk_si_batch FOREIGN KEY (batch_id) REFERENCES drug_batch(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;