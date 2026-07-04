package com.attendance.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * <p>
 * 提供 Token 的生成与解析功能，用于系统用户身份认证。
 * 基于 jjwt 库实现。
 * </p>
 *
 * @author KQ-system
 */
public class JwtUtil {

    /** JWT 签名密钥（应与 application.properties 中的 jwt.secret 保持一致） */
    private static final String SECRET = "KqSystemAttendance2024SecretKey!@#";

    /** Token 默认有效时长：24小时（单位：毫秒） */
    private static final long EXPIRATION = 24 * 60 * 60 * 1000L;

    /** Token 中用户ID的键名 */
    private static final String CLAIM_KEY_USER_ID = "userId";

    /** Token 中用户名的键名 */
    private static final String CLAIM_KEY_USERNAME = "username";

    // ==================== 生成 Token ====================

    /**
     * 根据用户ID和用户名生成 JWT Token
     * <p>
     * Token 中包含以下自定义声明：
     * <ul>
     *   <li>userId - 用户ID</li>
     *   <li>username - 用户名</li>
     * </ul>
     * </p>
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return 生成的 JWT Token 字符串
     */
    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userId);
        claims.put(CLAIM_KEY_USERNAME, username);

        // ✅ 先生成，存到变量
        String token = Jwts.builder()
                .setClaims(claims)                              // 设置自定义声明
                .setSubject(username)                           // 设置主题（用户名）
                .setIssuedAt(new Date())                        // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // 设置过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET)     // 使用 HS256 算法签名
                .compact();

        // ✅ 打印日志
        System.out.println("========== 生成的Token (2参数) ===========");
        System.out.println("Token: " + token);
        System.out.println("Token长度: " + token.length());
        System.out.println("Token分段数: " + token.split("\\.").length + "段");
        System.out.println("==========================================");

        return token;
    }

    /**
     * 根据用户ID和用户名生成 JWT Token（支持自定义过期时间）
     *
     * @param userId       用户ID
     * @param username     用户名
     * @param expirationMs 过期时间（毫秒）
     * @return 生成的 JWT Token 字符串
     */
    public static String generateToken(Long userId, String username, long expirationMs) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userId);
        claims.put(CLAIM_KEY_USERNAME, username);

        // ✅ 先生成，存到变量
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();

        // ✅ 打印日志
        System.out.println("========== 生成的Token (3参数) ===========");
        System.out.println("Token: " + token);
        System.out.println("Token长度: " + token.length());
        System.out.println("Token分段数: " + token.split("\\.").length + "段");
        System.out.println("==========================================");

        return token;
    }

    // ==================== 解析 Token ====================

    /**
     * 解析 JWT Token，返回声明内容
     * <p>
     * 如果 Token 无效或已过期，将抛出异常。
     * </p>
     *
     * @param token JWT Token 字符串
     * @return Claims 对象，包含所有声明信息
     * @throws io.jsonwebtoken.ExpiredJwtException    Token 已过期
     * @throws io.jsonwebtoken.UnsupportedJwtException Token 不支持
     * @throws io.jsonwebtoken.MalformedJwtException  Token 格式错误
     * @throws io.jsonwebtoken.SignatureException     签名验证失败
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)  // 设置签名密钥
                .parseClaimsJws(token)  // 解析 Token
                .getBody();             // 获取声明内容
    }

    // ==================== Token 验证 ====================

    /**
     * 验证 Token 是否有效
     * <p>
     * 有效的条件：格式正确、签名匹配、未过期。
     * </p>
     *
     * @param token JWT Token 字符串
     * @return true=有效，false=无效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            // ✅ 打印具体异常，方便排查
            System.out.println("========== Token验证失败 ==========");
            System.out.println("Token: " + token);
            System.out.println("异常类型: " + e.getClass().getSimpleName());
            System.out.println("异常信息: " + e.getMessage());
            e.printStackTrace();  // 打印完整堆栈
            System.out.println("====================================");
            return false;
        }
    }

    /**
     * 判断 Token 是否已过期
     *
     * @param token JWT Token 字符串
     * @return true=已过期，false=未过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // 已过期异常，直接返回 true
            return true;
        } catch (Exception e) {
            // 其他异常（格式错误等），视为已过期
            return true;
        }
    }

    // ==================== 从 Token 中提取信息 ====================

    /**
     * 从 Token 中获取用户ID
     *
     * @param token JWT Token 字符串
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        // Integer 是 JSON 反序列化数字的默认类型，需要兼容处理
        Object userIdObj = claims.get(CLAIM_KEY_USER_ID);
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        }
        return (Long) userIdObj;
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token JWT Token 字符串
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get(CLAIM_KEY_USERNAME, String.class);
    }

    /**
     * 从 Token 中获取主题（即用户名）
     *
     * @param token JWT Token 字符串
     * @return 主题/用户名
     */
    public static String getSubjectFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }
}