package com.attendance.aspect;

import com.attendance.annotation.RequireRole;
import com.attendance.entity.User;
import com.attendance.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 角色权限切面
 * <p>
 * 拦截带有 {@link RequireRole} 注解的Controller方法，
 * 检查当前登录用户的角色是否满足要求。
 * </p>
 *
 * <h3>工作原理</h3>
 * <ol>
 *   <li>从HttpServletRequest中获取当前登录用户的ID</li>
 *   <li>查询数据库获取用户角色</li>
 *   <li>检查用户角色是否在允许的角色列表中</li>
 *   <li>如果不满足，抛出AccessException</li>
 * </ol>
 *
 * @author KQ-system
 */
@Aspect
@Component
public class RoleAspect {

    private static final Logger log = LoggerFactory.getLogger(RoleAspect.class);

    @Autowired
    private UserService userService;

    /**
     * 拦截带 @RequireRole 注解的方法
     *
     * @param joinPoint 切点
     * @return 方法执行结果
     * @throws Throwable 方法执行异常或权限不足异常
     */
    @Around("@annotation(com.attendance.annotation.RequireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前HTTP请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("无法获取HTTP请求上下文");
        }

        HttpServletRequest request = attributes.getRequest();

        // 获取当前登录用户ID（由LoginInterceptor设置）
        Object userIdObj = request.getAttribute("currentUserId");
        if (userIdObj == null) {
            log.warn("访问受限接口但用户未登录，URI: {}", request.getRequestURI());
            throw new RuntimeException("未登录，请先登录");
        }

        Integer userId = userIdObj instanceof Integer ? (Integer) userIdObj : ((Number) userIdObj).intValue();

        // 获取方法上的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireRole requireRole = method.getAnnotation(RequireRole.class);

        if (requireRole == null) {
            // 方法上没有注解，检查类上是否有
            requireRole = method.getDeclaringClass().getAnnotation(RequireRole.class);
        }

        // 查询用户信息
        User user = userService.getUserById(userId);
        if (user == null) {
            log.warn("用户不存在，userId: {}", userId);
            throw new RuntimeException("用户不存在");
        }

        String userRole = user.getRole();
        String[] allowedRoles = requireRole.value();
        String message = requireRole.message();

        // 检查角色权限（OR关系）
        boolean hasPermission = false;
        for (String allowedRole : allowedRoles) {
            if (allowedRole.equals(userRole)) {
                hasPermission = true;
                break;
            }
        }

        if (!hasPermission) {
            log.warn("权限不足: userId={}, role={}, uri={}, requiredRoles={}",
                    userId, userRole, request.getRequestURI(), String.join(",", allowedRoles));
            throw new com.attendance.exception.AccessException(message);
        }

        log.debug("权限检查通过: userId={}, role={}, uri={}",
                userId, userRole, request.getRequestURI());

        // 权限通过，执行原方法
        return joinPoint.proceed();
    }
}