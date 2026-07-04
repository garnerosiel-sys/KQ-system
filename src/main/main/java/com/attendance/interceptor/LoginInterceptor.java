package com.attendance.interceptor;

import com.attendance.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 登录权限拦截器
 * <p>
 * 拦截所有请求（除白名单路径外），验证请求头中的 JWT Token 是否有效。
 * 如果 Token 无效或缺失，返回 HTTP 401 状态码和标准 JSON 错误信息。
 * </p>
 *
 * <h3>白名单路径</h3>
 * 以下路径无需 Token 验证：
 * <ul>
 *   <li>{@code /api/login}  — 登录接口</li>
 *   <li>{@code /api/register} — 注册接口</li>
 * </ul>
 *
 * <h3>Token 传递方式</h3>
 * 客户端需在 HTTP 请求头中携带 Token：
 * <pre>
 *   Authorization: Bearer &lt;token&gt;
 * </pre>
 * 也支持直接传递原始 Token（无 Bearer 前缀）。
 *
 * @author KQ-system
 */
@Component("loginInterceptor")
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    /** Token 请求头名称 */
    private static final String HEADER_AUTHORIZATION = "Authorization";

    /** Bearer Token 前缀 */
    private static final String TOKEN_PREFIX = "Bearer ";

    /** 请求属性键：当前登录用户 ID */
    public static final String REQUEST_ATTR_USER_ID = "currentUserId";

    /** 请求属性键：当前登录用户名 */
    public static final String REQUEST_ATTR_USERNAME = "currentUsername";

    /**
     * 白名单路径集合 —— 这些路径无需 Token 验证
     * <p>
     * 可根据业务需要在此处添加更多路径（如验证码接口、密码重置等）。
     * 支持精确匹配。
     * </p>
     */
    private static final Set<String> WHITE_LIST = new HashSet<>(Arrays.asList(
            "/api/login",
            "/api/register"
    ));

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // ---- 1. CORS 预检请求直接放行 ----
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // ---- 2. 白名单路径直接放行 ----
        String requestURI = request.getRequestURI();
        if (isWhiteListed(requestURI)) {
            log.debug("白名单路径放行: {}", requestURI);
            return true;
        }

        // ---- 3. 获取 Token ----
        String token = extractToken(request);
        if (token == null || token.isEmpty()) {
            log.warn("请求缺少 Token: {} {}", request.getMethod(), requestURI);
            writeUnauthorizedResponse(response, "Token 缺失，请先登录");
            return false;
        }

        // ---- 4. 验证 Token ----
        if (!JwtUtil.validateToken(token)) {
            log.warn("Token 验证失败: {} {}", request.getMethod(), requestURI);
            writeUnauthorizedResponse(response, "Token 无效或已过期，请重新登录");
            return false;
        }

        // ---- 5. Token 有效，将用户信息存入请求属性 ----
        try {
            request.setAttribute(REQUEST_ATTR_USER_ID, JwtUtil.getUserIdFromToken(token));
            request.setAttribute(REQUEST_ATTR_USERNAME, JwtUtil.getUsernameFromToken(token));
        } catch (Exception e) {
            log.error("从 Token 提取用户信息失败", e);
            writeUnauthorizedResponse(response, "Token 解析失败");
            return false;
        }

        log.debug("Token 验证通过, userId={}, username={}",
                request.getAttribute(REQUEST_ATTR_USER_ID),
                request.getAttribute(REQUEST_ATTR_USERNAME));
        return true;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 判断请求路径是否在白名单中
     */
    private boolean isWhiteListed(String requestURI) {
        for (String path : WHITE_LIST) {
            if (requestURI.equals(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从请求头中提取 JWT Token
     * <p>
     * 优先处理带 "Bearer " 前缀的标准格式，
     * 若没有该前缀，则直接使用原始值作为 Token。
     * </p>
     *
     * @param request HTTP 请求
     * @return Token 字符串，若请求头中没有则返回 null
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HEADER_AUTHORIZATION);
        if (authHeader == null || authHeader.isEmpty()) {
            return null;
        }

        if (authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return authHeader;
    }

    /**
     * 向客户端写入 401 未授权响应
     *
     * @param response HTTP 响应对象
     * @param message  错误提示消息
     * @throws IOException 写入响应时可能发生的 IO 异常
     */
    private void writeUnauthorizedResponse(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());           // 401
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 手动构造 JSON，避免额外依赖
        String json = String.format("{\"code\":401,\"message\":\"%s\"}", escapeJson(message));

        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }

    /**
     * 对 JSON 字符串中的特殊字符进行转义
     * <p>
     * 防止消息中包含双引号或反斜杠等破坏 JSON 结构。
     * </p>
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
