package com.library.controller;

import com.library.common.Result;
import com.library.mapper.BookMapper;
import com.library.mapper.UserMapper;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘控制器
 * 提供系统统计数据接口
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BorrowService borrowService;

    /**
     * 获取系统统计数据
     * @return 统计数据（图书总数、用户总数、借阅数量、分类统计）
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", bookMapper.count());      // 图书总数
        stats.put("totalUsers", userMapper.count());      // 用户总数
        stats.put("borrowedBooks", borrowService.getBorrowedCount()); // 借阅中数量
        stats.put("categoryStats", borrowService.getCategoryStats()); // 分类统计
        return Result.success(stats);
    }
}
