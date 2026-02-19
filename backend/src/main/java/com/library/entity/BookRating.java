package com.library.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 图书评分实体类
 */
@Data
public class BookRating {
    private Long id;
    private Long bookId;
    private Long userId;
    private Integer rating;
    private String comment;
    private LocalDateTime createTime;
}
