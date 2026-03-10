package com.library.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 分类新增/更新请求DTO
 */
@Data
public class CategoryRequest {
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称不能超过50个字符")
    private String name;

    @Size(max = 200, message = "描述不能超过200个字符")
    private String description;

    @Min(value = 0, message = "排序值不能为负数")
    private Integer sort = 0;
}
