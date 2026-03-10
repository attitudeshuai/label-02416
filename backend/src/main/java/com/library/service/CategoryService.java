package com.library.service;

import com.library.common.BusinessException;
import com.library.entity.Category;
import com.library.mapper.BookMapper;
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

    @Autowired
    private BookMapper bookMapper;

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
        Category category = categoryMapper.findById(id);
        if (category != null) {
            Long bookCount = bookMapper.countByCategory(category.getName());
            if (bookCount != null && bookCount > 0) {
                throw new BusinessException("该分类下存在" + bookCount + "本图书，无法删除");
            }
        }
        categoryMapper.deleteById(id);
    }
}
