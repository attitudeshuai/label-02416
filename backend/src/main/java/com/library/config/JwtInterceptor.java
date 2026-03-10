package com.library.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.common.Result;
import com.library.util.JwtUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT认证拦截器
 * 校验请求中的JWT令牌，将用户信息存入request属性
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("越权访问 - 缺少token: uri={}, ip={}", request.getRequestURI(), request.getRemoteAddr());
            writeErrorResponse(response, 401, "未登录或token已过期");
            return false;
        }

        try {
            String token = authHeader.substring(7);
            DecodedJWT decoded = jwtUtil.verifyToken(token);
            request.setAttribute("userId", decoded.getClaim("userId").asLong());
            request.setAttribute("username", decoded.getClaim("username").asString());
            request.setAttribute("role", decoded.getClaim("role").asInt());
            return true;
        } catch (Exception e) {
            log.warn("越权访问 - token无效: uri={}, ip={}, error={}", request.getRequestURI(), request.getRemoteAddr(), e.getMessage());
            writeErrorResponse(response, 401, "token无效或已过期");
            return false;
        }
    }

    /**
     * 使用 Result 统一格式写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(status, message)));
    }
}
