package com.library.controller;

import com.library.common.ForbiddenException;
import com.library.common.Result;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 借阅控制器
 * 处理图书借阅和归还请求，userId从JWT令牌中提取
 */
@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    /**
     * 借阅图书 - userId从token中获取，防止伪造身份
     */
    @PostMapping("/borrow")
    public Result<BorrowRecord> borrowBook(@RequestParam Long bookId, HttpServletRequest httpReq) {
        Long userId = (Long) httpReq.getAttribute("userId");
        BorrowRecord record = borrowService.borrowBook(userId, bookId);
        return Result.success(record);
    }

    /**
     * 归还图书 - userId从token中获取，防止伪造身份
     */
    @PostMapping("/return")
    public Result<BorrowRecord> returnBook(@RequestParam Long bookId, HttpServletRequest httpReq) {
        Long userId = (Long) httpReq.getAttribute("userId");
        BorrowRecord record = borrowService.returnBook(userId, bookId);
        return Result.success(record);
    }

    /**
     * 获取当前用户的借阅记录
     */
    @GetMapping("/my")
    public Result<List<BorrowRecord>> getMyRecords(HttpServletRequest httpReq) {
        Long userId = (Long) httpReq.getAttribute("userId");
        return Result.success(borrowService.getUserBorrowRecords(userId));
    }

    /**
     * 获取所有借阅记录（仅管理员）
     */
    @GetMapping("/all")
    public Result<List<BorrowRecord>> getAllRecords(HttpServletRequest httpReq) {
        Integer role = (Integer) httpReq.getAttribute("role");
        if (role == null || role != 1) {
            throw new ForbiddenException("无权限访问");
        }
        return Result.success(borrowService.getAllBorrowRecords());
    }

    /**
     * 获取分类借阅统计
     */
    @GetMapping("/stats/category")
    public Result<List<Map<String, Object>>> getCategoryStats() {
        return Result.success(borrowService.getCategoryStats());
    }
}
