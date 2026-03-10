package com.library.service;

import com.library.common.BusinessException;
import com.library.entity.Category;
import com.library.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> findAll() {
        return categoryMapper.findAll();
    }

    public Category add(Category category) {
        Category existing = categoryMapper.findByName(category.getName());
        if (existing != null) {
            throw new BusinessException("分类名称已存在");
        }
        categoryMapper.insert(category);
        return category;
    }

    public Category update(Long id, Category category) {
        Category existing = categoryMapper.findByName(category.getName());
        if (existing != null && !existing.getId().equals(id)) {
            throw new BusinessException("分类名称已存在");
        }
        category.setId(id);
        categoryMapper.update(category);
        return categoryMapper.findById(id);
    }

    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}
