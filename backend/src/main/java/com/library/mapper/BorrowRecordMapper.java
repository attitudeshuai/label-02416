package com.library.mapper;

import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 借阅记录数据访问接口
 * SQL映射见 resources/mapper/BorrowRecordMapper.xml
 */
@Mapper
public interface BorrowRecordMapper {

    int insert(BorrowRecord record);

    int update(BorrowRecord record);

    List<BorrowRecord> findByUserId(@Param("userId") Long userId);

    List<BorrowRecord> findAll();

    BorrowRecord findActiveBorrow(@Param("userId") Long userId, @Param("bookId") Long bookId);

    Long countBorrowed();

    int countActiveBorrowByBookId(@Param("bookId") Long bookId);

    List<Map<String, Object>> countBorrowByCategory();

    List<Map<String, Object>> countByCategory();
}
