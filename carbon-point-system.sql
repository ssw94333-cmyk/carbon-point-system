-- =============================================
-- 智慧碳积分管理系统数据库脚本
-- 数据库名称: carbon-point-system
-- 创建时间: 2026-01-14
-- =============================================

-- 删除数据库（如果存在）
DROP DATABASE IF EXISTS `carbon-point-system`;

-- 创建数据库
CREATE DATABASE `carbon-point-system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `carbon-point-system`;

-- =============================================
-- 用户表
-- =============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名，唯一',
  `password` VARCHAR(255) NOT NULL COMMENT '密码，加密存储',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号，唯一',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `user_type` INT NOT NULL DEFAULT 0 COMMENT '用户类型，0-普通用户，1-管理员',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态，0-禁用，1-启用',
  `total_points` INT NOT NULL DEFAULT 0 COMMENT '累计积分总额',
  `current_points` INT NOT NULL DEFAULT 0 COMMENT '当前可用积分',
  `total_carbon_reduction` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '累计减碳量，单位：千克',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 碳行为记录表
-- =============================================
DROP TABLE IF EXISTS `carbon_behavior_record`;
CREATE TABLE `carbon_behavior_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `user_id` BIGINT NOT NULL COMMENT '用户ID，外键关联用户表',
  `behavior_type` VARCHAR(50) NOT NULL COMMENT '行为类型',
  `behavior_name` VARCHAR(100) NOT NULL COMMENT '行为名称',
  `behavior_value` DECIMAL(10,2) NOT NULL COMMENT '行为数值',
  `carbon_reduction` DECIMAL(10,2) NOT NULL COMMENT '减碳量，单位：千克',
  `points` INT NOT NULL COMMENT '获得积分',
  `description` TEXT DEFAULT NULL COMMENT '行为描述',
  `proof_image` VARCHAR(500) DEFAULT NULL COMMENT '证明材料图片URL，多个用逗号分隔',
  `audit_status` INT NOT NULL DEFAULT 0 COMMENT '审核状态，0-待审核，1-审核通过，2-审核拒绝',
  `audit_user_id` BIGINT DEFAULT NULL COMMENT '审核人ID，外键关联用户表',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='碳行为记录表';

