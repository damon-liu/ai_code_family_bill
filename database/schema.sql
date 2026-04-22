-- 家庭记账系统数据库脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS family_bill DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE family_bill;

-- 支付方式表
CREATE TABLE IF NOT EXISTS payment_method (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '支付方式名称（如：微信、支付宝、现金、银行卡）',
    icon VARCHAR(100) COMMENT '图标URL',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1-支出方式，2-收入方式，3-通用',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_type (type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付方式表';

-- 账单分类表
CREATE TABLE IF NOT EXISTS bill_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon VARCHAR(100) COMMENT '图标URL',
    type TINYINT NOT NULL COMMENT '类型：1-支出，2-收入',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_type (type),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单分类表';

-- 账单表
CREATE TABLE IF NOT EXISTS bill (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    payee VARCHAR(100) NOT NULL COMMENT '收款方',
    amount DECIMAL(10, 2) NOT NULL COMMENT '金额（支出为负数，收入为正数）',
    transaction_time DATETIME NOT NULL COMMENT '交易时间',
    payment_method_id BIGINT NOT NULL COMMENT '支付方式ID',
    transaction_no VARCHAR(100) COMMENT '交易号',
    category_id BIGINT COMMENT '分类ID',
    remark VARCHAR(500) COMMENT '备注',
    user_id BIGINT DEFAULT 1 COMMENT '用户ID（预留，支持多用户）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_transaction_time (transaction_time),
    INDEX idx_payment_method_id (payment_method_id),
    INDEX idx_category_id (category_id),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单表';

-- 初始化支付方式数据
INSERT INTO payment_method (name, icon, type, sort_order) VALUES
('微信', 'wechat', 3, 1),
('支付宝', 'alipay', 3, 2),
('现金', 'cash', 3, 3),
('银行卡', 'bank', 3, 4),
('信用卡', 'credit', 1, 5);

-- 初始化账单分类数据（支出）
INSERT INTO bill_category (name, icon, type, parent_id, sort_order) VALUES
('餐饮', 'food', 1, 0, 1),
('交通', 'transport', 1, 0, 2),
('购物', 'shopping', 1, 0, 3),
('娱乐', 'entertainment', 1, 0, 4),
('医疗', 'medical', 1, 0, 5),
('教育', 'education', 1, 0, 6),
('住房', 'housing', 1, 0, 7),
('其他', 'other', 1, 0, 8);

-- 初始化账单分类数据（收入）
INSERT INTO bill_category (name, icon, type, parent_id, sort_order) VALUES
('工资', 'salary', 2, 0, 1),
('奖金', 'bonus', 2, 0, 2),
('投资收益', 'investment', 2, 0, 3),
('兼职', 'parttime', 2, 0, 4),
('其他', 'other', 2, 0, 5);


