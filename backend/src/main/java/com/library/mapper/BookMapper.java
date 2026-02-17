package com.library.mapper;

import com.library.entity.Book;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 图书数据访问接口
 * 定义图书相关的数据库操作
 */
@Mapper
public interface BookMapper {

    /**
     * 根据ID查询图书
     */
    @Select("SELECT * FROM book WHERE id = #{id}")
    Book findById(Long id);

    /**
     * 插入新图书
     */
    @Insert("INSERT INTO book (isbn, title, author, publisher, publish_date, category, price, stock, description, cover_image, status) " +
            "VALUES (#{isbn}, #{title}, #{author}, #{publisher}, #{publishDate}, #{category}, #{price}, #{stock}, #{description}, #{coverImage}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Book book);

    /**
     * 更新图书信息
     */
    @Update("UPDATE book SET isbn = #{isbn}, title = #{title}, author = #{author}, publisher = #{publisher}, " +
            "publish_date = #{publishDate}, category = #{category}, price = #{price}, stock = #{stock}, " +
            "description = #{description}, cover_image = #{coverImage}, status = #{status} WHERE id = #{id}")
    int update(Book book);

    /**
     * 根据ID删除图书
     */
    @Delete("DELETE FROM book WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 条件查询图书列表（分页）
     */
    List<Book> findByCondition(@Param("title") String title, 
                               @Param("author") String author, 
                               @Param("category") String category,
                               @Param("isbn") String isbn,
                               @Param("keyword") String keyword,
                               @Param("offset") Integer offset, 
                               @Param("limit") Integer limit);

    /**
     * 条件查询图书总数
     */
    Long countByCondition(@Param("title") String title, 
                          @Param("author") String author, 
                          @Param("category") String category,
                          @Param("isbn") String isbn,
                          @Param("keyword") String keyword);

    /**
     * 获取所有图书分类
     */
    @Select("SELECT DISTINCT category FROM book WHERE category IS NOT NULL")
    List<String> findAllCategories();

    /**
     * 减少库存（借阅时调用）
     */
    @Update("UPDATE book SET stock = stock - 1 WHERE id = #{id} AND stock > 0")
    int decreaseStock(Long id);

    /**
     * 增加库存（归还时调用）
     */
    @Update("UPDATE book SET stock = stock + 1 WHERE id = #{id}")
    int increaseStock(Long id);

    /**
     * 统计图书总数
     */
    @Select("SELECT COUNT(*) FROM book")
    Long count();

    /**
     * 获取最新上架的5本图书
     */
    @Select("SELECT * FROM book ORDER BY create_time DESC LIMIT 5")
    List<Book> findLatest();
}
