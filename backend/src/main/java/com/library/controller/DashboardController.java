package com.library.controller;

import com.library.common.Result;
import com.library.mapper.BookMapper;
import com.library.mapper.UserMapper;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘控制器
 * 根据用户角色返回不同的统计数据
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

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", bookMapper.count());
        stats.put("borrowedBooks", borrowService.getBorrowedCount());
        stats.put("categoryStats", borrowService.getCategoryStats());

        // 仅管理员可见用户总数
        if (role != null && role == 1) {
            stats.put("totalUsers", userMapper.count());
        }

        return Result.success(stats);
    }
}
