package com.attendance.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色权限注解
 * <p>
 * 用于标注Controller方法，限制只有特定角色才能访问。
 * 支持多个角色（OR关系），只要满足其中一个即可。
 * </p>
 *
 * <h3>使用示例</h3>
 * <pre>
 * &#64;RequireRole("admin")
 * public Result deleteUser(...) { ... }
 *
 * &#64;RequireRole({"admin", "workstation"})
 * public Result approveRequest(...) { ... }
 * </pre>
 *
 * @author KQ-system
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /**
     * 允许访问的角色列表
     * <p>
     * 支持的角色：
     * <ul>
     *   <li>admin - 系统管理员</li>
     *   <li>user - 普通员工</li>
     *   <li>workstation - 工作台</li>
     * </ul>
     * </p>
     *
     * @return 角色名称数组
     */
    String[] value();

    /**
     * 权限不足时的错误提示消息
     * <p>
     * 默认为 "权限不足，拒绝访问"
     * </p>
     *
     * @return 错误消息
     */
    String message() default "权限不足，拒绝访问";
}