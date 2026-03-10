package com.library.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 图书查询请求DTO
 */
@Data
public class BookQueryRequest {
    private String title;
    private String author;
    private String category;
    private String isbn;
    private String keyword;

    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能超过100")
    private Integer pageSize = 10;
}
