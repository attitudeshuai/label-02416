-- User table
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(100) NOT NULL,
    `nickname` VARCHAR(50) DEFAULT NULL,
    `email` VARCHAR(100) DEFAULT NULL,
    `phone` VARCHAR(20) DEFAULT NULL,
    `avatar` VARCHAR(255) DEFAULT NULL,
    `role` TINYINT NOT NULL DEFAULT 0,
    `status` TINYINT NOT NULL DEFAULT 1,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE (`username`)
);

-- Book table
CREATE TABLE IF NOT EXISTS `book` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `isbn` VARCHAR(20) DEFAULT NULL,
    `title` VARCHAR(200) NOT NULL,
    `author` VARCHAR(100) DEFAULT NULL,
    `publisher` VARCHAR(100) DEFAULT NULL,
    `publish_date` DATE DEFAULT NULL,
    `category` VARCHAR(50) DEFAULT NULL,
    `price` DECIMAL(10,2) DEFAULT NULL,
    `stock` INT NOT NULL DEFAULT 0,
    `description` TEXT,
    `cover_image` VARCHAR(255) DEFAULT NULL,
    `status` TINYINT NOT NULL DEFAULT 1,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

-- Borrow record table
CREATE TABLE IF NOT EXISTS `borrow_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `book_id` BIGINT NOT NULL,
    `borrow_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `due_date` TIMESTAMP DEFAULT NULL,
    `return_date` TIMESTAMP DEFAULT NULL,
    `status` TINYINT NOT NULL DEFAULT 0,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

-- Category table
CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(200) DEFAULT NULL,
    `sort` INT NOT NULL DEFAULT 0,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE (`name`)
);
