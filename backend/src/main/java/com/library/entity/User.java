package com.library.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库user表
 */
@Data
public class User {
    private Long id;                    // 用户ID
    private String username;            // 用户名
    private String password;            // 密码
    private String nickname;            // 昵称
    private String email;               // 邮箱
    private String phone;               // 手机号
    private String avatar;              // 头像URL
    private Integer role;               // 角色：0-普通用户，1-管理员
    private Integer status;             // 状态：0-禁用，1-启用
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime updateTime;   // 更新时间
}
