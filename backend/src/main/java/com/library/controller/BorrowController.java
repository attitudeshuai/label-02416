package com.library.controller;

import com.library.common.Result;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 借阅控制器
 * 处理图书借阅和归还请求
 */
@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    /**
     * 借阅图书
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 借阅记录
     */
    @PostMapping("/borrow")
    public Result<BorrowRecord> borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        try {
            BorrowRecord record = borrowService.borrowBook(userId, bookId);
            return Result.success(record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 归还图书
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 更新后的借阅记录
     */
    @PostMapping("/return")
    public Result<BorrowRecord> returnBook(@RequestParam Long userId, @RequestParam Long bookId) {
        try {
            BorrowRecord record = borrowService.returnBook(userId, bookId);
            return Result.success(record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户的借阅记录
     * @param userId 用户ID
     * @return 借阅记录列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<BorrowRecord>> getUserRecords(@PathVariable Long userId) {
        return Result.success(borrowService.getUserBorrowRecords(userId));
    }

    /**
     * 获取所有借阅记录（管理员用）
     * @return 所有借阅记录
     */
    @GetMapping("/all")
    public Result<List<BorrowRecord>> getAllRecords() {
        return Result.success(borrowService.getAllBorrowRecords());
    }

    /**
     * 获取分类借阅统计
     * @return 分类统计数据
     */
    @GetMapping("/stats/category")
    public Result<List<Map<String, Object>>> getCategoryStats() {
        return Result.success(borrowService.getCategoryStats());
    }
}
