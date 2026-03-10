package com.library.controller;

import com.library.common.ForbiddenException;
import com.library.common.Result;
import com.library.dto.CategoryRequest;
import com.library.entity.Category;
import com.library.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.findAll());
    }

    @PostMapping
    public Result<Category> add(@Valid @RequestBody CategoryRequest catReq, HttpServletRequest httpReq) {
        checkAdmin(httpReq);
        Category category = new Category();
        BeanUtils.copyProperties(catReq, category);
        return Result.success(categoryService.add(category));
    }

    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest catReq, HttpServletRequest httpReq) {
        checkAdmin(httpReq);
        Category category = new Category();
        BeanUtils.copyProperties(catReq, category);
        return Result.success(categoryService.update(id, category));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest httpReq) {
        checkAdmin(httpReq);
        categoryService.delete(id);
        return Result.success();
    }

    private void checkAdmin(HttpServletRequest httpReq) {
        Integer role = (Integer) httpReq.getAttribute("role");
        if (role == null || role != 1) {
            throw new ForbiddenException("无权限操作");
        }
    }
}
