package com.library.dto;

import lombok.Data;

/**
 * Book Query Request DTO
 */
@Data
public class BookQueryRequest {
    private String title;
    private String author;
    private String category;
    private String isbn;
    private String keyword; // For multi-condition search
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
