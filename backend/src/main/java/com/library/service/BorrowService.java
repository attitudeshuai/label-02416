package com.library.service;

import com.library.common.BusinessException;
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

    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookId) {
        BorrowRecord existing = borrowRecordMapper.findActiveBorrow(userId, bookId);
        if (existing != null) {
            throw new BusinessException("您已借阅过这本书，请先归还");
        }

        int updated = bookMapper.decreaseStock(bookId);
        if (updated == 0) {
            throw new BusinessException("该图书库存不足");
        }

        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(30));
        record.setStatus(0);

        borrowRecordMapper.insert(record);
        return record;
    }

    @Transactional
    public BorrowRecord returnBook(Long userId, Long bookId) {
        BorrowRecord record = borrowRecordMapper.findActiveBorrow(userId, bookId);
        if (record == null) {
            throw new BusinessException("未找到有效的借阅记录");
        }

        bookMapper.increaseStock(bookId);

        record.setReturnDate(LocalDateTime.now());
        record.setStatus(1);
        borrowRecordMapper.update(record);

        return record;
    }

    public List<BorrowRecord> getUserBorrowRecords(Long userId) {
        return borrowRecordMapper.findByUserId(userId);
    }

    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordMapper.findAll();
    }

    public Long getBorrowedCount() {
        return borrowRecordMapper.countBorrowed();
    }

    public List<Map<String, Object>> getCategoryStats() {
        return borrowRecordMapper.countBorrowByCategory();
    }
}