-- =============================================
-- 行为规则表
-- =============================================
DROP TABLE IF EXISTS `behavior_rule`;
CREATE TABLE `behavior_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `behavior_type` VARCHAR(50) NOT NULL COMMENT '行为类型，唯一',
  `behavior_name` VARCHAR(100) NOT NULL COMMENT '行为名称',
  `unit` VARCHAR(20) NOT NULL COMMENT '单位',
  `carbon_reduction_per_unit` DECIMAL(10,4) NOT NULL COMMENT '每单位减碳量，单位：千克',
  `points_per_unit` INT NOT NULL COMMENT '每单位获得积分',
  `min_value` DECIMAL(10,2) DEFAULT NULL COMMENT '最小数值限制',
  `max_value` DECIMAL(10,2) DEFAULT NULL COMMENT '每日最大数值限制',
  `max_points_per_day` INT DEFAULT NULL COMMENT '每日最大积分限制',
  `need_proof` INT NOT NULL DEFAULT 0 COMMENT '是否需要证明材料，0-不需要，1-需要',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态，0-禁用，1-启用',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `description` TEXT DEFAULT NULL COMMENT '规则说明',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_behavior_type` (`behavior_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行为规则表';

-- =============================================
-- 积分变动记录表
-- =============================================
DROP TABLE IF EXISTS `points_change_record`;
CREATE TABLE `points_change_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `user_id` BIGINT NOT NULL COMMENT '用户ID，外键关联用户表',
  `change_type` INT NOT NULL COMMENT '变动类型，1-获得，2-消费',
  `source_type` VARCHAR(50) NOT NULL COMMENT '来源类型',
  `source_id` BIGINT DEFAULT NULL COMMENT '来源ID，关联具体业务表',
  `points` INT NOT NULL COMMENT '积分变动数量，正数为获得，负数为消费',
  `balance_before` INT NOT NULL COMMENT '变动前积分余额',
  `balance_after` INT NOT NULL COMMENT '变动后积分余额',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注说明',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分变动记录表';

-- =============================================
-- 商品表
-- =============================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `category` VARCHAR(50) NOT NULL COMMENT '商品分类',
  `description` TEXT DEFAULT NULL COMMENT '商品描述',
  `image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片URL，多个用逗号分隔',
  `points_required` INT NOT NULL COMMENT '所需积分',
  `stock` INT NOT NULL COMMENT '库存数量',
  `total_stock` INT NOT NULL COMMENT '总库存数量',
  `exchange_limit` INT NOT NULL DEFAULT 0 COMMENT '每人限兑数量，0表示不限制',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态，0-下架，1-上架',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- =============================================
-- 兑换订单表
-- =============================================
DROP TABLE IF EXISTS `exchange_order`;
CREATE TABLE `exchange_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号，唯一',
  `user_id` BIGINT NOT NULL COMMENT '用户ID，外键关联用户表',
  `product_id` BIGINT NOT NULL COMMENT '商品ID，外键关联商品表',
  `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称，冗余字段',
  `quantity` INT NOT NULL COMMENT '兑换数量',
  `points_required` INT NOT NULL COMMENT '所需积分',
  `total_points` INT NOT NULL COMMENT '总积分',
  `order_status` INT NOT NULL DEFAULT 0 COMMENT '订单状态，0-待处理，1-已发货，2-已完成，3-已取消',
  `receiver_name` VARCHAR(50) DEFAULT NULL COMMENT '收货人姓名，可空',
  `receiver_phone` VARCHAR(20) DEFAULT NULL COMMENT '收货人电话，可空',
  `receiver_address` VARCHAR(500) DEFAULT NULL COMMENT '收货地址，可空',
  `express_company` VARCHAR(50) DEFAULT NULL COMMENT '快递公司',
  `express_no` VARCHAR(50) DEFAULT NULL COMMENT '快递单号',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_status` (`order_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='兑换订单表';

-- =============================================
-- 活动表
-- =============================================
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
  `description` TEXT DEFAULT NULL COMMENT '活动描述',
  `image` VARCHAR(500) DEFAULT NULL COMMENT '活动图片URL，多个用逗号分隔',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `registration_start` DATETIME NOT NULL COMMENT '报名开始时间',
  `registration_end` DATETIME NOT NULL COMMENT '报名结束时间',
  `max_participants` INT NOT NULL DEFAULT 0 COMMENT '最大参与人数，0表示不限制',
  `current_participants` INT NOT NULL DEFAULT 0 COMMENT '当前参与人数',
  `reward_points` INT NOT NULL DEFAULT 0 COMMENT '奖励积分',
  `reward_description` VARCHAR(500) DEFAULT NULL COMMENT '奖励说明',
  `rules` TEXT DEFAULT NULL COMMENT '参与规则',
  `status` INT NOT NULL DEFAULT 0 COMMENT '状态，0-未开始，1-进行中，2-已结束，3-已取消',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- =============================================
-- 活动报名表
-- =============================================
DROP TABLE IF EXISTS `activity_registration`;
CREATE TABLE `activity_registration` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `activity_id` BIGINT NOT NULL COMMENT '活动ID，外键关联活动表',
  `user_id` BIGINT NOT NULL COMMENT '用户ID，外键关联用户表',
  `participation_status` INT NOT NULL DEFAULT 0 COMMENT '参与状态，0-已报名，1-已完成，2-已取消',
  `reward_status` INT NOT NULL DEFAULT 0 COMMENT '奖励状态，0-未发放，1-已发放',
  `points_rewarded` INT NOT NULL DEFAULT 0 COMMENT '已奖励积分',
  `participation_record` TEXT DEFAULT NULL COMMENT '参与记录',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动报名表';

-- =============================================
-- 收货地址表
-- =============================================
DROP TABLE IF EXISTS `shipping_address`;
CREATE TABLE `shipping_address` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `user_id` BIGINT NOT NULL COMMENT '用户ID，外键关联用户表',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
  `province` VARCHAR(50) NOT NULL COMMENT '省份',
  `city` VARCHAR(50) NOT NULL COMMENT '城市',
  `district` VARCHAR(50) NOT NULL COMMENT '区县',
  `detail_address` VARCHAR(500) NOT NULL COMMENT '详细地址',
  `is_default` INT NOT NULL DEFAULT 0 COMMENT '是否默认地址，0-否，1-是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收货地址表';

-- =============================================
-- 插入用户数据
-- =============================================
INSERT INTO `user` (`username`, `password`, `phone`, `nickname`, `avatar`, `email`, `user_type`, `status`, `total_points`, `current_points`, `total_carbon_reduction`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lZzYfY1Ky5Zy8Oy6i', '13800138000', '系统管理员', '/avatar/admin.jpg', 'admin@carbon.com', 1, 1, 0, 0, 0.00),
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lZzYfY1Ky5Zy8Oy6i', '13800138001', '张三', '/avatar/user1.jpg', 'zhangsan@example.com', 0, 1, 1500, 800, 125.50),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lZzYfY1Ky5Zy8Oy6i', '13800138002', '李四', '/avatar/user2.jpg', 'lisi@example.com', 0, 1, 2300, 1200, 185.30),
('wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lZzYfY1Ky5Zy8Oy6i', '13800138003', '王五', '/avatar/user3.jpg', 'wangwu@example.com', 0, 1, 1800, 950, 156.80),
('zhaoliu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lZzYfY1Ky5Zy8Oy6i', '13800138004', '赵六', '/avatar/user4.jpg', 'zhaoliu@example.com', 0, 1, 3200, 1800, 245.60),
('sunqi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lZzYfY1Ky5Zy8Oy6i', '13800138005', '孙七', '/avatar/user5.jpg', 'sunqi@example.com', 0, 1, 980, 450, 78.90),
('zhouba', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lZzYfY1Ky5Zy8Oy6i', '13800138006', '周八', '/avatar/user6.jpg', 'zhouba@example.com', 0, 1, 2650, 1350, 198.40);

-- =============================================
-- 插入行为规则数据
-- =============================================
INSERT INTO `behavior_rule` (`behavior_type`, `behavior_name`, `unit`, `carbon_reduction_per_unit`, `points_per_unit`, `min_value`, `max_value`, `max_points_per_day`, `need_proof`, `status`, `sort_order`, `description`) VALUES
('walking', '步行出行', '公里', 0.1200, 10, 1.00, 20.00, 200, 0, 1, 1, '步行代替开车出行，每公里减少0.12千克碳排放，获得10积分'),
('cycling', '骑行出行', '公里', 0.1500, 15, 1.00, 30.00, 300, 0, 1, 2, '骑行代替开车出行，每公里减少0.15千克碳排放，获得15积分'),
('public_transport', '公交出行', '次', 2.5000, 20, 1.00, 10.00, 200, 0, 1, 3, '乘坐公共交通代替开车，每次减少2.5千克碳排放，获得20积分'),
('garbage_sorting', '垃圾分类', '次', 0.5000, 5, 1.00, 5.00, 25, 1, 1, 4, '正确进行垃圾分类，每次减少0.5千克碳排放，获得5积分，需要上传证明材料'),
('save_electricity', '节约用电', '度', 0.7850, 8, 1.00, 50.00, 400, 1, 1, 5, '节约用电，每度电减少0.785千克碳排放，获得8积分，需要上传电表照片'),
('save_water', '节约用水', '吨', 0.1940, 5, 0.10, 10.00, 50, 1, 1, 6, '节约用水，每吨水减少0.194千克碳排放，获得5积分，需要上传水表照片'),
('reusable_bag', '使用环保袋', '次', 0.0800, 3, 1.00, 5.00, 15, 0, 1, 7, '使用环保袋代替塑料袋，每次减少0.08千克碳排放，获得3积分'),
('paperless_office', '无纸化办公', '次', 0.0500, 2, 1.00, 10.00, 20, 0, 1, 8, '使用电子文档代替纸质文档，每次减少0.05千克碳排放，获得2积分');

-- =============================================
-- 插入碳行为记录数据
-- =============================================
INSERT INTO `carbon_behavior_record` (`user_id`, `behavior_type`, `behavior_name`, `behavior_value`, `carbon_reduction`, `points`, `description`, `proof_image`, `audit_status`, `audit_user_id`, `audit_time`, `audit_remark`) VALUES
(2, 'walking', '步行出行', 5.00, 0.60, 50, '今天步行上班5公里', NULL, 1, 1, '2026-01-13 10:30:00', '审核通过'),
(2, 'public_transport', '公交出行', 2.00, 5.00, 40, '乘坐公交车上下班', NULL, 1, 1, '2026-01-13 11:00:00', '审核通过'),
(3, 'cycling', '骑行出行', 8.00, 1.20, 120, '骑行8公里', NULL, 1, 1, '2026-01-13 09:15:00', '审核通过'),
(3, 'garbage_sorting', '垃圾分类', 3.00, 1.50, 15, '进行垃圾分类', '/proof/garbage1.jpg', 1, 1, '2026-01-13 14:20:00', '审核通过'),
(4, 'save_electricity', '节约用电', 10.00, 7.85, 80, '本月节约用电10度', '/proof/electric1.jpg', 1, 1, '2026-01-12 16:45:00', '审核通过'),
(4, 'walking', '步行出行', 3.00, 0.36, 30, '步行3公里', NULL, 1, 1, '2026-01-13 08:30:00', '审核通过'),
(5, 'cycling', '骑行出行', 12.00, 1.80, 180, '骑行12公里', NULL, 1, 1, '2026-01-12 07:50:00', '审核通过'),
(5, 'public_transport', '公交出行', 4.00, 10.00, 80, '乘坐公交车4次', NULL, 1, 1, '2026-01-13 12:10:00', '审核通过'),
(6, 'reusable_bag', '使用环保袋', 5.00, 0.40, 15, '购物使用环保袋', NULL, 1, 1, '2026-01-13 15:30:00', '审核通过'),
(7, 'walking', '步行出行', 6.00, 0.72, 60, '步行6公里', NULL, 0, NULL, NULL, NULL);

-- =============================================
-- 插入积分变动记录数据
-- =============================================
INSERT INTO `points_change_record` (`user_id`, `change_type`, `source_type`, `source_id`, `points`, `balance_before`, `balance_after`, `remark`) VALUES
(2, 1, '碳行为', 1, 50, 0, 50, '步行出行获得积分'),
(2, 1, '碳行为', 2, 40, 50, 90, '公交出行获得积分'),
(2, 2, '积分兑换', 1, -100, 890, 790, '兑换环保水杯消费积分'),
(3, 1, '碳行为', 3, 120, 0, 120, '骑行出行获得积分'),
(3, 1, '碳行为', 4, 15, 120, 135, '垃圾分类获得积分'),
(3, 2, '积分兑换', 2, -50, 1250, 1200, '兑换优惠券消费积分'),
(4, 1, '碳行为', 5, 80, 0, 80, '节约用电获得积分'),
(4, 1, '碳行为', 6, 30, 80, 110, '步行出行获得积分'),
(5, 1, '碳行为', 7, 180, 0, 180, '骑行出行获得积分'),
(5, 1, '碳行为', 8, 80, 180, 260, '公交出行获得积分');

-- =============================================
-- 插入商品数据
-- =============================================
INSERT INTO `product` (`name`, `category`, `description`, `image`, `points_required`, `stock`, `total_stock`, `exchange_limit`, `status`, `sort_order`) VALUES
('环保水杯', '实物商品', '304不锈钢保温杯，容量500ml，环保材质，可循环使用', '/product/cup1.jpg,/product/cup2.jpg', 100, 50, 100, 2, 1, 1),
('竹纤维毛巾', '实物商品', '天然竹纤维材质，柔软吸水，抗菌防臭', '/product/towel1.jpg', 80, 80, 100, 3, 1, 2),
('环保购物袋', '实物商品', '可折叠便携购物袋，承重20kg，环保耐用', '/product/bag1.jpg', 50, 120, 150, 5, 1, 3),
('10元优惠券', '优惠券', '商城通用优惠券，满50元可用', '/product/coupon1.jpg', 50, 500, 500, 10, 1, 4),
('20元优惠券', '优惠券', '商城通用优惠券，满100元可用', '/product/coupon2.jpg', 100, 300, 300, 5, 1, 5),
('植树公益', '公益捐赠', '为西部地区捐赠一棵树苗，助力绿化环境', '/product/tree1.jpg', 200, 1000, 1000, 0, 1, 6),
('环保台灯', '实物商品', 'LED节能台灯，护眼光源，低碳环保', '/product/lamp1.jpg', 300, 30, 50, 1, 1, 7),
('有机肥料', '实物商品', '天然有机肥料1kg，适合家庭园艺', '/product/fertilizer1.jpg', 150, 60, 80, 2, 1, 8);

-- =============================================
-- 插入兑换订单数据
-- =============================================
INSERT INTO `exchange_order` (`order_no`, `user_id`, `product_id`, `product_name`, `quantity`, `points_required`, `total_points`, `order_status`, `receiver_name`, `receiver_phone`, `receiver_address`, `express_company`, `express_no`) VALUES
('EO202601130001', 2, 1, '环保水杯', 1, 100, 100, 2, '张三', '13800138001', '北京市朝阳区xx街道xx小区1号楼101', '顺丰速运', 'SF1234567890'),
('EO202601130002', 3, 4, '10元优惠券', 1, 50, 50, 2, NULL, NULL, NULL, NULL, NULL),
('EO202601130003', 4, 3, '环保购物袋', 2, 50, 100, 1, '王五', '13800138003', '上海市浦东新区xx路xx号', '中通快递', 'ZTO9876543210'),
('EO202601130004', 5, 6, '植树公益', 1, 200, 200, 2, NULL, NULL, NULL, NULL, NULL),
('EO202601130005', 6, 2, '竹纤维毛巾', 1, 80, 80, 0, '孙七', '13800138005', '广州市天河区xx大道xx号', NULL, NULL),
('EO202601140006', 7, 5, '20元优惠券', 1, 100, 100, 2, NULL, NULL, NULL, NULL, NULL);

-- =============================================
-- 插入活动数据
-- =============================================
INSERT INTO `activity` (`title`, `description`, `image`, `start_time`, `end_time`, `registration_start`, `registration_end`, `max_participants`, `current_participants`, `reward_points`, `reward_description`, `rules`, `status`, `sort_order`) VALUES
('绿色出行周', '倡导绿色出行，连续7天使用公共交通、骑行或步行上下班', '/activity/green_travel.jpg', '2026-01-20 00:00:00', '2026-01-26 23:59:59', '2026-01-14 00:00:00', '2026-01-19 23:59:59', 100, 15, 200, '完成活动可获得200积分奖励', '连续7天记录绿色出行行为，每天至少1次', 0, 1),
('垃圾分类挑战赛', '参与垃圾分类，养成环保好习惯', '/activity/garbage_sorting.jpg', '2026-01-15 00:00:00', '2026-01-31 23:59:59', '2026-01-10 00:00:00', '2026-01-14 23:59:59', 200, 45, 150, '完成活动可获得150积分奖励', '活动期间完成10次垃圾分类记录', 1, 2),
('节能减排月', '节约用电用水，践行低碳生活', '/activity/energy_saving.jpg', '2026-01-01 00:00:00', '2026-01-31 23:59:59', '2025-12-25 00:00:00', '2025-12-31 23:59:59', 0, 128, 300, '完成活动可获得300积分奖励', '活动期间节约用电或用水累计达到50度/吨', 1, 3),
('环保知识竞赛', '学习环保知识，提升环保意识', '/activity/knowledge_contest.jpg', '2026-02-01 10:00:00', '2026-02-01 12:00:00', '2026-01-15 00:00:00', '2026-01-31 23:59:59', 500, 0, 100, '参与答题可获得100积分奖励', '在线答题，答对80%以上题目即可获得奖励', 0, 4),
('植树节活动', '参与植树造林，为地球增添绿色', '/activity/tree_planting.jpg', '2026-03-12 08:00:00', '2026-03-12 17:00:00', '2026-02-20 00:00:00', '2026-03-10 23:59:59', 300, 0, 500, '参与植树可获得500积分奖励', '现场参与植树活动，每人至少种植1棵树', 0, 5);

-- =============================================
-- 插入活动报名数据
-- =============================================
INSERT INTO `activity_registration` (`activity_id`, `user_id`, `participation_status`, `reward_status`, `points_rewarded`, `participation_record`) VALUES
(2, 2, 0, 0, 0, NULL),
(2, 3, 1, 1, 150, '已完成10次垃圾分类记录'),
(2, 4, 0, 0, 0, NULL),
(3, 2, 0, 0, 0, NULL),
(3, 3, 0, 0, 0, NULL),
(3, 4, 1, 1, 300, '已完成节约用电50度'),
(3, 5, 1, 1, 300, '已完成节约用水50吨'),
(3, 6, 0, 0, 0, NULL),
(1, 5, 0, 0, 0, NULL),
(1, 7, 0, 0, 0, NULL);

-- =============================================
-- 插入收货地址数据
-- =============================================
INSERT INTO `shipping_address` (`user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `is_default`) VALUES
(2, '张三', '13800138001', '北京市', '朝阳区', '望京街道', 'xx小区1号楼101室', 1),
(2, '张三', '13800138001', '北京市', '海淀区', '中关村街道', 'xx大厦A座2001室', 0),
(3, '李四', '13800138002', '上海市', '浦东新区', '陆家嘴街道', 'xx路xx号xx室', 1),
(4, '王五', '13800138003', '上海市', '浦东新区', '张江镇', 'xx科技园xx栋xx室', 1),
(5, '赵六', '13800138004', '深圳市', '南山区', '科技园街道', 'xx大厦B座1501室', 1),
(6, '孙七', '13800138005', '广州市', '天河区', '天河北街道', 'xx大道xx号', 1),
(6, '孙七', '13800138005', '广州市', '越秀区', '北京路街道', 'xx广场xx号', 0),
(7, '周八', '13800138006', '杭州市', '西湖区', '文新街道', 'xx小区xx幢xx室', 1);

-- =============================================
-- 数据库创建完成
-- =============================================
