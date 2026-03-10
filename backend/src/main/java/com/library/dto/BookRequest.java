package com.library.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 图书新增/更新请求DTO
 */
@Data
public class BookRequest {
    @NotBlank(message = "书名不能为空")
    @Size(max = 200, message = "书名不能超过200个字符")
    private String title;

    @Size(max = 20, message = "ISBN不能超过20个字符")
    private String isbn;

    @Size(max = 100, message = "作者不能超过100个字符")
    private String author;

    @Size(max = 100, message = "出版社不能超过100个字符")
    private String publisher;

    private LocalDate publishDate;

    @Size(max = 50, message = "分类不能超过50个字符")
    private String category;

    @DecimalMin(value = "0.00", message = "价格不能为负数")
    @Digits(integer = 8, fraction = 2, message = "价格格式不正确")
    private BigDecimal price;

    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    private String description;

    @Size(max = 255, message = "封面图片URL不能超过255个字符")
    private String coverImage;
}
