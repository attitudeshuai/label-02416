package com.library.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 分类实体类
 * 对应数据库category表
 */
@Data
public class Category {
    private Long id;
    private String name;
    private String description;
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
