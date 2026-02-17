-- Set character encoding
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

-- User table
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'User ID',
    `username` VARCHAR(50) NOT NULL COMMENT 'Username',
    `password` VARCHAR(100) NOT NULL COMMENT 'Password',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT 'Nickname',
    `email` VARCHAR(100) DEFAULT NULL COMMENT 'Email',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT 'Phone',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT 'Avatar URL',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT 'Role: 0-Normal User, 1-Admin',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0-Disabled, 1-Enabled',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User Table';

-- Book table
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Book ID',
    `isbn` VARCHAR(20) DEFAULT NULL COMMENT 'ISBN',
    `title` VARCHAR(200) NOT NULL COMMENT 'Book Title',
    `author` VARCHAR(100) DEFAULT NULL COMMENT 'Author',
    `publisher` VARCHAR(100) DEFAULT NULL COMMENT 'Publisher',
    `publish_date` DATE DEFAULT NULL COMMENT 'Publish Date',
    `category` VARCHAR(50) DEFAULT NULL COMMENT 'Category',
    `price` DECIMAL(10,2) DEFAULT NULL COMMENT 'Price',
    `stock` INT NOT NULL DEFAULT 0 COMMENT 'Stock',
    `description` TEXT COMMENT 'Description',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT 'Cover Image URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0-Unavailable, 1-Available',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    PRIMARY KEY (`id`),
    KEY `idx_title` (`title`),
    KEY `idx_author` (`author`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Book Table';

-- Borrow record table
DROP TABLE IF EXISTS `borrow_record`;
CREATE TABLE `borrow_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Record ID',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `book_id` BIGINT NOT NULL COMMENT 'Book ID',
    `borrow_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Borrow Date',
    `due_date` DATETIME DEFAULT NULL COMMENT 'Due Date',
    `return_date` DATETIME DEFAULT NULL COMMENT 'Return Date',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT 'Status: 0-Borrowed, 1-Returned, 2-Overdue',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Borrow Record Table';

-- Insert default admin user (password: admin123, BCrypt encrypted)
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '$2b$12$KEb90x8tFMl4WHJiHjJ.JeL.DxtN2mtmX6YUwSbj8D8BBz.WeviS2', '管理员', 1, 1);

-- Insert default normal user (password: user123, BCrypt encrypted)
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('user', '$2b$12$1IpRHqltmbnYYhAU0K.mCO1EcZ9zN75W1VOdnps02cS3RUAaUi/om', '普通用户', 0, 1);

-- Insert sample books
INSERT INTO `book` (`isbn`, `title`, `author`, `publisher`, `publish_date`, `category`, `price`, `stock`, `description`, `cover_image`) VALUES
('978-7-111-42036-8', 'Java核心技术', '凯·霍斯特曼', '机械工业出版社', '2020-01-15', '编程', 89.00, 10, 'Java编程语言的全面指南，适合初学者和进阶开发者', NULL),
('978-7-111-42037-5', 'Spring实战', '克雷格·沃尔斯', '人民邮电出版社', '2021-03-20', '编程', 79.00, 8, '从零开始学习Spring框架开发', NULL),
('978-7-111-42038-2', 'MySQL必知必会', '本·福达', '人民邮电出版社', '2019-06-10', '数据库', 69.00, 15, 'MySQL数据库快速入门指南', NULL),
('978-7-111-42039-9', 'Python编程从入门到实践', '埃里克·马瑟斯', '人民邮电出版社', '2022-02-28', '编程', 59.00, 12, 'Python编程语言入门教程', NULL),
('978-7-111-42040-5', 'JavaScript高级程序设计', '马特·弗里斯比', '人民邮电出版社', '2021-08-15', '前端', 99.00, 6, 'JavaScript核心技术详解', NULL),
('978-7-111-42041-2', '算法导论', '托马斯·科尔曼', '机械工业出版社', '2020-06-01', '算法', 128.00, 5, '计算机算法的经典教材', NULL),
('978-7-111-42042-9', '深入理解计算机系统', '兰德尔·布莱恩特', '机械工业出版社', '2019-12-15', '计算机', 139.00, 7, '从程序员视角深入理解计算机系统', NULL),
('978-7-111-42043-6', '代码整洁之道', '罗伯特·马丁', '人民邮电出版社', '2021-05-20', '编程', 79.00, 10, '编写可读、可维护代码的艺术', NULL);
