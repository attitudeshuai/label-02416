package com.library.mapper;

import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

/**
 * Borrow Record Mapper Interface
 */
@Mapper
public interface BorrowRecordMapper {

    @Insert("INSERT INTO borrow_record (user_id, book_id, borrow_date, due_date, status) " +
            "VALUES (#{userId}, #{bookId}, #{borrowDate}, #{dueDate}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BorrowRecord record);

    @Update("UPDATE borrow_record SET return_date = #{returnDate}, status = #{status} WHERE id = #{id}")
    int update(BorrowRecord record);

    @Select("SELECT br.*, b.title as book_title, u.username " +
            "FROM borrow_record br " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "LEFT JOIN sys_user u ON br.user_id = u.id " +
            "WHERE br.user_id = #{userId} ORDER BY br.create_time DESC")
    List<BorrowRecord> findByUserId(Long userId);

    @Select("SELECT br.*, b.title as book_title, u.username " +
            "FROM borrow_record br " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "LEFT JOIN sys_user u ON br.user_id = u.id " +
            "ORDER BY br.create_time DESC")
    List<BorrowRecord> findAll();

    @Select("SELECT br.* FROM borrow_record br WHERE br.user_id = #{userId} AND br.book_id = #{bookId} AND br.status = 0")
    BorrowRecord findActiveBorrow(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Select("SELECT COUNT(*) FROM borrow_record WHERE status = 0")
    Long countBorrowed();

    @Select("SELECT COUNT(*) FROM borrow_record WHERE book_id = #{bookId} AND status = 0")
    int countActiveBorrowByBookId(Long bookId);

    @Select("SELECT b.category, COUNT(*) as count FROM borrow_record br " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "GROUP BY b.category")
    List<Map<String, Object>> countByCategory();
}
