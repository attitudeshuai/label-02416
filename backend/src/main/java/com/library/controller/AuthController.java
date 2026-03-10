package com.library.controller;

import com.library.common.Result;
import com.library.dto.LoginRequest;
import com.library.dto.RegisterRequest;
import com.library.entity.User;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录和注册请求
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param request 登录请求（用户名和密码）
     * @return 登录结果（包含token和用户信息）
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> result = userService.login(request);
        return Result.success(result);
    }

    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        user.setPassword(null); // 隐藏密码
        return Result.success(user);
    }
}
