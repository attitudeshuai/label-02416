package com.library.mapper;

import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图书数据访问接口
 * SQL映射见 resources/mapper/BookMapper.xml
 */
@Mapper
public interface BookMapper {

    Book findById(@Param("id") Long id);

    int insert(Book book);

    int update(Book book);

    int deleteById(@Param("id") Long id);

    List<Book> findByCondition(@Param("title") String title,
                               @Param("author") String author,
                               @Param("category") String category,
                               @Param("isbn") String isbn,
                               @Param("keyword") String keyword,
                               @Param("offset") Integer offset,
                               @Param("limit") Integer limit);

    Long countByCondition(@Param("title") String title,
                          @Param("author") String author,
                          @Param("category") String category,
                          @Param("isbn") String isbn,
                          @Param("keyword") String keyword);
    Long countByCategory(@Param("category") String category);

    List<String> findAllCategories();

    int decreaseStock(@Param("id") Long id);

    int increaseStock(@Param("id") Long id);

    Long count();

    List<Book> findLatest();
}
