package com.library.service;

import com.library.common.BusinessException;
import com.library.dto.LoginRequest;
import com.library.dto.RegisterRequest;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务
 * 处理用户登录、注册等业务逻辑
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, Object> login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            log.warn("登录失败 - 用户不存在: username={}", request.getUsername());
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("登录失败 - 密码错误: username={}", request.getUsername());
            throw new BusinessException("密码错误");
        }
        if (user.getStatus() != 1) {
            log.warn("登录失败 - 账号已禁用: username={}, userId={}", request.getUsername(), user.getId());
            throw new BusinessException("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        log.info("登录成功: username={}, userId={}, role={}", user.getUsername(), user.getId(), user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("role", user.getRole());
        return result;
    }

    public User register(RegisterRequest request) {
        User existingUser = userMapper.findByUsername(request.getUsername());
        if (existingUser != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(0);
        user.setStatus(1);

        userMapper.insert(user);
        return user;
    }

    public User getUserById(Long id) {
        return userMapper.findById(id);
    }

    public User updateUser(User user) {
        userMapper.update(user);
        return userMapper.findById(user.getId());
    }
}
