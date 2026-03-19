-- Adminer 5.4.1 MySQL 8.0.44 dump

SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

SET NAMES utf8mb4;

DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `credit_code` varchar(100) DEFAULT NULL,
  `name` varchar(200) NOT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `customer` (`id`, `address`, `created_at`, `credit_code`, `name`, `phone`, `updated_at`) VALUES
(1,	'北京市朝阳区建国路88号',	'2025-12-27 19:23:13.281368',	'9111010878543210X',	'北京协和医院药房',	'010-65296114',	'2025-12-27 19:23:13.281368'),
(2,	'上海市徐汇区枫林路180号',	'2025-12-27 19:23:13.281368',	'9131010478745678A',	'上海瑞金医院采购中心',	'021-64370045',	'2025-12-27 19:23:13.281368'),
(3,	'广州市越秀区中山二路106号',	'2025-12-27 19:23:13.281368',	'9144010178456321B',	'广东省人民医院药剂科',	'020-83827812',	'2025-12-27 19:23:13.281368'),
(4,	'成都市武侯区国学巷37号',	'2025-12-27 19:23:13.281368',	'9151010778321456C',	'华西医院药学部',	'028-85422333',	'2025-12-27 19:23:13.281368'),
(5,	'散客',	'2025-12-27 22:15:42.919206',	NULL,	'散客',	'00000000000',	'2025-12-27 22:15:42.919206');

