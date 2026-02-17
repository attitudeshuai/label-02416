package com.library.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书实体类
 * 对应数据库book表
 */
@Data
public class Book {
    private Long id;                    // 图书ID
    private String isbn;                // ISBN编号
    private String title;               // 书名
    private String author;              // 作者
    private String publisher;           // 出版社
    private LocalDate publishDate;      // 出版日期
    private String category;            // 分类
    private BigDecimal price;           // 价格
    private Integer stock;              // 库存数量
    private String description;         // 图书简介
    private String coverImage;          // 封面图片URL
    private Integer status;             // 状态：0-下架，1-上架
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime updateTime;   // 更新时间
}
