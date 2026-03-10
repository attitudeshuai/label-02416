package com.library.mapper;

import com.library.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类数据访问接口
 * SQL映射见 resources/mapper/CategoryMapper.xml
 */
@Mapper
public interface CategoryMapper {

    List<Category> findAll();

    Category findById(@Param("id") Long id);

    Category findByName(@Param("name") String name);

    int insert(Category category);

    int update(Category category);

    int deleteById(@Param("id") Long id);
}