DROP TABLE IF EXISTS `drug`;
CREATE TABLE `drug` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `approval_no` varchar(100) DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `manufacturer` varchar(200) DEFAULT NULL,
  `name` varchar(200) NOT NULL,
  `retail_price` decimal(38,2) NOT NULL,
  `spec` varchar(200) DEFAULT NULL,
  `status` tinyint NOT NULL,
  `unit` varchar(50) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `category_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKgp2v51p5n48ip1i7dl9ejujhh` (`code`),
  KEY `FKdgg9cd5it9clrlyhbaf92ct5o` (`category_id`),
  CONSTRAINT `FKdgg9cd5it9clrlyhbaf92ct5o` FOREIGN KEY (`category_id`) REFERENCES `drug_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `drug` (`id`, `approval_no`, `code`, `created_at`, `manufacturer`, `name`, `retail_price`, `spec`, `status`, `unit`, `updated_at`, `category_id`) VALUES
(1,	'国药准字H20058762',	'AMXL',	'2025-12-27 16:57:52.441456',	'华北制药',	'阿莫西林胶囊',	25.50,	'0.25g*24粒',	1,	'盒',	'2025-12-27 16:57:52.441456',	1),
(2,	'国药准字H20058763',	'TBKWP',	'2025-12-27 16:57:52.441456',	'广州白云山',	'头孢克肟片',	38.00,	'0.1g*12片',	1,	'盒',	'2025-12-27 16:57:52.441456',	1),
(3,	'国药准字H20058764',	'XBDPKSP',	'2025-12-27 16:57:52.441456',	'拜耳医药',	'硝苯地平控释片',	30.00,	'30mg*7片',	1,	'盒',	'2025-12-27 16:57:52.441456',	2),
(4,	'国药准字H20058765',	'ATFTTGP',	'2025-12-27 16:57:52.441456',	'辉瑞制药',	'阿托伐他汀钙片',	45.00,	'20mg*7片',	1,	'盒',	'2025-12-27 16:57:52.441456',	2),
(5,	'国药准字H20058766',	'OMLZ',	'2025-12-27 16:57:52.441456',	'阿斯利康',	'奥美拉唑肠溶胶囊',	28.00,	'20mg*14粒',	1,	'盒',	'2025-12-27 16:57:52.441456',	3),
(6,	'国药准字H20058767',	'WSSJN',	'2025-12-27 16:57:52.441456',	'养生堂',	'维生素C片',	10.00,	'100mg*60片',	1,	'瓶',	'2025-12-27 16:57:52.441456',	4),
(7,	'国药准字H20058768',	'YDG',	'2025-12-27 16:57:52.441456',	'稳健医疗',	'碘伏消毒液',	8.50,	'100ml',	1,	'瓶',	'2025-12-27 16:57:52.441456',	5);

DROP TABLE IF EXISTS `drug_batch`;
CREATE TABLE `drug_batch` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `batch_no` varchar(100) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `expire_date` date NOT NULL,
  `production_date` date DEFAULT NULL,
  `purchase_price` decimal(38,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `drug_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsv4dbnteu70id37mjbsc2rygp` (`drug_id`),
  CONSTRAINT `FKsv4dbnteu70id37mjbsc2rygp` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `drug_batch` (`id`, `batch_no`, `created_at`, `expire_date`, `production_date`, `purchase_price`, `updated_at`, `drug_id`) VALUES
(1,	'AMX-20231101',	'2025-12-27 17:12:00.040119',	'2027-11-01',	'2025-11-01',	18.50,	'2025-12-27 17:12:00.040119',	1),
(2,	'AMX-20240215',	'2025-12-27 17:12:00.040119',	'2026-08-15',	'2025-02-15',	19.20,	'2025-12-27 17:12:00.040119',	1),
(3,	'TBK-20231020',	'2025-12-27 17:12:00.040119',	'2027-10-20',	'2025-10-20',	28.00,	'2025-12-27 17:12:00.040119',	2),
(4,	'TBK-20240110',	'2025-12-27 17:12:00.040119',	'2026-07-10',	'2025-01-10',	29.50,	'2025-12-27 17:12:00.040119',	2),
(5,	'XBD-20231205',	'2025-12-27 17:12:00.040119',	'2027-12-05',	'2025-12-05',	22.00,	'2025-12-27 17:12:00.040119',	3),
(6,	'ATF-20231115',	'2025-12-27 17:12:00.040119',	'2027-11-15',	'2025-11-15',	35.00,	'2025-12-27 17:12:00.040119',	4),
(7,	'ATF-20240301',	'2025-12-27 17:12:00.040119',	'2026-09-01',	'2025-03-01',	36.50,	'2025-12-27 17:12:00.040119',	4),
(8,	'OML-20231025',	'2025-12-27 17:12:00.040119',	'2027-10-25',	'2025-10-25',	20.00,	'2025-12-27 17:12:00.040119',	5),
(9,	'WSS-20240120',	'2025-12-27 17:12:00.040119',	'2026-07-20',	'2025-01-20',	8.00,	'2025-12-27 17:12:00.040119',	6),
(10,	'WSS-20231210',	'2025-12-27 17:12:00.040119',	'2026-01-10',	'2024-12-10',	7.50,	'2025-12-27 17:12:00.040119',	6),
(11,	'YDG-20231215',	'2025-12-27 17:12:00.040119',	'2025-12-15',	'2023-12-15',	5.00,	'2025-12-27 17:12:00.040119',	7),
(16,	'AMD-20251227001',	'2025-12-27 23:56:24.340843',	'2027-12-01',	'2025-12-01',	38.00,	'2025-12-27 23:56:24.340843',	2),
(17,	'AMD-20251227001',	'2025-12-28 00:01:49.514649',	'2027-12-10',	'2025-12-10',	45.00,	'2025-12-28 00:01:49.514649',	4);

DROP TABLE IF EXISTS `drug_category`;
CREATE TABLE `drug_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `name` varchar(100) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKho4qtpgd18h5xejv3ihitdeip` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `drug_category` (`id`, `created_at`, `name`, `updated_at`) VALUES
(1,	'2025-12-27 16:57:38.978215',	'抗生素',	'2025-12-27 16:57:38.978215'),
(2,	'2025-12-27 16:57:38.978215',	'心脑血管',	'2025-12-27 16:57:38.978215'),
(3,	'2025-12-27 16:57:38.978215',	'消化系统',	'2025-12-27 16:57:38.978215'),
(4,	'2025-12-27 16:57:38.978215',	'维生素',	'2025-12-27 16:57:38.978215'),
(5,	'2025-12-27 16:57:38.978215',	'外用药',	'2025-12-27 16:57:38.978215');

DROP TABLE IF EXISTS `purchase`;
CREATE TABLE `purchase` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `purchase_no` varchar(50) NOT NULL,
  `purchased_at` datetime(6) NOT NULL,
  `status` tinyint NOT NULL,
  `total_amount` decimal(38,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `purchaser_id` bigint NOT NULL,
  `supplier_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKqcecijgurux4tq1hsh3xqy60c` (`purchase_no`),
  KEY `FKaa3wwdr757ccs9pj6m0f52u4b` (`purchaser_id`),
  KEY `FK8omm6fki86s9oqk0o9s6w43h5` (`supplier_id`),
  CONSTRAINT `FK8omm6fki86s9oqk0o9s6w43h5` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`),
  CONSTRAINT `FKaa3wwdr757ccs9pj6m0f52u4b` FOREIGN KEY (`purchaser_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `purchase` (`id`, `created_at`, `purchase_no`, `purchased_at`, `status`, `total_amount`, `updated_at`, `purchaser_id`, `supplier_id`) VALUES
(12,	'2025-12-27 19:38:36.000000',	'PUR-20251227-001',	'2025-12-27 19:38:36.000000',	2,	3910.00,	'2025-12-27 23:59:41.618323',	4,	1),
(15,	'2025-12-27 19:43:04.000000',	'PUR-20251227-002',	'2025-12-27 19:43:04.000000',	0,	3910.00,	'2025-12-28 00:00:12.658971',	4,	1),
(16,	'2025-12-27 23:56:24.340843',	'P-20251227235624',	'2025-12-27 00:00:00.000000',	1,	38610.00,	'2025-12-27 23:56:24.340843',	1,	2),
(17,	'2025-12-28 00:01:49.514649',	'P-20251228000149',	'2025-12-27 00:00:00.000000',	2,	23475.00,	'2025-12-28 00:02:32.103448',	5,	4);

DROP TABLE IF EXISTS `purchase_item`;
CREATE TABLE `purchase_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `qty` int NOT NULL,
  `unit_price` decimal(38,2) NOT NULL,
  `batch_id` bigint NOT NULL,
  `drug_id` bigint NOT NULL,
  `purchase_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKd42ibau2uchbdrylgqhb04b2j` (`batch_id`),
  KEY `FKnix9p4qdliq9pu7oxuufxe1pc` (`drug_id`),
  KEY `FK1mncc5yaore1sibgpj3jc4a7u` (`purchase_id`),
  CONSTRAINT `FK1mncc5yaore1sibgpj3jc4a7u` FOREIGN KEY (`purchase_id`) REFERENCES `purchase` (`id`),
  CONSTRAINT `FKd42ibau2uchbdrylgqhb04b2j` FOREIGN KEY (`batch_id`) REFERENCES `drug_batch` (`id`),
  CONSTRAINT `FKnix9p4qdliq9pu7oxuufxe1pc` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `purchase_item` (`id`, `amount`, `created_at`, `qty`, `unit_price`, `batch_id`, `drug_id`, `purchase_id`) VALUES
(36,	1850.00,	'2025-12-27 19:44:05.000000',	100,	18.50,	1,	1,	12),
(37,	1400.00,	'2025-12-27 19:44:05.000000',	50,	28.00,	3,	2,	12),
(38,	660.00,	'2025-12-27 19:44:05.000000',	30,	22.00,	5,	3,	12),
(39,	42560.00,	'2025-12-27 23:56:24.340843',	1000,	38.00,	16,	2,	16),
(40,	25425.00,	'2025-12-28 00:01:49.514649',	500,	45.00,	17,	4,	17);

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `role_name` varchar(50) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKiubw515ff0ugtm28p8g3myt0h` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `role` (`id`, `created_at`, `description`, `role_name`, `updated_at`) VALUES
(1,	'2025-12-27 07:35:50.092167',	NULL,	'SYSTEM',	'2025-12-27 07:35:50.092707'),
(2,	'2025-12-27 15:59:54.000000',	NULL,	'admin',	'2025-12-27 15:59:54.000000'),
(3,	'2025-12-27 19:25:31.678751',	'负责药品采购工作',	'采购员',	'2025-12-27 19:25:31.678751'),
(4,	'2025-12-27 19:25:31.678751',	'负责药品销售工作',	'销售员',	'2025-12-27 19:25:31.678751'),
(5,	'2025-12-27 19:25:31.678751',	'系统管理员',	'系统管理员',	'2025-12-27 19:25:31.678751'),
(6,	'2025-12-27 19:25:31.678751',	'负责库存管理',	'仓库管理员',	'2025-12-27 19:25:31.678751');

DROP TABLE IF EXISTS `sale`;
CREATE TABLE `sale` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `sale_no` varchar(50) NOT NULL,
  `sold_at` datetime(6) NOT NULL,
  `status` tinyint NOT NULL,
  `total_amount` decimal(38,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `customer_id` bigint NOT NULL,
  `seller_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK79ejm9vjcn0om389j87ddiqoi` (`sale_no`),
  KEY `FKjw88ojfoqquyd9f1obip1ar0g` (`customer_id`),
  KEY `FKifik8a9kgtivlcsgrdy712j8` (`seller_id`),
  CONSTRAINT `FKifik8a9kgtivlcsgrdy712j8` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKjw88ojfoqquyd9f1obip1ar0g` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `sale` (`id`, `created_at`, `sale_no`, `sold_at`, `status`, `total_amount`, `updated_at`, `customer_id`, `seller_id`) VALUES
(1,	'2025-12-27 22:02:27.763699',	'XS-20231220001',	'2023-12-20 10:30:00.000000',	2,	4900.00,	'2025-12-27 22:02:27.763699',	1,	5),
(2,	'2025-12-27 22:02:27.763699',	'XS-20231220002',	'2023-12-20 14:15:00.000000',	2,	3800.00,	'2025-12-27 22:02:27.763699',	2,	6),
(3,	'2025-12-27 22:02:27.763699',	'XS-20231221001',	'2023-12-21 09:45:00.000000',	1,	2250.00,	'2025-12-27 22:02:27.763699',	3,	5),
(4,	'2025-12-27 22:02:27.763699',	'XS-20231221002',	'2023-12-21 16:20:00.000000',	0,	1680.00,	'2025-12-27 22:02:27.763699',	4,	6),
(5,	'2025-12-27 22:02:27.763699',	'XS-20231222001',	'2023-12-22 11:10:00.000000',	2,	3150.00,	'2025-12-27 22:02:27.763699',	1,	5),
(7,	'2025-12-27 22:15:42.985955',	'SALE-20251227221542',	'2025-12-27 22:15:42.985955',	1,	8.50,	'2025-12-27 22:15:42.985955',	5,	1),
(8,	'2025-12-27 22:24:56.157490',	'SALE-20251227222456',	'2025-12-27 22:24:56.157490',	2,	150.00,	'2025-12-27 23:11:27.925249',	5,	1),
(9,	'2025-12-27 22:30:34.468949',	'SALE-20251227223034',	'2025-12-27 22:30:34.468949',	1,	255.00,	'2025-12-27 22:30:34.468949',	5,	7),
(10,	'2025-12-28 00:27:22.630867',	'SALE-20251228002722',	'2025-12-28 00:27:22.630867',	1,	2800.00,	'2025-12-28 00:27:22.630867',	1,	8),
(11,	'2025-12-28 00:30:37.371450',	'SALE-20251228003037',	'2025-12-28 00:30:37.371450',	1,	127.50,	'2025-12-28 00:30:37.371450',	5,	7);

DROP TABLE IF EXISTS `sale_item`;
CREATE TABLE `sale_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `qty` int NOT NULL,
  `unit_price` decimal(38,2) NOT NULL,
  `batch_id` bigint NOT NULL,
  `drug_id` bigint NOT NULL,
  `sale_id` bigint NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa7l9tgcxnc833yxwjflxg9rgk` (`batch_id`),
  KEY `FKgo6drxo2m7i3wbm3r3umnitob` (`drug_id`),
  KEY `FKar9qqr4n69xw1shum20oflleo` (`sale_id`),
  CONSTRAINT `FKa7l9tgcxnc833yxwjflxg9rgk` FOREIGN KEY (`batch_id`) REFERENCES `drug_batch` (`id`),
  CONSTRAINT `FKar9qqr4n69xw1shum20oflleo` FOREIGN KEY (`sale_id`) REFERENCES `sale` (`id`),
  CONSTRAINT `FKgo6drxo2m7i3wbm3r3umnitob` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `sale_item` (`id`, `amount`, `created_at`, `qty`, `unit_price`, `batch_id`, `drug_id`, `sale_id`, `updated_at`) VALUES
(1,	510.00,	'2025-12-27 22:04:11.621310',	20,	25.50,	1,	1,	1,	'2025-12-27 22:04:11.621310'),
(2,	570.00,	'2025-12-27 22:04:11.621310',	15,	38.00,	3,	2,	1,	'2025-12-27 22:04:11.621310'),
(3,	240.00,	'2025-12-27 22:04:11.621310',	8,	30.00,	5,	3,	1,	'2025-12-27 22:04:11.621310'),
(4,	250.00,	'2025-12-27 22:04:11.621310',	25,	10.00,	9,	6,	1,	'2025-12-27 22:04:11.621310'),
(5,	540.00,	'2025-12-27 22:04:11.621310',	12,	45.00,	6,	4,	2,	'2025-12-27 22:04:11.621310'),
(6,	504.00,	'2025-12-27 22:04:11.621310',	18,	28.00,	8,	5,	2,	'2025-12-27 22:04:11.621310'),
(7,	85.00,	'2025-12-27 22:04:11.621310',	10,	8.50,	11,	7,	2,	'2025-12-27 22:04:11.621310'),
(8,	240.00,	'2025-12-27 22:04:11.621310',	8,	30.00,	5,	3,	3,	'2025-12-27 22:04:11.621310'),
(9,	225.00,	'2025-12-27 22:04:11.621310',	5,	45.00,	6,	4,	3,	'2025-12-27 22:04:11.621310'),
(10,	336.00,	'2025-12-27 22:04:11.621310',	12,	28.00,	8,	5,	3,	'2025-12-27 22:04:11.621310'),
(11,	382.50,	'2025-12-27 22:04:11.621310',	15,	25.50,	1,	1,	4,	'2025-12-27 22:04:11.621310'),
(12,	380.00,	'2025-12-27 22:04:11.621310',	10,	38.00,	3,	2,	4,	'2025-12-27 22:04:11.621310'),
(13,	270.00,	'2025-12-27 22:04:11.621310',	6,	45.00,	6,	4,	4,	'2025-12-27 22:04:11.621310'),
(14,	34.00,	'2025-12-27 22:04:11.621310',	4,	8.50,	11,	7,	4,	'2025-12-27 22:04:11.621310'),
(15,	637.50,	'2025-12-27 22:04:11.621310',	25,	25.50,	1,	1,	5,	'2025-12-27 22:04:11.621310'),
(16,	760.00,	'2025-12-27 22:04:11.621310',	20,	38.00,	3,	2,	5,	'2025-12-27 22:04:11.621310'),
(17,	360.00,	'2025-12-27 22:04:11.621310',	12,	30.00,	5,	3,	5,	'2025-12-27 22:04:11.621310'),
(18,	675.00,	'2025-12-27 22:04:11.621310',	15,	45.00,	6,	4,	5,	'2025-12-27 22:04:11.621310'),
(19,	224.00,	'2025-12-27 22:04:11.621310',	8,	28.00,	8,	5,	5,	'2025-12-27 22:04:11.621310'),
(20,	8.50,	'2025-12-27 22:15:43.015863',	1,	8.50,	11,	7,	7,	'2025-12-27 22:15:43.015863'),
(21,	150.00,	'2025-12-27 22:24:56.253051',	5,	30.00,	5,	3,	8,	'2025-12-27 22:24:56.253051'),
(22,	255.00,	'2025-12-27 22:30:34.590362',	10,	25.50,	2,	1,	9,	'2025-12-27 22:30:34.590362'),
(23,	2800.00,	'2025-12-28 00:27:22.688177',	100,	28.00,	8,	5,	10,	'2025-12-28 00:27:22.688177'),
(24,	127.50,	'2025-12-28 00:30:37.392754',	5,	25.50,	2,	1,	11,	'2025-12-28 00:30:37.392754');

DROP TABLE IF EXISTS `stock_batch`;
CREATE TABLE `stock_batch` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `qty` int NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `warehouse` varchar(100) DEFAULT NULL,
  `warning_qty` int NOT NULL,
  `batch_id` bigint NOT NULL,
  `drug_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsu2pj779w7swmyoe1xykddfyn` (`batch_id`),
  KEY `FKjbbp47hole3ntykyx4d3rjf3q` (`drug_id`),
  CONSTRAINT `FKjbbp47hole3ntykyx4d3rjf3q` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`id`),
  CONSTRAINT `FKsu2pj779w7swmyoe1xykddfyn` FOREIGN KEY (`batch_id`) REFERENCES `drug_batch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `stock_batch` (`id`, `qty`, `updated_at`, `warehouse`, `warning_qty`, `batch_id`, `drug_id`) VALUES
(1,	260,	'2025-12-27 23:59:41.561290',	'主仓库-1区',	20,	1,	1),
(2,	710,	'2025-12-27 18:46:25.670925',	'主仓库-1区',	15,	2,	1),
(3,	170,	'2025-12-27 23:59:41.608130',	'主仓库-1区',	25,	3,	2),
(4,	61,	'2025-12-27 19:17:10.570974',	'主仓库-1区',	10,	4,	2),
(5,	165,	'2025-12-27 23:59:41.617760',	'主仓库-1区',	20,	5,	3),
(6,	110,	'2025-12-27 17:12:12.693763',	'主仓库-2区',	15,	6,	4),
(7,	75,	'2025-12-27 17:12:12.693763',	'主仓库-2区',	10,	7,	4),
(8,	30,	'2025-12-27 17:12:12.693763',	'主仓库-2区',	30,	8,	5),
(9,	200,	'2025-12-27 17:12:12.693763',	'主仓库-2区',	50,	9,	6),
(10,	150,	'2025-12-27 17:12:12.693763',	'主仓库-2区',	40,	10,	6),
(11,	79,	'2025-12-27 17:12:12.693763',	'主仓库-2区',	20,	11,	7),
(12,	95,	'2025-12-27 20:59:36.742854',	'备用仓库-A区',	10,	1,	1),
(13,	5,	'2025-12-27 21:27:27.344166',	'备用仓库-A区',	5,	5,	3),
(14,	100,	'2025-12-27 21:39:26.219268',	'备用仓库-B区',	25,	9,	6),
(15,	75,	'2025-12-27 20:58:44.522689',	'备用仓库-B区',	10,	11,	7),
(16,	565,	'2025-12-27 21:04:11.220440',	'WH-TARGET',	10,	4,	2),
(17,	1000,	'2025-12-27 23:56:24.340843',	'主仓库',	0,	16,	2),
(18,	1000,	'2025-12-28 00:02:32.103448',	'主仓库',	0,	17,	4);

DROP TABLE IF EXISTS `stock_warning`;
CREATE TABLE `stock_warning` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `last_reminded_at` datetime(6) DEFAULT NULL,
  `message` varchar(500) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `warning_type` varchar(255) DEFAULT NULL,
  `drug_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrql3l312c5nyqn4tkmxf4cap2` (`drug_id`),
  CONSTRAINT `FKrql3l312c5nyqn4tkmxf4cap2` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `license_no` varchar(100) DEFAULT NULL,
  `name` varchar(200) NOT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `supplier` (`id`, `address`, `created_at`, `license_no`, `name`, `phone`, `updated_at`) VALUES
(1,	'江苏省南京市鼓楼区中山路321号',	'2025-12-27 19:23:13.286329',	'苏A12345678',	'南京医药股份有限公司',	'025-83151111',	'2025-12-27 19:23:13.286329'),
(2,	'浙江省杭州市西湖区文三路568号',	'2025-12-27 19:23:13.286329',	'浙B87654321',	'杭州民生药业集团',	'0571-87218888',	'2025-12-27 19:23:13.286329'),
(3,	'天津市滨海新区泰达开发区',	'2025-12-27 19:23:13.286329',	'津C12349876',	'天津天士力制药',	'022-26736666',	'2025-12-27 19:23:13.286329'),
(4,	'吉林省通化市东昌区修正路36号',	'2025-12-27 19:23:13.286329',	'吉D87651234',	'修正药业集团',	'0435-3948888',	'2025-12-27 19:23:13.286329');

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `last_login_at` datetime(6) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `status` tinyint NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `username` varchar(50) NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  KEY `FK4qu1gr772nnf6ve5af002rwya` (`role_id`),
  CONSTRAINT `FK4qu1gr772nnf6ve5af002rwya` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `users` (`id`, `created_at`, `last_login_at`, `password_hash`, `status`, `updated_at`, `username`, `role_id`) VALUES
(1,	'2025-12-27 07:35:50.229451',	NULL,	'$2a$10$FuVmgiISOXMxDcF6edw5LeMeBWTfi6YR5HB1sLXQClycOMRhOeb9e',	1,	'2025-12-27 07:35:50.229451',	'system',	1),
(4,	'2025-12-27 16:02:25.216592',	NULL,	'$2a$10$FuVmgiISOXMxDcF6edw5LeMeBWTfi6YR5HB1sLXQClycOMRhOeb9e',	1,	'2025-12-27 16:02:25.216592',	'admin',	2),
(5,	'2025-12-27 19:26:59.711413',	NULL,	'$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',	1,	'2025-12-27 19:26:59.711413',	'张小三',	3),
(6,	'2025-12-27 19:26:59.711413',	NULL,	'$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',	1,	'2025-12-27 19:26:59.711413',	'李小四',	3),
(7,	'2025-12-27 19:26:59.711413',	NULL,	'$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',	1,	'2025-12-27 19:26:59.711413',	'王小五',	4),
(8,	'2025-12-27 19:26:59.711413',	NULL,	'$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',	1,	'2025-12-27 19:26:59.711413',	'赵小六',	4),
(9,	'2025-12-27 00:33:08.000000',	NULL,	'$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',	1,	'2025-12-27 00:33:08.000000',	'小小黑',	6);

DROP TABLE IF EXISTS `warning_template`;
CREATE TABLE `warning_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `actions` text,
  `created_at` datetime(6) DEFAULT NULL,
  `name` varchar(200) NOT NULL,
  `rules` text,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 2025-12-27 18:35:51 UTC

