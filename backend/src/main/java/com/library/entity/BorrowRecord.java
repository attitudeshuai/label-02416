package com.library.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 借阅记录实体类
 * 对应数据库borrow_record表
 */
@Data
public class BorrowRecord {
    private Long id;                    // 记录ID
    private Long userId;                // 用户ID
    private Long bookId;                // 图书ID
    private LocalDateTime borrowDate;   // 借阅日期
    private LocalDateTime dueDate;      // 应还日期
    private LocalDateTime returnDate;   // 实际归还日期
    private Integer status;             // 状态：0-借阅中，1-已归还，2-已逾期
    private LocalDateTime createTime;   // 创建时间
    
    // 扩展字段，用于页面展示
    private String bookTitle;           // 图书名称
    private String username;            // 用户名
}
