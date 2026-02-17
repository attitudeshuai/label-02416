package com.library.service;

import com.library.entity.BorrowRecord;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 借阅服务
 * 处理图书借阅和归还的业务逻辑
 */
@Service
public class BorrowService {

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private BookMapper bookMapper;

    /**
     * 借阅图书
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 借阅记录
     */
    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookId) {
        // 检查是否已借阅该书
        BorrowRecord existing = borrowRecordMapper.findActiveBorrow(userId, bookId);
        if (existing != null) {
            throw new RuntimeException("您已借阅过这本书，请先归还");
        }

        // 减少库存
        int updated = bookMapper.decreaseStock(bookId);
        if (updated == 0) {
            throw new RuntimeException("该图书库存不足");
        }

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(30)); // 借阅期限30天
        record.setStatus(0); // 0-借阅中

        borrowRecordMapper.insert(record);
        return record;
    }

    /**
     * 归还图书
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord returnBook(Long userId, Long bookId) {
        BorrowRecord record = borrowRecordMapper.findActiveBorrow(userId, bookId);
        if (record == null) {
            throw new RuntimeException("未找到有效的借阅记录");
        }

        // 增加库存
        bookMapper.increaseStock(bookId);

        // 更新借阅记录
        record.setReturnDate(LocalDateTime.now());
        record.setStatus(1); // 1-已归还
        borrowRecordMapper.update(record);

        return record;
    }

    /**
     * 获取用户的借阅记录
     * @param userId 用户ID
     * @return 借阅记录列表
     */
    public List<BorrowRecord> getUserBorrowRecords(Long userId) {
        return borrowRecordMapper.findByUserId(userId);
    }

    /**
     * 获取所有借阅记录（管理员用）
     * @return 所有借阅记录
     */
    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordMapper.findAll();
    }

    /**
     * 获取当前借阅中的图书数量
     * @return 借阅数量
     */
    public Long getBorrowedCount() {
        return borrowRecordMapper.countBorrowed();
    }

    /**
     * 获取分类统计数据
     * @return 各分类的借阅统计
     */
    public List<Map<String, Object>> getCategoryStats() {
        return borrowRecordMapper.countByCategory();
    }
}
